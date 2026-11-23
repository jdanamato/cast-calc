#!/usr/bin/env python3
"""
Compare Cast Calc's spell data with the Old School RuneScape Wiki.

Reads every spell in src/main/java/com/castcalc/SpellDatabase.java, fetches
the wiki's four spellbook navigation templates and each spell's infobox, and
reports:

  - spells in the game that the plugin is missing
  - spells in the plugin that the wiki no longer lists (removed or renamed)
  - spells whose level, Magic XP or rune cost differ

It only reads public wiki data through the MediaWiki API; nothing is written
anywhere. Run it locally with:

    python3 scripts/check_spells.py

Exit code 0 = everything matches, 1 = differences found (report printed as
Markdown), 2 = the wiki couldn't be read (network error, format change).
The weekly GitHub Action (.github/workflows/check-spells.yml) runs this and
opens an issue when it exits with 1.
"""

import json
import re
import sys
import time
import urllib.parse
import urllib.request
from pathlib import Path

API = "https://oldschool.runescape.wiki/api.php"
# The wiki asks automated clients to identify themselves.
USER_AGENT = "cast-calc-spell-check (+https://github.com/jdanamato/cast-calc)"

ROOT = Path(__file__).resolve().parent.parent
SPELL_DB = ROOT / "src/main/java/com/castcalc/SpellDatabase.java"
RUNE_ENUM = ROOT / "src/main/java/com/castcalc/Rune.java"

TEMPLATES = {
    "Template:Standard spellbook": "STANDARD",
    "Template:Ancient Magicks": "ANCIENT",
    "Template:Lunar spellbook": "LUNAR",
    "Template:Arceuus spellbook": "ARCEUUS",
}
WIKI_BOOK = {"Normal": "STANDARD", "Standard": "STANDARD", "Ancient": "ANCIENT",
             "Lunar": "LUNAR", "Arceuus": "ARCEUUS"}

# Wiki page title -> name used in SpellDatabase.java, where they differ.
ALIASES = {
    "Hunter Kit (spell)": "Hunter Kit",
    "Carrallanger Teleport": "Carrallangar Teleport",
    "Ape Atoll Teleport (standard)": "Teleport to Ape Atoll",
    "Ape Atoll Teleport (Arceuus)": "Ape Atoll Teleport (Arceuus)",
}
# Tele Group spells: the wiki writes "Tele Group X", the plugin "Telegroup X".
ALIAS_PATTERNS = [(re.compile(r"^Tele Group (.+)$"), r"Telegroup \1")]

# Spells left out on purpose. Keep the reason next to each one.
IGNORED = {
    # Free spells: no runes, so nothing for a cost calculator to show.
    "Lumbridge Home Teleport", "Edgeville Home Teleport", "Lunar Home Teleport",
    "Arceuus Home Teleport", "Minigame Teleport", "Teleport to Target",
    # Not one spell: wiki group pages the plugin models differently.
    "Jewellery Enchantments",
    # The plugin has one "Enchant Crossbow Bolt" entry (XP varies by bolt).
    "Enchant Crossbow Bolt (Opal)", "Enchant Crossbow Bolt (Sapphire)",
    "Enchant Crossbow Bolt (Jade)", "Enchant Crossbow Bolt (Pearl)",
    "Enchant Crossbow Bolt (Emerald)", "Enchant Crossbow Bolt (Red Topaz)",
    "Enchant Crossbow Bolt (Ruby)", "Enchant Crossbow Bolt (Diamond)",
    "Enchant Crossbow Bolt (Dragonstone)", "Enchant Crossbow Bolt (Onyx)",
}

# Plugin entries with no single wiki page, kept on purpose.
PLUGIN_ONLY = {
    # One entry for all bolt types (the wiki has a page per bolt).
    "Enchant Crossbow Bolt",
}

# Markers on a wiki page that mean "not a current, normal-game spell":
# removed/upcoming spells, and spells that only exist in Leagues.
NOT_CURRENT = re.compile(r"\{\{\s*(Gone|Removed|Future)\b|\|\s*update\s*=\s*Leagues", re.I)

# Rune names the plugin knows (from the Rune enum). Anything else in a wiki
# cost (a banana, an unpowered orb) is an item, not a rune, and is ignored.
RUNES = set(re.findall(r"^\s*([A-Z]+)\(", RUNE_ENUM.read_text(), re.M))


def api(params):
    params = dict(params, format="json", formatversion="2")
    url = API + "?" + urllib.parse.urlencode(params)
    req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    for attempt in range(3):
        try:
            with urllib.request.urlopen(req, timeout=30) as resp:
                return json.load(resp)
        except Exception:
            if attempt == 2:
                raise
            time.sleep(2 * (attempt + 1))


def template_links(title):
    data = api({"action": "parse", "page": title, "prop": "links"})
    return [l["title"] for l in data["parse"]["links"] if l["ns"] == 0 and l.get("exists", True)]


def page_texts(titles):
    """Wikitext for many pages, 50 per request (the API's limit)."""
    out = {}
    for i in range(0, len(titles), 50):
        chunk = titles[i:i + 50]
        data = api({"action": "query", "prop": "revisions", "rvprop": "content",
                    "rvslots": "main", "redirects": 1, "titles": "|".join(chunk)})
        redirects = {r["to"]: r["from"] for r in data["query"].get("redirects", [])}
        for page in data["query"]["pages"]:
            if "revisions" not in page:
                continue
            text = page["revisions"][0]["slots"]["main"]["content"]
            out[redirects.get(page["title"], page["title"])] = text
        time.sleep(0.5)
    return out


def infobox(text):
    m = re.search(r"\{\{Infobox Spell(.*?)\n\}\}", text, re.S)
    if not m:
        return None
    body = m.group(1)

    def field(key):
        f = re.search(r"\|\s*" + key + r"\s*=\s*([^\n]*)", body)
        return f.group(1).strip() if f else ""

    runes = {}
    cost = field("cost")
    req = re.search(r"\{\{RuneReq\|([^}]*)\}\}", cost)
    if req:
        for part in req.group(1).split("|"):
            if "=" in part:
                k, v = part.split("=", 1)
                k = k.strip().upper()
                if k in RUNES and v.strip().isdigit():
                    runes[k] = int(v.strip())
    return {"book": WIKI_BOOK.get(field("spellbook"), field("spellbook")),
            "level": field("level"), "xp": field("exp"), "runes": runes}


def plugin_spells():
    src = SPELL_DB.read_text()
    spells = {}
    pattern = re.compile(
        r'addSpell\(\s*"([^"]+)"\s*,\s*(\w+)\s*,\s*\w+\s*,\s*(\d+)\s*,\s*\w+\s*,\s*([\d.]+)\s*,'
        r'\s*XpMode\.\w+\s*,(.*?)\);')
    for name, book, level, xp, rest in pattern.findall(src):
        runes = {r: int(n) for r, n in re.findall(r"rune\((\w+),\s*(\d+)\)", rest)}
        spells[name] = {"book": book, "level": level, "xp": xp, "runes": runes}
    if not spells:
        raise RuntimeError("Couldn't read any spells from SpellDatabase.java")
    return spells


def plugin_name(wiki_title):
    if wiki_title in ALIASES:
        return ALIASES[wiki_title]
    for pat, repl in ALIAS_PATTERNS:
        if pat.match(wiki_title):
            return pat.sub(repl, wiki_title)
    return wiki_title


def same_number(a, b):
    try:
        return abs(float(a) - float(b)) < 1e-9
    except ValueError:
        return False


def main():
    ours = plugin_spells()
    try:
        titles = []
        for t in TEMPLATES:
            titles += template_links(t)
        titles = sorted(set(titles) - IGNORED)
        texts = page_texts(titles)
    except Exception as e:
        print(f"Couldn't read the wiki: {e}", file=sys.stderr)
        return 2

    wiki = {}
    for title, text in texts.items():
        box = infobox(text)
        if box is None or NOT_CURRENT.search(text[:3000]):
            continue  # not a spell page (category, thrall...) or not current
        if box["book"] not in TEMPLATES.values():
            continue
        if not box["runes"]:
            continue  # free spells (home teleports etc.): nothing to calculate
        wiki[plugin_name(title)] = dict(box, title=title)

    if len(wiki) < 100:
        print(f"Only found {len(wiki)} spells on the wiki; its format may have changed.",
              file=sys.stderr)
        return 2

    missing = sorted(n for n in wiki if n not in ours)
    gone = sorted(n for n in ours if n not in wiki and n not in PLUGIN_ONLY)
    changed = []
    for name in sorted(set(ours) & set(wiki)):
        o, w = ours[name], wiki[name]
        diffs = []
        if o["book"] != w["book"]:
            diffs.append(f"spellbook {o['book']} -> {w['book']}")
        if w["level"] and not same_number(o["level"], w["level"]):
            diffs.append(f"level {o['level']} -> {w['level']}")
        if w["xp"] and re.fullmatch(r"[\d.]+", w["xp"]) and not same_number(o["xp"], w["xp"]):
            diffs.append(f"XP {o['xp']} -> {w['xp']}")
        if w["runes"] and o["runes"] != w["runes"]:
            fmt = lambda r: ", ".join(f"{k.title()} x{v}" for k, v in sorted(r.items()))
            diffs.append(f"runes {fmt(o['runes'])} -> {fmt(w['runes'])}")
        if diffs:
            changed.append((name, diffs))

    if not (missing or gone or changed):
        print(f"All {len(ours)} spells match the wiki.")
        return 0

    page = lambda t: f"https://oldschool.runescape.wiki/w/{urllib.parse.quote(t.replace(' ', '_'))}"
    print("## Spell data differs from the OSRS Wiki\n")
    if missing:
        print(f"### On the wiki but not in the plugin ({len(missing)})\n")
        for n in missing:
            w = wiki[n]
            runes = ", ".join(f"{k.title()} x{v}" for k, v in sorted(w["runes"].items())) or "no runes"
            print(f"- [{w['title']}]({page(w['title'])}): {w['book'].title()}, "
                  f"level {w['level']}, {w['xp']} XP, {runes}")
        print()
    if gone:
        print(f"### In the plugin but not on the wiki ({len(gone)})\n")
        print("Removed from the game, renamed, or listed under another name.\n")
        for n in gone:
            print(f"- {n}")
        print()
    if changed:
        print(f"### Data differs ({len(changed)})\n")
        for n, diffs in changed:
            print(f"- [{n}]({page(wiki[n]['title'])}): " + "; ".join(diffs))
        print()
    print("To leave a spell out on purpose, add it to `IGNORED` in "
          "`scripts/check_spells.py` with the reason.")
    return 1


if __name__ == "__main__":
    sys.exit(main())

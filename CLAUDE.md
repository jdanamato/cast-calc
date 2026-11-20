# Cast Calc — Project Context

RuneLite plugin for Old School RuneScape (OSRS). Calculates live GP cost per
spell cast across all 4 spellbooks, detects rune-saving gear, runs profit/loss
math for utility spells, and includes a level/goal calculator. Built by Joe
(web designer/dev, not deeply fluent in Java) with Claude as coding partner —
Joe drives product/design/UX decisions, Claude implements and explains Java/
Swing/RuneLite specifics.

**Project root:** `/Users/joe/Developer/cast-calc`
**Stack:** Java 11, Gradle 8.10, RuneLite plugin SDK, Swing UI, IntelliJ IDEA, macOS.
**Run:** `cd ~/Developer/cast-calc && ./gradlew run` (use `./gradlew clean run` after big changes — Gradle can serve stale classes).
**Repo state:** Working directly on `master`, no remote-collaboration workflow —
Joe is the sole contributor. Repo has been cleaned up from the original RuneLite
`com.example` template (see "Repo hygiene" below); `build.gradle`,
`settings.gradle`, and `runelite-plugin.properties` all correctly reference
`cast-calc` / `com.castcalc` now.

---

## File map

```
src/main/java/com/castcalc/
├── CastCalcPlugin.java      — RuneLite plugin entry point; event subscriptions
├── CastCalcPanel.java       — the entire UI (side panel), ~1200+ lines
├── CastCalcStyle.java       — centralized "stylesheet": colors, fonts, spacing, layout constants
├── CastCalcConfig.java      — RuneLite config (user-facing settings, all default to sensible values)
├── CostCalculator.java      — rune cost math + price cache (client-thread safe) + RuneLineItem data class
├── ConversionPreset.java    — P&L conversion recipes (tablets, ores, hides, etc.)
├── SpellDatabase.java       — all ~181 spell definitions (runes, level, XP, category)
├── SpellData.java           — data class for one spell
├── SpellIcons.java          — spell name → RuneLite SpriteID mapping (icons)
├── XpMode.java              — enum: NORMAL / COMBAT / REANIMATE / VARIES
├── MagicXpTable.java        — OSRS XP table (levels 1-99), level<->XP conversion
├── CalcMode.java            — enum: CASTS / LEVEL / COST (level calculator modes)
├── Rune.java, RuneRequirement.java, RuneSavingGear.java — rune/gear model
├── Spellbook.java, SpellCategory.java — enums
└── (test) src/test/java/com/castcalc/CastCalcPluginTest.java — launches dev client
```

---

## Feature set (current state, all working)

1. **Spell list** — ~181 spells across Standard/Ancient/Lunar/Arceuus. Search, filter
   by spellbook/category, sort (Name, Cost, Level, Profit high/low). Click a row to
   expand inline detail (rune breakdown + P&L). **Multiple rows can be expanded
   simultaneously** (not accordion-style).
2. **Live GE pricing** — cached via `CostCalculator`, refreshed every 5 min or on
   manual "Refresh" click.
3. **Rune-saving gear detection** — reads equipped items (staves, Tome of Fire/Water,
   Kodai Wand, Bryophyta's staff), shows "Free runes: X, Y" chip at top, auto-updates
   on equipment change.
4. **Rune breakdown** (per expanded spell) — one line per rune, format:
   `[icon] Name x[qty]: [cost] gp`. Only the `x[qty]:` segment is muted
   (`TEXT_MUTED`); rune name and cost use normal/primary colors. Free runes (from
   gear) show `Free` in green. Partial-save runes show a muted `(NN% save)` suffix.
   No section header text (deliberately removed — see "UX decisions" below).
   Controlled by the `showRuneBreakdown` config toggle (**defaults to `true`**).
5. **Profit/Loss calculator** — per spell, shown as a line-item table (icon, item,
   cost) → divider → output value → profit (green/red). Covers: High/Low Alch (live
   item search), Tan Leather, Plank Make, Superheat, Spin Flax, Superglass Make,
   String Jewellery, all Reanimate spells (XP-only, no GP profit), and **all**
   tablet-making teleports (Standard, Ancient, Lunar — ~24 spells).
6. **Level/Goal Calculator** — toggle in header reveals:
   - Mode dropdown: **Casts** / **Level** / **Cost**
   - Casts mode: type a cast count → total GP cost + XP/level reached
   - Level mode: two synced fields (target level 1–99 ↔ target XP) → total GP cost
     + casts needed, computed from player's **live Magic level** (read from client)
   - Cost mode: GP budget (supports `1k`/`1.5m` shorthand) → casts affordable + XP/level reached
   - Non-calculable spells (combat, reanimate, "varies") are muted with a label
     instead of showing misleading numbers.
7. **Spell & item icons** — 20px icons throughout (spell rows via `SpriteManager`,
   P&L table items + rune breakdown items via `ItemManager`). Async-loaded, scaled
   after load (critical — see Pitfalls).

---

## Architecture decisions (why things are built this way)

### Threading model
RuneLite's `ItemManager`/`SpriteManager`/equipment reads require the **client
thread**; the Swing UI runs on the **Swing/EDT thread**. Mixing them crashes or
silently fails. Solution used throughout:
- `CostCalculator` holds a `ConcurrentHashMap` price cache, populated via
  `refreshPriceCache()` called from `clientThread.invokeLater()`.
- `RuneSavingGear` similarly caches equipment state, updated on
  `ItemContainerChanged` / `GameStateChanged` events (both fire on client thread).
- Panel reads only from these caches — never touches `ItemManager`/`client` directly
  from Swing code.
- Icon loading: `spriteManager.getSpriteAsync(id, 0, callback)` and
  `itemManager.getImage(id)` (returns `AsyncBufferedImage`, use `.onLoaded()`) are
  both async; callbacks must `SwingUtilities.invokeLater()` before touching Swing
  components.

### No nested JScrollPane
**Critical lesson (cost significant debugging time):** `PluginPanel` (RuneLite's
base class we extend) already wraps content in a scroll pane. Do **not** add
another `JScrollPane` anywhere in the panel — nested scroll panes cause broken/dead
mouse-wheel scrolling that's very hard to diagnose. All views (spell list, inline
details) are plain `BoxLayout.Y_AXIS` panels with no scroll wrapping of their own.

### Row-click vs child-click event bubbling
Swing sends mouse events to the deepest component under the cursor, not the
container. Since every row has a click listener (to expand/collapse) but also
contains child labels/icons, clicks on children were "swallowed" and didn't
trigger the row. Fixed with `forwardAllMouseEventsToRow(JComponent root)` — walks
the whole subtree recursively and attaches a forwarder that redispatches every
mouse event up to the parent. Call this once at the end of any row-building method
(and call `forwardMouseEventsToParent()` directly on any one-off icon/label added
after the fact, e.g. in `buildRuneLineItem()`).
There is no built-in Swing equivalent of CSS `pointer-events: none`; this pattern
is the workaround.

### Row layout: no HTML wrapping, no width prediction
Early attempts used `<html><body style='width:Npx'>` for text wrapping with
predicted pixel widths for columns — this **broke repeatedly** (text cut off,
cost column squeezed, layout races on panel resize/scrollbar-appearing). Final,
stable design: **single-line labels with truncation**, stacked vertically inside
each row (title / subtitle / secondary-line / cost — NOT side-by-side columns
with wrapping). No HTML, no manual width math. `configureTruncation()` sets
min/max height only, letting Swing truncate naturally. Tooltips show full text
on hover if truncated. Short inline rows (like rune line items: icon + name +
qty + cost, all short strings) use a plain `BoxLayout.X_AXIS` row of small
JLabels instead — no truncation needed since the content is short by construction.

### Inline expansion, not page navigation
Two earlier designs were tried and discarded:
1. Fixed detail panel below the whole list (scroll position wrong, detail invisible
   until scrolling to bottom).
2. `CardLayout` swap between a "list view" and a "detail view" (worked but felt
   heavy, scroll-position restoration was fragile).

**Final design:** clicking a row inserts a detail panel directly below that row
in the same `BoxLayout` list (`expandedSpells: Set<String>` tracks which spells
are expanded; `refreshSpellList()` inserts `buildSpellDetail()` right after each
expanded row). Multiple rows can be expanded at once. Expanded rows get a darker
background (`PANEL_EXPANDED`) that visually merges with their detail panel below.

### Style system
All colors/fonts/spacing/layout constants live in **`CastCalcStyle.java`** —
treat it as a CSS/design-tokens file. `CastCalcPanel.java` imports it via
`import static com.castcalc.CastCalcStyle.*;` and never hardcodes a color/size
that's used more than once. Key groups: `COLORS`, `TYPOGRAPHY` (with separate
`BASE_SIZE` / `DROPDOWN_FONT_SIZE` / `INPUT_FONT_SIZE` — these were unified from
inconsistent per-component fonts), `SPACING`, `LAYOUT` (`ICON_SIZE`,
`ICON_TEXT_GAP`, `COST_COLUMN_WIDTH`), `COMPONENTS` (border factories).

**Font gotcha:** `FontManager.getRunescapeSmallFont()` is a **bitmap font**,
pixel-locked — `.deriveFont(size)` silently ignores size changes. Switched to
`FontManager.getRunescapeFont()` / `getRunescapeBoldFont()` (TrueType, scale
correctly) for anything that needs a configurable size.

### Data model additions (level calculator)
`SpellData` carries `xpPerCast` (double) and `xpMode` (XpMode enum) in addition
to the original rune/level/category fields. `XpMode.COMBAT` name **collides**
with `SpellCategory.COMBAT` under wildcard static imports — fixed by removing
the `XpMode.*` wildcard import in `SpellDatabase.java` and fully qualifying all
`XpMode.NORMAL` / `XpMode.COMBAT` / etc. references there. If this file is
touched again, keep that qualification — re-adding the wildcard will break the
build with "reference to COMBAT is ambiguous" errors across ~50 lines.

### Rune breakdown data model (recent change)
`CostCalculator.getCostBreakdown()` (returns `Map<String, Long>` of pre-formatted
label strings) is now **legacy/unused** — kept only for backward compatibility,
nothing currently calls it. All rendering goes through the newer
`CostCalculator.getRuneLineItems()`, which returns `List<RuneLineItem>` — a
proper structured object (`rune`, `quantity`, `cost`, `free`, `savePercent`).
Prefer extending `RuneLineItem` / `getRuneLineItems()` over reviving the old
string-label approach if the rune breakdown needs new fields later (e.g. if a
"copy as text" feature is ever added).

---

## Known compile-time landmines (RuneLite API surface)

Several `ItemID`/`SpriteID` constants Claude *assumed* existed (by naming
convention) did not, and caused compile errors that had to be fixed reactively.
Documented in-code with comments; if adding new spells/items, expect some of
these to recur:

- `ItemID.CARRALLANGAR_TELEPORT` doesn't exist — it's `CARRALLANGER_TELEPORT`
  (an old in-game misspelling preserved in the constant name; the display name
  was later corrected but the Java constant wasn't renamed).
- Individual `SpriteID.SPELL_REANIMATE_<CREATURE>` constants don't exist — the
  18 individual reanimate spells were merged into 4 tiers in a 2021 game update;
  only `SPELL_BASIC/ADEPT/EXPERT/MASTER_REANIMATION` sprites exist. Mapped
  proportionally by level in `SpellIcons.java`.
- `SpriteID.SPELL_TELEGROUP_*` don't exist — Telegroup spells reuse their
  individual-teleport counterpart's sprite.
- `SpriteID.SPELL_KOUREND_CASTLE_TELEPORT` and `SPELL_BATTLEFRONT_TELEPORT`
  don't exist in the targeted RuneLite version — left uncommented/blank icon.
- Only `SpriteID.SPELL_RESURRECT_SUPERIOR_SKELETON` is confirmed to exist among
  the 9 Resurrect spell variants — all 9 fall back to that one sprite.
- `SpriteID.SPELL_VENGEANCE_GROUP` doesn't exist — reuses `SPELL_VENGEANCE`.

**General lesson:** when adding new RuneLite `ItemID`/`SpriteID` references,
expect ~15-20% to be wrong on first guess. Don't guess silently — build, read the
compiler's "cannot find symbol" list, and fix in a batch. Web search for the
specific constant name + "runelite" often finds the correct spelling via wiki
cross-references.

---

## Style/UX decisions made along the way

- Cost-per-cast on a spell row is **always neutral gold** (`TEXT_HIGHLIGHT`),
  never green/red — profit/loss color is reserved for actual profit/loss
  figures inside the P&L detail card, not the plain cost number.
- P&L card redesigned from a dense one-liner (`"1x Soft clay > 1x Tablet / In:
  119 - Out: 1,710 - Runes: 268 / +1,323 gp"`) to a vertical line-item table
  (icon | item | cost, one per line, divider, output, profit) — modeled after
  the OSRS Wiki's GE profit tables. Much clearer.
- **Rune breakdown redesigned** (most recent change): dropped the "Rune
  breakdown" section header text entirely — the rune icons make the section
  self-evident without a label. Each rune now reads as one natural sentence-like
  line: `[icon] Law x2: 240 gp`, with only the `x2:` portion muted. This mirrors
  the "no header, icon does the labeling" pattern already used for the P&L table.
- "P&L" label kept as-is (not renamed to "Profitable") because the category
  includes spells that can *lose* money too (e.g. teleport tablets at bad GE
  prices) — "Profitable" would be misleading.
- Bulk cost rows (100/1,000/10,000 casts) were **removed** from spell detail —
  redundant once cast-based calc mode existed; users can compute it themselves.
- Unicode arrows/symbols (↑ ↓ ▸ ⟳) don't render in RuneLite's UI font — replaced
  with ASCII equivalents (`(low)`/`(high)`, `->`, `Refresh`, etc.) everywhere.
- Icon size settled at **20px** (`ICON_SIZE` in stylesheet) with a 4px gap to text
  (`ICON_TEXT_GAP`). Both are single constants — change once, applies everywhere
  (spell rows, P&L table icons, rune breakdown icons).
- Row-level "pointer-events: none" pattern (`forwardAllMouseEventsToRow`) is the
  standard way any future clickable row should be built.

---

## Level Calculator — implementation notes for continuation

Stages 1 and 2 (data + full UI: toggle, mode dropdown, input fields, right-column
value swap, secondary line, muted non-calculable spells) are **done**. Design was
heavily spec'd via back-and-forth Q&A with Joe before building — see decisions below
if extending this feature:

- **Casts / Level / Cost are three input modes**, each with different secondary
  math shown on every spell row (right column = primary output for that mode,
  secondary line below subtitle = supporting info).
- **Level mode uses two synced text fields** (target level 1-99 AND target XP,
  either editable, they sync live via `DocumentListener` with a re-entrancy guard
  flag `updatingLevelFields`).
- **Current Magic level is read live from the client** (`client.getRealSkillLevel
  (Skill.MAGIC)`), pushed to the panel via `panel.setCurrentMagicLevel(int)` on
  login and on `StatChanged` events — never manually typed by the user.
- **Combat spells, Reanimate spells, and "Enchant Crossbow Bolt"** are muted
  (grayed, non-numeric label) in Level mode specifically, because their effective
  XP rate is either variable in practice (combat: hit vs. splash) or negligible/
  not a real training method (reanimate) or context-dependent (bolt enchant
  varies 9.5–150 XP by bolt type — not yet split into per-bolt-type entries,
  flagged `XpMode.VARIES`, shows "XP varies").
- **Shorthand parsing** (`500k`, `1.5m`) implemented in `parseShorthand()` for
  both the Casts and Cost input fields.
- `MagicXpTable` is a standalone reusable class — precomputes the standard OSRS
  XP curve at class-load time via the canonical formula, don't hand-roll another
  copy of this table elsewhere.

**Not yet built / explicitly deferred:**
- Per-bolt-type breakdown for Enchant Crossbow Bolt (currently just muted).
- Alch search result item icons still use the old unscaled pattern in one place
  — Joe deferred fixing this ("fix that later"), but the *correct* pattern
  (`onLoaded()` callback + `getScaledInstance()`) is already established
  elsewhere in the file (`buildItemIconLabel()`) and should be reused verbatim
  when this gets picked up.
- "Hide non-XP spells in calc mode" config option was discussed as a good idea
  but not implemented — current behavior always shows all spells (muted vs
  hidden), per Joe's explicit choice.

---

## Repo hygiene (recently resolved — for awareness only)

The repo was originally scaffolded from RuneLite's `com.example` template and
still had leftover boilerplate (`ExamplePlugin.java`, `ExampleConfig.java`,
`ExamplePluginTest.java`, `build.gradle`/`settings.gradle`/
`runelite-plugin.properties` all referencing `example`). This has been **fully
cleaned up**:
- `settings.gradle` → `rootProject.name = 'cast-calc'`
- `runelite-plugin.properties` → correct `displayName`, `author`, `description`,
  `tags`, and `plugins=com.castcalc.CastCalcPlugin`
- `build.gradle` modernized (removed shadowJar task and unused example-specific
  config, added `pf4j` dependency, correct `group`/`version`)
- All `com.example.*` files deleted
- README given section dividers for readability

If a fresh clone or a new session ever shows `com.example` references anywhere,
that's a regression — the cleanup above is the fix.

**On git workflow:** Joe works solo, directly on `master`, no PR-based workflow
for this repo. A "Create PR" button showing up in a git GUI tool is a red
herring for this project — it's for merging one branch into another, which
isn't Joe's workflow here. Only relevant later for the *actual* Plugin Hub
submission, which is a PR against `runelite/plugin-hub` (a different repo
entirely), not this repo.

---

## Working style notes for whoever picks this up

- Joe is not a Java expert — explain *why*, not just *what*, especially around
  Swing/threading quirks that aren't obvious from other languages/frameworks.
- Joe pushes back hard on hand-wavy fixes and will call it out if something is
  patched superficially without diagnosing the actual root cause (see the
  multi-message scroll-bug saga — the eventual real fix was architectural:
  removing a nested scroll pane — not another band-aid). Also seen with the
  "detail only shows price" report — turned out to be a config toggle, not a
  code bug, and diagnosis required reading the actual current file contents
  rather than guessing from the bug description alone.
- When something breaks after a change, **read back the actual current file
  state before re-editing** — several early bugs were caused by editing based on
  stale assumptions about what the file already contained.
- Prefer centralizing repeated values into `CastCalcStyle.java` proactively;
  Joe explicitly asked for this pattern ("stylesheet" mental model) and expects
  new visual constants to go there, not inline.
- When RuneLite API constants are uncertain, say so up front rather than
  presenting a guess as fact — Joe would rather get a compiler error to paste
  back than have silently-wrong data (e.g. wrong XP values, wrong sprite IDs).
- Break large features into explicit stages and ask clarifying questions before
  writing code when a feature has many open design branches (this was
  explicitly requested for the Level Calculator and worked well).
- If a "large diff" looks alarming, don't assume the worst — check what actually
  changed before treating it as a regression. In one case a 4,000+ line diff
  Joe flagged as suspicious turned out to be harmless repo-hygiene cleanup
  unrelated to the bug he was asking about.

---

## Immediate next steps / open threads (as of last message)

1. Level Calculator polish, if continuing: per-bolt Enchant Crossbow Bolt
   entries; "hide vs mute" config option; possibly a summary/aggregate view
   (total runes needed across all rune types to reach a goal).
2. Alch search result item icons still use the pre-fix unscaled pattern — low
   priority, deferred by Joe.
3. Consider whether `getCostBreakdown()` (the legacy string-map method in
   `CostCalculator`) should just be deleted now that nothing calls it, or left
   as-is for now — no urgency either way.
4. Plugin Hub submission not yet started (push to GitHub, fork
   `runelite/plugin-hub`, add marker file, open PR) — was explained early on but
   not executed; revisit once the plugin is feature-complete and polished.
5. A portfolio case-study write-up was already drafted (separate from this repo)
   describing the project for Joe's portfolio site — not part of the codebase,
   mentioned for context only.

# Cast Calc

Cast Calc is a RuneLite side-panel plugin for Old School RuneScape that shows the GP cost of casting spells using live Grand Exchange prices. It covers Standard, Ancient, Lunar, and Arceuus spellbooks, with tools for rune costs, profit/loss checks, rune-saving gear, and Magic training goals.

## What It Does

### Spell Cost Calculator
- Shows cast cost for 182 spells across all four spellbooks
- Uses live GE prices for rune costs, refreshed automatically every 5 minutes or manually with the Refresh button
- Lets you search, filter, and sort spells by name, spellbook, category, level, cost, or profit/loss
- Expands spell rows to show per-rune cost details
- Shows free-rune and rune-save effects when your equipped gear affects the spell cost

### Magic Goal Calculator
- **Casts mode**: Enter a number of casts to see total GP cost and Magic XP gained
- **Level mode**: Choose a target Magic level or XP total to calculate casts and GP required
- **Cost mode**: Enter a GP budget to see how many casts you can afford and how much XP you would gain

### Rune-Saving Gear Detection
Cast Calc automatically detects equipped gear and adjusts spell costs in real time.

| Gear | Effect |
|------|--------|
| Elemental staves | Provides unlimited matching elemental runes |
| Combination staves | Provides unlimited runes for both elemental types |
| Tome of Fire | Provides unlimited fire runes |
| Tome of Water | Provides unlimited water runes |
| Kodai Wand | Provides water runes and a 15% chance to save all runes |
| Bryophyta's Staff | Has a 1/15 chance to save nature runes |

### Profit & Loss Calculator
For supported utility spells, Cast Calc compares input cost, output value, and rune cost to show profit or loss per cast.

**Alchemy**
- High Level Alchemy
- Low Level Alchemy
- Item search with GE buy price, alchemy value, rune cost, and profit/loss

**Lunar Conversions**
- Tan Leather
- Plank Make
- Spin Flax
- Superglass Make
- String Jewellery

**Superheat Item**
- Bronze, iron, silver, steel, gold, mithril, adamantite, and runite bars
- Supports multi-ore alloy costs such as coal requirements

**Arceuus Reanimation**
- Covers all 18 ensouled head types
- Shows head cost, rune cost, total GP cost, and Prayer XP gained

## Settings

| Setting | Default | Description |
|---------|---------|-------------|
| Use Equipped Gear | On | Factor equipped rune-saving gear into spell costs |
| Show Rune Breakdown | On | Show per-rune costs when expanding a spell |
| Default Spellbook Filter | All | Choose which spellbook appears when the panel opens |
| Show Level Requirement | On | Display Magic level requirement next to each spell |
| Highlight Profitable | On | Highlight utility spells with profitable presets |
| Expanded Card Text Size | Small | Adjust text size inside expanded spell details |

## API Notes & Limitations

- **GE prices**: Fetched through RuneLite's `ItemManager.getItemPrice()`, which uses OSRS Grand Exchange price data. Prices can lag behind active market trades.
- **Alchemy values**: Retrieved from RuneLite item composition data. These are fixed in-game values, not GE prices.
- **Item search**: Uses RuneLite item search for the alchemy calculator. Results are capped for UI performance.
- **Gear detection**: Reads the equipped-item container and updates when equipment changes.
- **Reanimation spells**: Shown as cost-per-cast and Prayer XP rather than profit/loss, since the output is XP rather than a sellable item.
- **Superglass Make**: Uses an average molten glass output estimate. Actual output varies in game.

## License

BSD-2-Clause (same as RuneLite)

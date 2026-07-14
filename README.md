# Cast Calc - RuneLite Plugin for OSRS

A RuneLite side-panel plugin that shows the GP cost of every spell cast across all four spellbooks, using live Grand Exchange prices. Includes a full profit & loss calculator for utility spells and rune-saving gear detection.

---

## Features

### Spell Cost Calculator
- **All 4 spellbooks**: Standard, Ancient, Lunar, and Arceuus - 182 spells
- **Live GE prices**: Rune costs pulled from the Grand Exchange, refreshed every 5 minutes (or manually via the Refresh button)
- **Search & filter**: Find spells by name, spellbook, category, or toggle "P&L spells only"
- **Sort**: By name, cost (ascending/descending), or level (ascending/descending)
- **Rune breakdown**: Expand any spell to see per-rune cost details with free-rune and save-chance labels
- **Spell count**: Live count of how many spells match your current filters

### Magic Goal Calculator
- **Casts mode**: Enter a cast count to see total cost and Magic XP gained
- **Level mode**: Choose a target level or XP total to calculate casts and GP required
- **Cost mode**: Enter a GP budget to see affordable casts and resulting XP

### Rune-Saving Gear Detection
Automatically detects your equipped gear and adjusts all costs in real time:

| Gear | Effect |
|------|--------|
| Elemental staves (air, water, earth, fire) | Provides unlimited elemental runes |
| Combination staves (smoke, steam, dust, mud, lava, mist) | Provides two elemental types |
| Tome of Fire | Provides unlimited fire runes |
| Tome of Water | Provides unlimited water runes |
| Kodai Wand | Provides water runes + 15% chance to save all runes |
| Bryophyta's Staff | 1/15 chance to save nature runes |

Gear effects are shown at the top of the panel and update automatically when you change equipment.

### Profit & Loss Calculator
For utility spells, click to see whether casting is profitable:

**Alchemy**
- **High Level Alchemy**: Search any item → see HA value vs. GE buy price vs. rune cost = profit/loss per alch
- **Low Level Alchemy**: Same as above with LA values

**Lunar Conversions** (with pre-built presets)
- **Tan Leather**: Green, blue, red, and black dragonhide (5 hides per cast)
- **Plank Make**: All 4 log types with NPC plank fees (70–1,050 gp)
- **Spin Flax**: Flax → bow string (5 per cast)
- **Superglass Make**: Sand + soda ash, or sand + giant seaweed → molten glass
- **String Jewellery**: Gold amulet (u) → gold amulet

**Superheat Item** (with multi-ore alloy support)
- Bronze (tin + copper), iron, silver, steel (iron + 2 coal), gold
- Mithril (mith + 4 coal), adamantite (addy + 6 coal), runite (rune + 8 coal)

**Arceuus Reanimation** (all 18 ensouled head types)
- Shows cost per cast (head GE price + rune cost) and prayer XP gained
- Covers goblin through dragon heads

---

## Installation

### Option A: RuneLite Plugin Hub (Recommended)
Once approved, search for **"Cast Calc"** in the RuneLite Plugin Hub.

### Option B: Build from Source
1. Clone this repository
2. Build with Gradle:
   ```bash
   ./gradlew build
   ```
3. Run the development client with `./gradlew run`

---

## Configuration

Open RuneLite Settings → Cast Calc:

| Setting | Default | Description |
|---------|---------|-------------|
| Use Equipped Gear | ON | Factor in staves/tomes for cost reduction |
| Show Rune Breakdown | ON | Show per-rune costs when expanding a spell |
| Default Spellbook Filter | All | Which spellbook to show on panel open |
| Show Level Requirement | ON | Display magic level next to each spell |
| Highlight Profitable | ON | Green P&L label on utility spells with presets |
| Expanded Card Text Size | Small | Text size used inside expanded spell details |

---

## Project Structure

```
src/main/java/com/castcalc/
├── CastCalcPlugin.java       # Entry point - registers panel, handles events
├── CastCalcConfig.java        # User settings (gear toggle, filters, display prefs)
├── CastCalcPanel.java         # Full Swing side panel UI
├── CastCalcStyle.java         # Shared UI colors, fonts, spacing, and layout constants
├── CostCalculator.java        # Cost engine - spell costs, alch P&L, conversion P&L
├── ConversionPreset.java      # Pre-built input-to-output mappings for P&L spells
├── SpellDatabase.java         # Spell definitions and rune requirements
├── SpellData.java             # Spell data model
├── MagicXpTable.java          # OSRS Magic XP and level calculations
├── CalcMode.java              # Goal calculator input modes
├── Spellbook.java             # Spellbook enum (Standard, Ancient, Lunar, Arceuus)
├── SpellCategory.java         # Category enum (Combat, Teleport, Utility, Curse, Support)
├── Rune.java                  # Rune enum with OSRS item IDs
├── RuneRequirement.java       # Rune + quantity pair
└── RuneSavingGear.java        # Gear detection & rune-saving probability math
```

---

## API Notes & Limitations

- **GE Prices**: Fetched via RuneLite's `ItemManager.getItemPrice()` which uses the OSRS GE API. Prices may lag ~5 minutes behind real-time.
- **High Alch Values**: Retrieved from `ItemComposition.getHaPrice()` - these are fixed game values, not GE-based.
- **Item Search**: Uses `ItemManager.search()` for the alchemy calculator. Results capped at 8 per query for UI performance.
- **Gear Detection**: Reads the equipment container via `InventoryID.EQUIPMENT`. Updates automatically on equipment change events.
- **Reanimation Spells**: Shown as cost-per-cast rather than profit, since the "output" is prayer XP (not a sellable item). The cost = ensouled head GE price + rune cost.
- **Superglass Make**: Uses an average of ~10 molten glass output per cast. Actual output varies (1.3x the input amount, rounded).

---

## Adding New Spells or Presets

**Add a spell** to `SpellDatabase.java`:
```java
addSpell("Spell Name", SPELLBOOK, CATEGORY, level, hasProfitLoss,
    rune(FIRE, 5), rune(NATURE, 1));
```

**Add a conversion preset** to `ConversionPreset.java`:
```java
register("Spell Name",
    new ConversionPreset("Label", OUTPUT_ITEM_ID, "Output name", outputQty, extraFee,
        input(INPUT_ITEM_ID, "Input name", inputQty),
        input(SECOND_INPUT_ID, "Second input", qty))  // multi-input supported
);
```

---

## License

BSD-2-Clause (same as RuneLite)

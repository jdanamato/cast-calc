package com.castcalc;

import net.runelite.api.ItemID;

import java.util.*;

/**
 * Pre-built conversion presets for P&L-eligible spells.
 * Each preset maps a spell name to its possible input→output conversions.
 * Supports multi-input conversions (e.g., Superheat steel = iron ore + 2 coal).
 */
public class ConversionPreset
{
    private final String label;
    private final List<ItemInput> inputs;
    private final int outputItemId;
    private final String outputName;
    private final int outputQty;
    private final int extraGpCost;

    public ConversionPreset(String label, int outputItemId, String outputName,
                            int outputQty, int extraGpCost, ItemInput... inputs)
    {
        this.label = label;
        this.outputItemId = outputItemId;
        this.outputName = outputName;
        this.outputQty = outputQty;
        this.extraGpCost = extraGpCost;
        this.inputs = Arrays.asList(inputs);
    }

    public String getLabel()
    {
        return label;
    }

    public List<ItemInput> getInputs()
    {
        return inputs;
    }

    public int getOutputItemId()
    {
        return outputItemId;
    }

    public String getOutputName()
    {
        return outputName;
    }

    public int getOutputQty()
    {
        return outputQty;
    }

    public int getExtraGpCost()
    {
        return extraGpCost;
    }

    public static class ItemInput
    {
        private final int itemId;
        private final String name;
        private final int quantity;

        public ItemInput(int itemId, String name, int quantity)
        {
            this.itemId = itemId;
            this.name = name;
            this.quantity = quantity;
        }

        public int getItemId()
        {
            return itemId;
        }

        public String getName()
        {
            return name;
        }

        public int getQuantity()
        {
            return quantity;
        }
    }

    private static ItemInput input(int itemId, String name, int qty)
    {
        return new ItemInput(itemId, name, qty);
    }

    private static final Map<String, List<ConversionPreset>> PRESETS = new LinkedHashMap<>();

    static
    {
        // =====================================================================
        // TAN LEATHER — tans 5 hides per cast
        // =====================================================================
        register("Tan Leather",
            new ConversionPreset("Green d'hide \u2192 Leather", ItemID.GREEN_DRAGON_LEATHER,
                "Green dragon leather", 5, 0,
                input(ItemID.GREEN_DRAGONHIDE, "Green dragonhide", 5)),
            new ConversionPreset("Blue d'hide \u2192 Leather", ItemID.BLUE_DRAGON_LEATHER,
                "Blue dragon leather", 5, 0,
                input(ItemID.BLUE_DRAGONHIDE, "Blue dragonhide", 5)),
            new ConversionPreset("Red d'hide \u2192 Leather", ItemID.RED_DRAGON_LEATHER,
                "Red dragon leather", 5, 0,
                input(ItemID.RED_DRAGONHIDE, "Red dragonhide", 5)),
            new ConversionPreset("Black d'hide \u2192 Leather", ItemID.BLACK_DRAGON_LEATHER,
                "Black dragon leather", 5, 0,
                input(ItemID.BLACK_DRAGONHIDE, "Black dragonhide", 5))
        );

        // =====================================================================
        // PLANK MAKE
        // =====================================================================
        register("Plank Make",
            new ConversionPreset("Logs \u2192 Plank", ItemID.PLANK,
                "Plank", 1, 70,
                input(ItemID.LOGS, "Logs", 1)),
            new ConversionPreset("Oak logs \u2192 Oak plank", ItemID.OAK_PLANK,
                "Oak plank", 1, 175,
                input(ItemID.OAK_LOGS, "Oak logs", 1)),
            new ConversionPreset("Teak logs \u2192 Teak plank", ItemID.TEAK_PLANK,
                "Teak plank", 1, 350,
                input(ItemID.TEAK_LOGS, "Teak logs", 1)),
            new ConversionPreset("Mahogany logs \u2192 Mahogany plank", ItemID.MAHOGANY_PLANK,
                "Mahogany plank", 1, 1050,
                input(ItemID.MAHOGANY_LOGS, "Mahogany logs", 1))
        );

        // =====================================================================
        // SUPERHEAT ITEM
        // =====================================================================
        register("Superheat Item",
            new ConversionPreset("Bronze (tin + copper)", ItemID.BRONZE_BAR,
                "Bronze bar", 1, 0,
                input(ItemID.TIN_ORE, "Tin ore", 1),
                input(ItemID.COPPER_ORE, "Copper ore", 1)),
            new ConversionPreset("Iron ore \u2192 Iron bar", ItemID.IRON_BAR,
                "Iron bar", 1, 0,
                input(ItemID.IRON_ORE, "Iron ore", 1)),
            new ConversionPreset("Silver ore \u2192 Silver bar", ItemID.SILVER_BAR,
                "Silver bar", 1, 0,
                input(ItemID.SILVER_ORE, "Silver ore", 1)),
            new ConversionPreset("Steel (iron + 2 coal)", ItemID.STEEL_BAR,
                "Steel bar", 1, 0,
                input(ItemID.IRON_ORE, "Iron ore", 1),
                input(ItemID.COAL, "Coal", 2)),
            new ConversionPreset("Gold ore \u2192 Gold bar", ItemID.GOLD_BAR,
                "Gold bar", 1, 0,
                input(ItemID.GOLD_ORE, "Gold ore", 1)),
            new ConversionPreset("Mithril (mith ore + 4 coal)", ItemID.MITHRIL_BAR,
                "Mithril bar", 1, 0,
                input(ItemID.MITHRIL_ORE, "Mithril ore", 1),
                input(ItemID.COAL, "Coal", 4)),
            new ConversionPreset("Adamantite (addy ore + 6 coal)", ItemID.ADAMANTITE_BAR,
                "Adamantite bar", 1, 0,
                input(ItemID.ADAMANTITE_ORE, "Adamantite ore", 1),
                input(ItemID.COAL, "Coal", 6)),
            new ConversionPreset("Runite (rune ore + 8 coal)", ItemID.RUNITE_BAR,
                "Runite bar", 1, 0,
                input(ItemID.RUNITE_ORE, "Runite ore", 1),
                input(ItemID.COAL, "Coal", 8))
        );

        // =====================================================================
        // SPIN FLAX
        // =====================================================================
        register("Spin Flax",
            new ConversionPreset("Flax \u2192 Bow string", ItemID.BOW_STRING,
                "Bow string", 5, 0,
                input(ItemID.FLAX, "Flax", 5))
        );

        // =====================================================================
        // SUPERGLASS MAKE
        // =====================================================================
        register("Superglass Make",
            new ConversionPreset("Sand + Soda ash \u2192 Molten glass", ItemID.MOLTEN_GLASS,
                "Molten glass", 10, 0,
                input(ItemID.BUCKET_OF_SAND, "Bucket of sand", 6),
                input(ItemID.SODA_ASH, "Soda ash", 6)),
            new ConversionPreset("Sand + Giant seaweed \u2192 Molten glass", ItemID.MOLTEN_GLASS,
                "Molten glass", 10, 0,
                input(ItemID.BUCKET_OF_SAND, "Bucket of sand", 3),
                input(ItemID.GIANT_SEAWEED, "Giant seaweed", 1))
        );

        // =====================================================================
        // STRING JEWELLERY
        // =====================================================================
        register("String Jewellery",
            new ConversionPreset("Gold amulet (u) \u2192 Gold amulet", ItemID.GOLD_AMULET,
                "Gold amulet", 1, 0,
                input(ItemID.GOLD_AMULET_U, "Gold amulet (u)", 1))
        );

        // =====================================================================
        // MAGIC TABLET MAKING (at lectern in Player-Owned House)
        // Each cast: 1 soft clay + spell runes \u2192 1 tablet
        // =====================================================================
        registerTabletSpell("Varrock Teleport", ItemID.VARROCK_TELEPORT);
        registerTabletSpell("Lumbridge Teleport", ItemID.LUMBRIDGE_TELEPORT);
        registerTabletSpell("Falador Teleport", ItemID.FALADOR_TELEPORT);
        registerTabletSpell("Teleport to House", ItemID.TELEPORT_TO_HOUSE);
        registerTabletSpell("Camelot Teleport", ItemID.CAMELOT_TELEPORT);
        registerTabletSpell("Ardougne Teleport", ItemID.ARDOUGNE_TELEPORT);
        registerTabletSpell("Watchtower Teleport", ItemID.WATCHTOWER_TELEPORT);
        registerTabletSpell("Kourend Castle Teleport", ItemID.KOUREND_CASTLE_TELEPORT);
        registerTabletSpell("Trollheim Teleport", ItemID.TROLLHEIM_TELEPORT);

        // Ancient spellbook tablets (Mahogany Eagle lectern, Deathmatch unlock)
        registerTabletSpell("Paddewwa Teleport", ItemID.PADDEWWA_TELEPORT);
        registerTabletSpell("Senntisten Teleport", ItemID.SENNTISTEN_TELEPORT);
        registerTabletSpell("Kharyrll Teleport", ItemID.KHARYRLL_TELEPORT);
        registerTabletSpell("Lassar Teleport", ItemID.LASSAR_TELEPORT);
        registerTabletSpell("Dareeyak Teleport", ItemID.DAREEYAK_TELEPORT);
        registerTabletSpell("Carrallangar Teleport", ItemID.CARRALLANGER_TELEPORT);
        registerTabletSpell("Annakarl Teleport", ItemID.ANNAKARL_TELEPORT);
        registerTabletSpell("Ghorrock Teleport", ItemID.GHORROCK_TELEPORT);

        // Lunar spellbook tablets (Mahogany Eagle lectern, Lunar Diplomacy unlock)
        registerTabletSpell("Moonclan Teleport", ItemID.MOONCLAN_TELEPORT);
        registerTabletSpell("Waterbirth Teleport", ItemID.WATERBIRTH_TELEPORT);
        registerTabletSpell("Barbarian Teleport", ItemID.BARBARIAN_TELEPORT);
        registerTabletSpell("Khazard Teleport", ItemID.KHAZARD_TELEPORT);
        registerTabletSpell("Fishing Guild Teleport", ItemID.FISHING_GUILD_TELEPORT);
        registerTabletSpell("Catherby Teleport", ItemID.CATHERBY_TELEPORT);
        registerTabletSpell("Ice Plateau Teleport", ItemID.ICE_PLATEAU_TELEPORT);
        // Note: Teleport to Ape Atoll uses banana over standard recipe and isn't included.

        // =====================================================================
        // REANIMATE SPELLS — ensouled head \u2192 prayer XP
        // =====================================================================
        register("Reanimate Goblin",
            new ConversionPreset("Ensouled goblin head", -1,
                "Prayer XP (130)", 1, 0,
                input(ItemID.ENSOULED_GOBLIN_HEAD, "Ensouled goblin head", 1)));
        register("Reanimate Monkey",
            new ConversionPreset("Ensouled monkey head", -1,
                "Prayer XP (182)", 1, 0,
                input(ItemID.ENSOULED_MONKEY_HEAD, "Ensouled monkey head", 1)));
        register("Reanimate Imp",
            new ConversionPreset("Ensouled imp head", -1,
                "Prayer XP (286)", 1, 0,
                input(ItemID.ENSOULED_IMP_HEAD, "Ensouled imp head", 1)));
        register("Reanimate Dog",
            new ConversionPreset("Ensouled dog head", -1,
                "Prayer XP (520)", 1, 0,
                input(ItemID.ENSOULED_DOG_HEAD, "Ensouled dog head", 1)));
        register("Reanimate Chaos Druid",
            new ConversionPreset("Ensouled chaos druid head", -1,
                "Prayer XP (584)", 1, 0,
                input(ItemID.ENSOULED_CHAOS_DRUID_HEAD, "Ensouled chaos druid head", 1)));
        register("Reanimate Giant",
            new ConversionPreset("Ensouled giant head", -1,
                "Prayer XP (650)", 1, 0,
                input(ItemID.ENSOULED_GIANT_HEAD, "Ensouled giant head", 1)));
        register("Reanimate Ogre",
            new ConversionPreset("Ensouled ogre head", -1,
                "Prayer XP (716)", 1, 0,
                input(ItemID.ENSOULED_OGRE_HEAD, "Ensouled ogre head", 1)));
        register("Reanimate Elf",
            new ConversionPreset("Ensouled elf head", -1,
                "Prayer XP (754)", 1, 0,
                input(ItemID.ENSOULED_ELF_HEAD, "Ensouled elf head", 1)));
        register("Reanimate Troll",
            new ConversionPreset("Ensouled troll head", -1,
                "Prayer XP (780)", 1, 0,
                input(ItemID.ENSOULED_TROLL_HEAD, "Ensouled troll head", 1)));
        register("Reanimate Horror",
            new ConversionPreset("Ensouled horror head", -1,
                "Prayer XP (832)", 1, 0,
                input(ItemID.ENSOULED_HORROR_HEAD, "Ensouled horror head", 1)));
        register("Reanimate Kalphite",
            new ConversionPreset("Ensouled kalphite head", -1,
                "Prayer XP (884)", 1, 0,
                input(ItemID.ENSOULED_KALPHITE_HEAD, "Ensouled kalphite head", 1)));
        register("Reanimate Dagannoth",
            new ConversionPreset("Ensouled dagannoth head", -1,
                "Prayer XP (936)", 1, 0,
                input(ItemID.ENSOULED_DAGANNOTH_HEAD, "Ensouled dagannoth head", 1)));
        register("Reanimate Bloodveld",
            new ConversionPreset("Ensouled bloodveld head", -1,
                "Prayer XP (1040)", 1, 0,
                input(ItemID.ENSOULED_BLOODVELD_HEAD, "Ensouled bloodveld head", 1)));
        register("Reanimate TzHaar",
            new ConversionPreset("Ensouled tzhaar head", -1,
                "Prayer XP (1104)", 1, 0,
                input(ItemID.ENSOULED_TZHAAR_HEAD, "Ensouled tzhaar head", 1)));
        register("Reanimate Demon",
            new ConversionPreset("Ensouled demon head", -1,
                "Prayer XP (1170)", 1, 0,
                input(ItemID.ENSOULED_DEMON_HEAD, "Ensouled demon head", 1)));
        register("Reanimate Aviansie",
            new ConversionPreset("Ensouled aviansie head", -1,
                "Prayer XP (1234)", 1, 0,
                input(ItemID.ENSOULED_AVIANSIE_HEAD, "Ensouled aviansie head", 1)));
        register("Reanimate Abyssal",
            new ConversionPreset("Ensouled abyssal head", -1,
                "Prayer XP (1300)", 1, 0,
                input(ItemID.ENSOULED_ABYSSAL_HEAD, "Ensouled abyssal head", 1)));
        register("Reanimate Dragon",
            new ConversionPreset("Ensouled dragon head", -1,
                "Prayer XP (1560)", 1, 0,
                input(ItemID.ENSOULED_DRAGON_HEAD, "Ensouled dragon head", 1)));
    }

    private static void register(String spellName, ConversionPreset... presets)
    {
        PRESETS.put(spellName, Arrays.asList(presets));
    }

    /**
     * Helper: adds a tablet-making preset to an existing teleport spell's preset list.
     * Also ensures the spell has presets even if none existed before.
     */
    private static void registerTabletSpell(String spellName, int tabletItemId)
    {
        ConversionPreset preset = new ConversionPreset(
            "Tablet (at lectern)", tabletItemId,
            "Tablet", 1, 0,
            input(ItemID.SOFT_CLAY, "Soft clay", 1)
        );

        List<ConversionPreset> existing = PRESETS.get(spellName);
        if (existing == null)
        {
            PRESETS.put(spellName, new ArrayList<>(Collections.singletonList(preset)));
        }
        else
        {
            List<ConversionPreset> mutable = new ArrayList<>(existing);
            mutable.add(preset);
            PRESETS.put(spellName, mutable);
        }
    }

    public static List<ConversionPreset> getPresetsForSpell(String spellName)
    {
        return PRESETS.get(spellName);
    }

    public static Map<String, List<ConversionPreset>> getAllPresets()
    {
        return PRESETS;
    }

    public static boolean hasPresets(String spellName)
    {
        return PRESETS.containsKey(spellName);
    }
}

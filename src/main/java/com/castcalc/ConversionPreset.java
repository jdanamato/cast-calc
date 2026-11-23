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
    /** Prayer XP per cast for reanimation presets; 0 for everything else. */
    private final int prayerXp;

    public ConversionPreset(String label, int outputItemId, String outputName,
                            int outputQty, int extraGpCost, ItemInput... inputs)
    {
        this(label, outputItemId, outputName, outputQty, extraGpCost, 0, inputs);
    }

    private ConversionPreset(String label, int outputItemId, String outputName,
                             int outputQty, int extraGpCost, int prayerXp, ItemInput... inputs)
    {
        this.label = label;
        this.outputItemId = outputItemId;
        this.outputName = outputName;
        this.outputQty = outputQty;
        this.extraGpCost = extraGpCost;
        this.prayerXp = prayerXp;
        this.inputs = Arrays.asList(inputs);
    }

    /**
     * A reanimation preset: 1 ensouled head in, Prayer XP out (no item).
     * The label names the head ("Goblin head") and becomes the card header.
     * headItemId must be the GE-tradeable variant (the ItemID constant with
     * the numeric suffix, e.g. ENSOULED_GOBLIN_HEAD_13448); the unsuffixed
     * constant is a different, untradeable item with no GE price.
     */
    private static ConversionPreset reanimate(String headName, int headItemId, int prayerXp)
    {
        return new ConversionPreset(headName, -1, "Prayer XP", 1, 0, prayerXp,
            input(headItemId, "Ensouled head", 1));
    }

    public int getPrayerXp()
    {
        return prayerXp;
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
        // Input names are shown on a narrow P&L line where only ~72px of text
        // fits (about 11 characters) before Swing cuts it off with "...". Keep
        // them short enough that an input and its output don't truncate to the
        // same prefix (e.g. "Green hide", not "Green dragonhide" next to
        // "Green dragon leather"). The output name also titles the card, so it
        // keeps the full in-game name.

        // =====================================================================
        // TAN LEATHER — tans 5 hides per cast
        // =====================================================================
        register("Tan Leather",
            new ConversionPreset("Green d'hide \u2192 Leather", ItemID.GREEN_DRAGON_LEATHER,
                "Green dragon leather", 5, 0,
                input(ItemID.GREEN_DRAGONHIDE, "Green hide", 5)),
            new ConversionPreset("Blue d'hide \u2192 Leather", ItemID.BLUE_DRAGON_LEATHER,
                "Blue dragon leather", 5, 0,
                input(ItemID.BLUE_DRAGONHIDE, "Blue hide", 5)),
            new ConversionPreset("Red d'hide \u2192 Leather", ItemID.RED_DRAGON_LEATHER,
                "Red dragon leather", 5, 0,
                input(ItemID.RED_DRAGONHIDE, "Red hide", 5)),
            new ConversionPreset("Black d'hide \u2192 Leather", ItemID.BLACK_DRAGON_LEATHER,
                "Black dragon leather", 5, 0,
                input(ItemID.BLACK_DRAGONHIDE, "Black hide", 5))
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
                input(ItemID.MAHOGANY_LOGS, "Mahog. logs", 1))
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
                input(ItemID.ADAMANTITE_ORE, "Addy ore", 1),
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
        // CHARGE ORB — cast on the matching obelisk, 1 unpowered orb per cast
        // =====================================================================
        register("Charge Water Orb",
            new ConversionPreset("Unpowered orb -> Water orb", ItemID.WATER_ORB,
                "Water orb", 1, 0,
                input(ItemID.UNPOWERED_ORB, "Unpowered orb", 1)));
        register("Charge Earth Orb",
            new ConversionPreset("Unpowered orb -> Earth orb", ItemID.EARTH_ORB,
                "Earth orb", 1, 0,
                input(ItemID.UNPOWERED_ORB, "Unpowered orb", 1)));
        register("Charge Fire Orb",
            new ConversionPreset("Unpowered orb -> Fire orb", ItemID.FIRE_ORB,
                "Fire orb", 1, 0,
                input(ItemID.UNPOWERED_ORB, "Unpowered orb", 1)));
        register("Charge Air Orb",
            new ConversionPreset("Unpowered orb -> Air orb", ItemID.AIR_ORB,
                "Air orb", 1, 0,
                input(ItemID.UNPOWERED_ORB, "Unpowered orb", 1)));

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
        registerTabletSpell("Civitas illa Fortis Teleport", ItemID.CIVITAS_ILLA_FORTIS_TELEPORT);
        registerTabletSpell("Watchtower Teleport", ItemID.WATCHTOWER_TELEPORT);
        registerTabletSpell("Kourend Castle Teleport", ItemID.KOUREND_CASTLE_TELEPORT);
        // No Trollheim entry: that tablet isn't made at a lectern (it's a house
        // tablet + Scroll of redirection) and is untradeable, so it has no GE price.

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
        // REANIMATION — 1 ensouled head per cast -> Prayer XP (no item out)
        // Since 2021 there are only 4 reanimation spells; each head needs a
        // specific tier. Data from the OSRS Wiki "Ensouled heads" table.
        // =====================================================================
        register("Basic Reanimation",
            reanimate("Goblin head", ItemID.ENSOULED_GOBLIN_HEAD_13448, 130),
            reanimate("Monkey head", ItemID.ENSOULED_MONKEY_HEAD_13451, 182),
            reanimate("Imp head", ItemID.ENSOULED_IMP_HEAD_13454, 286),
            reanimate("Minotaur head", ItemID.ENSOULED_MINOTAUR_HEAD_13457, 364),
            reanimate("Scorpion head", ItemID.ENSOULED_SCORPION_HEAD_13460, 454),
            reanimate("Bear head", ItemID.ENSOULED_BEAR_HEAD_13463, 480),
            reanimate("Unicorn head", ItemID.ENSOULED_UNICORN_HEAD_13466, 494));
        register("Adept Reanimation",
            reanimate("Dog head", ItemID.ENSOULED_DOG_HEAD_13469, 520),
            reanimate("Chaos druid head", ItemID.ENSOULED_CHAOS_DRUID_HEAD_13472, 584),
            reanimate("Giant head", ItemID.ENSOULED_GIANT_HEAD_13475, 650),
            reanimate("Ogre head", ItemID.ENSOULED_OGRE_HEAD_13478, 716),
            reanimate("Elf head", ItemID.ENSOULED_ELF_HEAD_13481, 754),
            reanimate("Troll head", ItemID.ENSOULED_TROLL_HEAD_13484, 780),
            reanimate("Horror head", ItemID.ENSOULED_HORROR_HEAD_13487, 832));
        register("Expert Reanimation",
            reanimate("Kalphite head", ItemID.ENSOULED_KALPHITE_HEAD_13490, 884),
            reanimate("Dagannoth head", ItemID.ENSOULED_DAGANNOTH_HEAD_13493, 936),
            reanimate("Bloodveld head", ItemID.ENSOULED_BLOODVELD_HEAD_13496, 1040),
            reanimate("TzHaar head", ItemID.ENSOULED_TZHAAR_HEAD_13499, 1104),
            reanimate("Demon head", ItemID.ENSOULED_DEMON_HEAD_13502, 1170),
            reanimate("Hellhound head", ItemID.ENSOULED_HELLHOUND_HEAD_26997, 1200));
        register("Master Reanimation",
            reanimate("Aviansie head", ItemID.ENSOULED_AVIANSIE_HEAD_13505, 1234),
            reanimate("Abyssal head", ItemID.ENSOULED_ABYSSAL_HEAD_13508, 1300),
            reanimate("Dragon head", ItemID.ENSOULED_DRAGON_HEAD_13511, 1560));
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

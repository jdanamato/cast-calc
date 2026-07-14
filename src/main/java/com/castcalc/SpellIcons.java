package com.castcalc;

import net.runelite.api.SpriteID;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps spell names to their RuneLite SpriteID constants for icon display.
 *
 * To add a new spell:
 *   1. Find its constant in RuneLite's SpriteID class. Convention:
 *      SPELL_<UPPERCASE_SPELL_NAME_WITH_UNDERSCORES>
 *   2. Add a register() call using the exact spell name as in SpellDatabase.
 *
 * If a constant name is wrong it will cause a compile error — comment out
 * the offending line and that spell will render with a blank placeholder.
 *
 * Notes on known missing constants:
 *   - Telegroup spells share sprites with their individual counterparts
 *   - Individual Reanimate spells were merged in 2021; individual SpriteID
 *     constants no longer exist — all fall back to SPELL_BASIC_REANIMATION
 *   - Resurrect variants: only SUPERIOR_SKELETON confirmed; others fall back
 *   - Battlefront Teleport: no constant exists in the version of RuneLite
 *     we target
 */
public final class SpellIcons
{
    private SpellIcons() {}

    private static final Map<String, Integer> SPELL_SPRITE_IDS = new HashMap<>();

    static
    {
        // =====================================================================
        // STANDARD - COMBAT
        // =====================================================================
        register("Wind Strike",       SpriteID.SPELL_WIND_STRIKE);
        register("Water Strike",      SpriteID.SPELL_WATER_STRIKE);
        register("Earth Strike",      SpriteID.SPELL_EARTH_STRIKE);
        register("Fire Strike",       SpriteID.SPELL_FIRE_STRIKE);
        register("Wind Bolt",         SpriteID.SPELL_WIND_BOLT);
        register("Water Bolt",        SpriteID.SPELL_WATER_BOLT);
        register("Earth Bolt",        SpriteID.SPELL_EARTH_BOLT);
        register("Fire Bolt",         SpriteID.SPELL_FIRE_BOLT);
        register("Wind Blast",        SpriteID.SPELL_WIND_BLAST);
        register("Water Blast",       SpriteID.SPELL_WATER_BLAST);
        register("Earth Blast",       SpriteID.SPELL_EARTH_BLAST);
        register("Fire Blast",        SpriteID.SPELL_FIRE_BLAST);
        register("Wind Wave",         SpriteID.SPELL_WIND_WAVE);
        register("Water Wave",        SpriteID.SPELL_WATER_WAVE);
        register("Earth Wave",        SpriteID.SPELL_EARTH_WAVE);
        register("Fire Wave",         SpriteID.SPELL_FIRE_WAVE);
        register("Wind Surge",        SpriteID.SPELL_WIND_SURGE);
        register("Water Surge",       SpriteID.SPELL_WATER_SURGE);
        register("Earth Surge",       SpriteID.SPELL_EARTH_SURGE);
        register("Fire Surge",        SpriteID.SPELL_FIRE_SURGE);
        register("Crumble Undead",    SpriteID.SPELL_CRUMBLE_UNDEAD);
        register("Iban Blast",        SpriteID.SPELL_IBAN_BLAST);
        register("Saradomin Strike",  SpriteID.SPELL_SARADOMIN_STRIKE);
        register("Claws of Guthix",   SpriteID.SPELL_CLAWS_OF_GUTHIX);
        register("Flames of Zamorak", SpriteID.SPELL_FLAMES_OF_ZAMORAK);

        // =====================================================================
        // STANDARD - CURSES
        // =====================================================================
        register("Confuse",       SpriteID.SPELL_CONFUSE);
        register("Weaken",        SpriteID.SPELL_WEAKEN);
        register("Curse",         SpriteID.SPELL_CURSE);
        register("Vulnerability", SpriteID.SPELL_VULNERABILITY);
        register("Enfeeble",      SpriteID.SPELL_ENFEEBLE);
        register("Stun",          SpriteID.SPELL_STUN);
        register("Bind",          SpriteID.SPELL_BIND);
        register("Snare",         SpriteID.SPELL_SNARE);
        register("Entangle",      SpriteID.SPELL_ENTANGLE);

        // =====================================================================
        // STANDARD - UTILITY
        // =====================================================================
        register("Low Level Alchemy",   SpriteID.SPELL_LOW_LEVEL_ALCHEMY);
        register("High Level Alchemy",  SpriteID.SPELL_HIGH_LEVEL_ALCHEMY);
        register("Superheat Item",      SpriteID.SPELL_SUPERHEAT_ITEM);
        register("Bones to Bananas",    SpriteID.SPELL_BONES_TO_BANANAS);
        register("Bones to Peaches",    SpriteID.SPELL_BONES_TO_PEACHES);
        register("Charge",              SpriteID.SPELL_CHARGE);

        // =====================================================================
        // STANDARD - ENCHANTMENTS
        // =====================================================================
        register("Lvl-1 Enchant",         SpriteID.SPELL_LVL_1_ENCHANT);
        register("Lvl-2 Enchant",         SpriteID.SPELL_LVL_2_ENCHANT);
        register("Lvl-3 Enchant",         SpriteID.SPELL_LVL_3_ENCHANT);
        register("Lvl-4 Enchant",         SpriteID.SPELL_LVL_4_ENCHANT);
        register("Lvl-5 Enchant",         SpriteID.SPELL_LVL_5_ENCHANT);
        register("Lvl-6 Enchant",         SpriteID.SPELL_LVL_6_ENCHANT);
        register("Lvl-7 Enchant",         SpriteID.SPELL_LVL_7_ENCHANT);
        register("Enchant Crossbow Bolt", SpriteID.SPELL_ENCHANT_CROSSBOW_BOLT);

        // =====================================================================
        // STANDARD - TELEPORTS
        // Note: Battlefront Teleport has no SpriteID constant — blank placeholder.
        // =====================================================================
        register("Varrock Teleport",        SpriteID.SPELL_VARROCK_TELEPORT);
        register("Lumbridge Teleport",      SpriteID.SPELL_LUMBRIDGE_TELEPORT);
        register("Falador Teleport",        SpriteID.SPELL_FALADOR_TELEPORT);
        register("Teleport to House",       SpriteID.SPELL_TELEPORT_TO_HOUSE);
        register("Camelot Teleport",        SpriteID.SPELL_CAMELOT_TELEPORT);
        register("Ardougne Teleport",       SpriteID.SPELL_ARDOUGNE_TELEPORT);
        register("Watchtower Teleport",     SpriteID.SPELL_WATCHTOWER_TELEPORT);
        register("Trollheim Teleport",      SpriteID.SPELL_TROLLHEIM_TELEPORT);
        register("Kourend Castle Teleport", SpriteID.SPELL_TELEPORT_TO_KOUREND);
        register("Teleport to Ape Atoll",   SpriteID.SPELL_TELEPORT_TO_APE_ATOLL);
        register("Teleother Lumbridge",   SpriteID.SPELL_TELEOTHER_LUMBRIDGE);
        register("Teleother Falador",     SpriteID.SPELL_TELEOTHER_FALADOR);
        register("Teleother Camelot",     SpriteID.SPELL_TELEOTHER_CAMELOT);

        // =====================================================================
        // ANCIENT - COMBAT
        // =====================================================================
        register("Smoke Rush",    SpriteID.SPELL_SMOKE_RUSH);
        register("Smoke Burst",   SpriteID.SPELL_SMOKE_BURST);
        register("Smoke Blitz",   SpriteID.SPELL_SMOKE_BLITZ);
        register("Smoke Barrage", SpriteID.SPELL_SMOKE_BARRAGE);
        register("Shadow Rush",   SpriteID.SPELL_SHADOW_RUSH);
        register("Shadow Burst",  SpriteID.SPELL_SHADOW_BURST);
        register("Shadow Blitz",  SpriteID.SPELL_SHADOW_BLITZ);
        register("Shadow Barrage",SpriteID.SPELL_SHADOW_BARRAGE);
        register("Blood Rush",    SpriteID.SPELL_BLOOD_RUSH);
        register("Blood Burst",   SpriteID.SPELL_BLOOD_BURST);
        register("Blood Blitz",   SpriteID.SPELL_BLOOD_BLITZ);
        register("Blood Barrage", SpriteID.SPELL_BLOOD_BARRAGE);
        register("Ice Rush",      SpriteID.SPELL_ICE_RUSH);
        register("Ice Burst",     SpriteID.SPELL_ICE_BURST);
        register("Ice Blitz",     SpriteID.SPELL_ICE_BLITZ);
        register("Ice Barrage",   SpriteID.SPELL_ICE_BARRAGE);

        // =====================================================================
        // ANCIENT - TELEPORTS
        // =====================================================================
        register("Paddewwa Teleport",    SpriteID.SPELL_PADDEWWA_TELEPORT);
        register("Senntisten Teleport",  SpriteID.SPELL_SENNTISTEN_TELEPORT);
        register("Kharyrll Teleport",    SpriteID.SPELL_KHARYRLL_TELEPORT);
        register("Lassar Teleport",      SpriteID.SPELL_LASSAR_TELEPORT);
        register("Dareeyak Teleport",    SpriteID.SPELL_DAREEYAK_TELEPORT);
        register("Carrallangar Teleport",SpriteID.SPELL_CARRALLANGAR_TELEPORT);
        register("Annakarl Teleport",    SpriteID.SPELL_ANNAKARL_TELEPORT);
        register("Ghorrock Teleport",    SpriteID.SPELL_GHORROCK_TELEPORT);

        // =====================================================================
        // LUNAR - UTILITY
        // =====================================================================
        register("Tan Leather",      SpriteID.SPELL_TAN_LEATHER);
        register("Plank Make",       SpriteID.SPELL_PLANK_MAKE);
        register("Spin Flax",        SpriteID.SPELL_SPIN_FLAX);
        register("Superglass Make",  SpriteID.SPELL_SUPERGLASS_MAKE);
        register("String Jewellery", SpriteID.SPELL_STRING_JEWELLERY);
        register("Bake Pie",         SpriteID.SPELL_BAKE_PIE);
        register("Humidify",         SpriteID.SPELL_HUMIDIFY);
        register("Magic Imbue",      SpriteID.SPELL_MAGIC_IMBUE);
        register("Hunter Kit",       SpriteID.SPELL_HUNTER_KIT);
        register("Fertile Soil",     SpriteID.SPELL_FERTILE_SOIL);
        register("Cure Plant",       SpriteID.SPELL_CURE_PLANT);

        // =====================================================================
        // LUNAR - SUPPORT
        // Note: Vengeance Group has no SpriteID constant — falls back to blank.
        // =====================================================================
        register("Cure Other",             SpriteID.SPELL_CURE_OTHER);
        register("Cure Me",                SpriteID.SPELL_CURE_ME);
        register("Cure Group",             SpriteID.SPELL_CURE_GROUP);
        register("Dream",                  SpriteID.SPELL_DREAM);
        register("Heal Other",             SpriteID.SPELL_HEAL_OTHER);
        register("Heal Group",             SpriteID.SPELL_HEAL_GROUP);
        register("Energy Transfer",        SpriteID.SPELL_ENERGY_TRANSFER);
        register("Vengeance Other",        SpriteID.SPELL_VENGEANCE_OTHER);
        register("Vengeance",              SpriteID.SPELL_VENGEANCE);
        register("Vengeance Group",        SpriteID.SPELL_VENGEANCE); // no GROUP constant; reuse Vengeance sprite
        register("Boost Potion Share",     SpriteID.SPELL_BOOST_POTION_SHARE);
        register("Stat Restore Pot Share", SpriteID.SPELL_STAT_RESTORE_POT_SHARE);

        // =====================================================================
        // LUNAR - TELEPORTS
        // Note: Telegroup spells have no separate SpriteID constants. They
        // reuse the sprite of their individual counterpart.
        // =====================================================================
        register("Moonclan Teleport",       SpriteID.SPELL_MOONCLAN_TELEPORT);
        register("Ourania Teleport",        SpriteID.SPELL_OURANIA_TELEPORT);
        register("Waterbirth Teleport",     SpriteID.SPELL_WATERBIRTH_TELEPORT);
        register("Barbarian Teleport",      SpriteID.SPELL_BARBARIAN_TELEPORT);
        register("Khazard Teleport",        SpriteID.SPELL_KHAZARD_TELEPORT);
        register("Fishing Guild Teleport",  SpriteID.SPELL_FISHING_GUILD_TELEPORT);
        register("Catherby Teleport",       SpriteID.SPELL_CATHERBY_TELEPORT);
        register("Ice Plateau Teleport",    SpriteID.SPELL_ICE_PLATEAU_TELEPORT);
        register("Telegroup Moonclan",      SpriteID.SPELL_MOONCLAN_TELEPORT);
        register("Telegroup Waterbirth",    SpriteID.SPELL_WATERBIRTH_TELEPORT);
        register("Telegroup Barbarian",     SpriteID.SPELL_BARBARIAN_TELEPORT);
        register("Telegroup Khazard",       SpriteID.SPELL_KHAZARD_TELEPORT);
        register("Telegroup Fishing Guild", SpriteID.SPELL_FISHING_GUILD_TELEPORT);
        register("Telegroup Catherby",      SpriteID.SPELL_CATHERBY_TELEPORT);
        register("Telegroup Ice Plateau",   SpriteID.SPELL_ICE_PLATEAU_TELEPORT);

        // =====================================================================
        // ARCEUUS - COMBAT
        // =====================================================================
        register("Ghostly Grasp",      SpriteID.SPELL_GHOSTLY_GRASP);
        register("Skeletal Grasp",     SpriteID.SPELL_SKELETAL_GRASP);
        register("Undead Grasp",       SpriteID.SPELL_UNDEAD_GRASP);
        register("Inferior Demonbane", SpriteID.SPELL_INFERIOR_DEMONBANE);
        register("Superior Demonbane", SpriteID.SPELL_SUPERIOR_DEMONBANE);
        register("Dark Demonbane",     SpriteID.SPELL_DARK_DEMONBANE);
        register("Lesser Corruption",  SpriteID.SPELL_LESSER_CORRUPTION);
        register("Greater Corruption", SpriteID.SPELL_GREATER_CORRUPTION);

        // =====================================================================
        // ARCEUUS - REANIMATION
        // Individual reanimate spell constants were removed when the spells were
        // merged into 4 tiers in 2021. All fall back to SPELL_BASIC_REANIMATION.
        // =====================================================================
        register("Reanimate Goblin",      SpriteID.SPELL_BASIC_REANIMATION);
        register("Reanimate Monkey",      SpriteID.SPELL_BASIC_REANIMATION);
        register("Reanimate Imp",         SpriteID.SPELL_BASIC_REANIMATION);
        register("Reanimate Dog",         SpriteID.SPELL_BASIC_REANIMATION);
        register("Reanimate Chaos Druid", SpriteID.SPELL_BASIC_REANIMATION);
        register("Reanimate Giant",       SpriteID.SPELL_ADEPT_REANIMATION);
        register("Reanimate Ogre",        SpriteID.SPELL_ADEPT_REANIMATION);
        register("Reanimate Elf",         SpriteID.SPELL_ADEPT_REANIMATION);
        register("Reanimate Troll",       SpriteID.SPELL_ADEPT_REANIMATION);
        register("Reanimate Horror",      SpriteID.SPELL_EXPERT_REANIMATION);
        register("Reanimate Kalphite",    SpriteID.SPELL_EXPERT_REANIMATION);
        register("Reanimate Dagannoth",   SpriteID.SPELL_EXPERT_REANIMATION);
        register("Reanimate Bloodveld",   SpriteID.SPELL_EXPERT_REANIMATION);
        register("Reanimate TzHaar",      SpriteID.SPELL_MASTER_REANIMATION);
        register("Reanimate Demon",       SpriteID.SPELL_MASTER_REANIMATION);
        register("Reanimate Aviansie",    SpriteID.SPELL_MASTER_REANIMATION);
        register("Reanimate Abyssal",     SpriteID.SPELL_MASTER_REANIMATION);
        register("Reanimate Dragon",      SpriteID.SPELL_MASTER_REANIMATION);

        // =====================================================================
        // ARCEUUS - UTILITY
        // =====================================================================
        register("Basic Reanimation",  SpriteID.SPELL_BASIC_REANIMATION);
        register("Adept Reanimation",  SpriteID.SPELL_ADEPT_REANIMATION);
        register("Expert Reanimation", SpriteID.SPELL_EXPERT_REANIMATION);
        register("Master Reanimation", SpriteID.SPELL_MASTER_REANIMATION);
        register("Demonic Offering",   SpriteID.SPELL_DEMONIC_OFFERING);
        register("Sinister Offering",  SpriteID.SPELL_SINISTER_OFFERING);
        register("Shadow Veil",        SpriteID.SPELL_SHADOW_VEIL);
        register("Vile Vigour",        SpriteID.SPELL_VILE_VIGOUR);
        register("Dark Lure",          SpriteID.SPELL_DARK_LURE);
        register("Mark of Darkness",   SpriteID.SPELL_MARK_OF_DARKNESS);
        register("Ward of Arceuus",    SpriteID.SPELL_WARD_OF_ARCEUUS);

        // Resurrect spells — one sprite constant exists per tier (not per
        // creature type), matching the 3 tiers shown in-game.
        register("Resurrect Lesser Ghost",     SpriteID.SPELL_RESURRECT_LESSER_GHOST);
        register("Resurrect Lesser Skeleton",  SpriteID.SPELL_RESURRECT_LESSER_GHOST);
        register("Resurrect Lesser Zombie",    SpriteID.SPELL_RESURRECT_LESSER_GHOST);
        register("Resurrect Superior Ghost",   SpriteID.SPELL_RESURRECT_SUPERIOR_SKELETON);
        register("Resurrect Superior Skeleton",SpriteID.SPELL_RESURRECT_SUPERIOR_SKELETON);
        register("Resurrect Superior Zombie",  SpriteID.SPELL_RESURRECT_SUPERIOR_SKELETON);
        register("Resurrect Greater Ghost",    SpriteID.SPELL_RESURRECT_GREATER_ZOMBIE);
        register("Resurrect Greater Skeleton", SpriteID.SPELL_RESURRECT_GREATER_ZOMBIE);
        register("Resurrect Greater Zombie",   SpriteID.SPELL_RESURRECT_GREATER_ZOMBIE);

        // =====================================================================
        // ARCEUUS - TELEPORTS
        // Note: Battlefront Teleport has no SpriteID constant — blank placeholder.
        // =====================================================================
        register("Arceuus Library Teleport",      SpriteID.SPELL_ARCEUUS_LIBRARY_TELEPORT);
        register("Draynor Manor Teleport",        SpriteID.SPELL_DRAYNOR_MANOR_TELEPORT);
        // register("Battlefront Teleport", ???); // no SpriteID constant exists
        register("Mind Altar Teleport",           SpriteID.SPELL_MIND_ALTAR_TELEPORT);
        register("Respawn Teleport",              SpriteID.SPELL_RESPAWN_TELEPORT);
        register("Salve Graveyard Teleport",      SpriteID.SPELL_SALVE_GRAVEYARD_TELEPORT);
        register("Fenkenstrain's Castle Teleport",SpriteID.SPELL_FENKENSTRAINS_CASTLE_TELEPORT);
        register("West Ardougne Teleport",        SpriteID.SPELL_WEST_ARDOUGNE_TELEPORT);
        register("Harmony Island Teleport",       SpriteID.SPELL_HARMONY_ISLAND_TELEPORT);
        register("Cemetery Teleport",             SpriteID.SPELL_CEMETERY_TELEPORT);
        register("Barrows Teleport",              SpriteID.SPELL_BARROWS_TELEPORT);
        register("Ape Atoll Teleport (Arceuus)",  SpriteID.SPELL_APE_ATOLL_TELEPORT);
    }

    private static void register(String spellName, int spriteId)
    {
        SPELL_SPRITE_IDS.put(spellName, spriteId);
    }

    /**
     * Returns the sprite ID for a spell, or -1 if no mapping exists.
     * -1 means the row renders a blank 24x24 placeholder.
     */
    public static int getSpriteId(String spellName)
    {
        Integer id = SPELL_SPRITE_IDS.get(spellName);
        return id != null ? id : -1;
    }

    public static boolean hasIcon(String spellName)
    {
        return SPELL_SPRITE_IDS.containsKey(spellName);
    }
}

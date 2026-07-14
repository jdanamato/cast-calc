package com.castcalc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.castcalc.Rune.*;
import static com.castcalc.Spellbook.*;
import static com.castcalc.SpellCategory.*;

/**
 * Complete spell database for all four OSRS spellbooks.
 *
 * Each entry: name, spellbook, category, level, P&L flag, XP per cast,
 * XpMode, rune requirements.
 *
 * XpMode is qualified as XpMode.X to avoid ambiguity with SpellCategory.COMBAT.
 *
 * XP values sourced from the OSRS Wiki. Combat spell XP assumes successful cast.
 */
public final class SpellDatabase
{
    private static final List<SpellData> ALL_SPELLS = new ArrayList<>();

    static
    {
        // =====================================================================
        // STANDARD SPELLBOOK - COMBAT
        // =====================================================================
        addSpell("Wind Strike",       STANDARD, COMBAT, 1,  false, 5.5,  XpMode.COMBAT, rune(AIR, 1), rune(MIND, 1));
        addSpell("Water Strike",      STANDARD, COMBAT, 5,  false, 7.5,  XpMode.COMBAT, rune(AIR, 1), rune(WATER, 1), rune(MIND, 1));
        addSpell("Earth Strike",      STANDARD, COMBAT, 9,  false, 9.5,  XpMode.COMBAT, rune(AIR, 1), rune(EARTH, 2), rune(MIND, 1));
        addSpell("Fire Strike",       STANDARD, COMBAT, 13, false, 11.5, XpMode.COMBAT, rune(AIR, 2), rune(FIRE, 3), rune(MIND, 1));
        addSpell("Wind Bolt",         STANDARD, COMBAT, 17, false, 13.5, XpMode.COMBAT, rune(AIR, 2), rune(CHAOS, 1));
        addSpell("Water Bolt",        STANDARD, COMBAT, 23, false, 16.5, XpMode.COMBAT, rune(AIR, 2), rune(WATER, 2), rune(CHAOS, 1));
        addSpell("Earth Bolt",        STANDARD, COMBAT, 29, false, 19.5, XpMode.COMBAT, rune(AIR, 2), rune(EARTH, 3), rune(CHAOS, 1));
        addSpell("Fire Bolt",         STANDARD, COMBAT, 35, false, 22.5, XpMode.COMBAT, rune(AIR, 3), rune(FIRE, 4), rune(CHAOS, 1));
        addSpell("Wind Blast",        STANDARD, COMBAT, 41, false, 25.5, XpMode.COMBAT, rune(AIR, 3), rune(DEATH, 1));
        addSpell("Water Blast",       STANDARD, COMBAT, 47, false, 28.5, XpMode.COMBAT, rune(AIR, 3), rune(WATER, 3), rune(DEATH, 1));
        addSpell("Earth Blast",       STANDARD, COMBAT, 53, false, 31.5, XpMode.COMBAT, rune(AIR, 3), rune(EARTH, 4), rune(DEATH, 1));
        addSpell("Fire Blast",        STANDARD, COMBAT, 59, false, 34.5, XpMode.COMBAT, rune(AIR, 4), rune(FIRE, 5), rune(DEATH, 1));
        addSpell("Wind Wave",         STANDARD, COMBAT, 62, false, 36,   XpMode.COMBAT, rune(AIR, 5), rune(BLOOD, 1));
        addSpell("Water Wave",        STANDARD, COMBAT, 65, false, 37.5, XpMode.COMBAT, rune(AIR, 5), rune(WATER, 7), rune(BLOOD, 1));
        addSpell("Earth Wave",        STANDARD, COMBAT, 70, false, 40,   XpMode.COMBAT, rune(AIR, 5), rune(EARTH, 7), rune(BLOOD, 1));
        addSpell("Fire Wave",         STANDARD, COMBAT, 75, false, 42.5, XpMode.COMBAT, rune(AIR, 5), rune(FIRE, 7), rune(BLOOD, 1));
        addSpell("Wind Surge",        STANDARD, COMBAT, 81, false, 44.5, XpMode.COMBAT, rune(AIR, 7), rune(WRATH, 1));
        addSpell("Water Surge",       STANDARD, COMBAT, 85, false, 46,   XpMode.COMBAT, rune(AIR, 7), rune(WATER, 10), rune(WRATH, 1));
        addSpell("Earth Surge",       STANDARD, COMBAT, 90, false, 48,   XpMode.COMBAT, rune(AIR, 7), rune(EARTH, 10), rune(WRATH, 1));
        addSpell("Fire Surge",        STANDARD, COMBAT, 95, false, 50.5, XpMode.COMBAT, rune(AIR, 7), rune(FIRE, 10), rune(WRATH, 1));
        addSpell("Crumble Undead",    STANDARD, COMBAT, 39, false, 24.5, XpMode.COMBAT, rune(AIR, 2), rune(EARTH, 2), rune(CHAOS, 1));
        addSpell("Iban Blast",        STANDARD, COMBAT, 50, false, 30,   XpMode.COMBAT, rune(FIRE, 5), rune(DEATH, 1));
        addSpell("Saradomin Strike",  STANDARD, COMBAT, 60, false, 35,   XpMode.COMBAT, rune(AIR, 4), rune(FIRE, 2), rune(BLOOD, 2));
        addSpell("Claws of Guthix",   STANDARD, COMBAT, 60, false, 35,   XpMode.COMBAT, rune(AIR, 4), rune(FIRE, 1), rune(BLOOD, 2));
        addSpell("Flames of Zamorak", STANDARD, COMBAT, 60, false, 35,   XpMode.COMBAT, rune(AIR, 1), rune(FIRE, 4), rune(BLOOD, 2));

        // =====================================================================
        // STANDARD SPELLBOOK - CURSES
        // =====================================================================
        addSpell("Confuse",       STANDARD, CURSE, 3,  false, 13,   XpMode.NORMAL, rune(WATER, 3), rune(EARTH, 2), rune(BODY, 1));
        addSpell("Weaken",        STANDARD, CURSE, 11, false, 20.5, XpMode.NORMAL, rune(WATER, 3), rune(EARTH, 2), rune(BODY, 1));
        addSpell("Curse",         STANDARD, CURSE, 19, false, 29,   XpMode.NORMAL, rune(WATER, 2), rune(EARTH, 3), rune(BODY, 1));
        addSpell("Vulnerability", STANDARD, CURSE, 66, false, 76,   XpMode.NORMAL, rune(WATER, 5), rune(EARTH, 5), rune(SOUL, 1));
        addSpell("Enfeeble",      STANDARD, CURSE, 73, false, 83,   XpMode.NORMAL, rune(WATER, 8), rune(EARTH, 8), rune(SOUL, 1));
        addSpell("Stun",          STANDARD, CURSE, 80, false, 90,   XpMode.NORMAL, rune(WATER, 12), rune(EARTH, 12), rune(SOUL, 1));
        addSpell("Bind",          STANDARD, CURSE, 20, false, 30,   XpMode.NORMAL, rune(WATER, 3), rune(EARTH, 3), rune(NATURE, 2));
        addSpell("Snare",         STANDARD, CURSE, 50, false, 60,   XpMode.NORMAL, rune(WATER, 4), rune(EARTH, 4), rune(NATURE, 3));
        addSpell("Entangle",      STANDARD, CURSE, 79, false, 89,   XpMode.NORMAL, rune(WATER, 5), rune(EARTH, 5), rune(NATURE, 4));

        // =====================================================================
        // STANDARD SPELLBOOK - UTILITY
        // =====================================================================
        addSpell("Low Level Alchemy",   STANDARD, UTILITY, 21, true,  31,  XpMode.NORMAL, rune(FIRE, 3), rune(NATURE, 1));
        addSpell("High Level Alchemy",  STANDARD, UTILITY, 55, true,  65,  XpMode.NORMAL, rune(FIRE, 5), rune(NATURE, 1));
        addSpell("Superheat Item",      STANDARD, UTILITY, 43, true,  53,  XpMode.NORMAL, rune(FIRE, 4), rune(NATURE, 1));
        addSpell("Bones to Bananas",    STANDARD, UTILITY, 15, false, 25,  XpMode.NORMAL, rune(EARTH, 2), rune(WATER, 2), rune(NATURE, 1));
        addSpell("Bones to Peaches",    STANDARD, UTILITY, 60, false, 65,  XpMode.NORMAL, rune(EARTH, 2), rune(WATER, 4), rune(NATURE, 2));
        addSpell("Charge",              STANDARD, UTILITY, 80, false, 180, XpMode.NORMAL, rune(AIR, 3), rune(FIRE, 3), rune(BLOOD, 3));

        // =====================================================================
        // STANDARD SPELLBOOK - ENCHANTMENTS
        // =====================================================================
        addSpell("Lvl-1 Enchant",         STANDARD, UTILITY, 7,  false, 17.5, XpMode.NORMAL, rune(WATER, 1), rune(COSMIC, 1));
        addSpell("Lvl-2 Enchant",         STANDARD, UTILITY, 27, false, 37,   XpMode.NORMAL, rune(AIR, 3), rune(COSMIC, 1));
        addSpell("Lvl-3 Enchant",         STANDARD, UTILITY, 49, false, 59,   XpMode.NORMAL, rune(FIRE, 5), rune(COSMIC, 1));
        addSpell("Lvl-4 Enchant",         STANDARD, UTILITY, 57, false, 67,   XpMode.NORMAL, rune(EARTH, 10), rune(COSMIC, 1));
        addSpell("Lvl-5 Enchant",         STANDARD, UTILITY, 68, false, 78,   XpMode.NORMAL, rune(WATER, 15), rune(EARTH, 15), rune(COSMIC, 1));
        addSpell("Lvl-6 Enchant",         STANDARD, UTILITY, 87, false, 97,   XpMode.NORMAL, rune(EARTH, 20), rune(FIRE, 20), rune(COSMIC, 1));
        addSpell("Lvl-7 Enchant",         STANDARD, UTILITY, 93, false, 110,  XpMode.NORMAL, rune(BLOOD, 20), rune(SOUL, 20), rune(COSMIC, 1));
        addSpell("Enchant Crossbow Bolt", STANDARD, UTILITY, 4,  false, 0,    XpMode.VARIES, rune(COSMIC, 1));

        // =====================================================================
        // STANDARD SPELLBOOK - TELEPORTS
        // =====================================================================
        addSpell("Varrock Teleport",        STANDARD, TELEPORT, 25, true,  35,   XpMode.NORMAL, rune(AIR, 3), rune(FIRE, 1), rune(LAW, 1));
        addSpell("Lumbridge Teleport",      STANDARD, TELEPORT, 31, true,  41,   XpMode.NORMAL, rune(AIR, 3), rune(EARTH, 1), rune(LAW, 1));
        addSpell("Falador Teleport",        STANDARD, TELEPORT, 37, true,  48,   XpMode.NORMAL, rune(AIR, 3), rune(WATER, 1), rune(LAW, 1));
        addSpell("Teleport to House",       STANDARD, TELEPORT, 40, true,  30,   XpMode.NORMAL, rune(AIR, 1), rune(EARTH, 1), rune(LAW, 1));
        addSpell("Camelot Teleport",        STANDARD, TELEPORT, 45, true,  55.5, XpMode.NORMAL, rune(AIR, 5), rune(LAW, 1));
        addSpell("Ardougne Teleport",       STANDARD, TELEPORT, 51, true,  61,   XpMode.NORMAL, rune(WATER, 2), rune(LAW, 2));
        addSpell("Watchtower Teleport",     STANDARD, TELEPORT, 58, true,  68,   XpMode.NORMAL, rune(EARTH, 2), rune(LAW, 2));
        addSpell("Trollheim Teleport",      STANDARD, TELEPORT, 61, true,  68,   XpMode.NORMAL, rune(FIRE, 2), rune(LAW, 2));
        addSpell("Kourend Castle Teleport", STANDARD, TELEPORT, 69, true,  82,   XpMode.NORMAL, rune(FIRE, 5), rune(WATER, 4), rune(SOUL, 2), rune(LAW, 2));
        addSpell("Teleother Lumbridge",     STANDARD, TELEPORT, 74, false, 84,   XpMode.NORMAL, rune(EARTH, 1), rune(LAW, 1), rune(SOUL, 1));
        addSpell("Teleother Falador",       STANDARD, TELEPORT, 82, false, 92,   XpMode.NORMAL, rune(WATER, 1), rune(LAW, 1), rune(SOUL, 1));
        addSpell("Teleother Camelot",       STANDARD, TELEPORT, 90, false, 100,  XpMode.NORMAL, rune(LAW, 1), rune(SOUL, 2));
        addSpell("Teleport to Ape Atoll",   STANDARD, TELEPORT, 64, false, 74,   XpMode.NORMAL, rune(FIRE, 2), rune(WATER, 2), rune(LAW, 2));

        // =====================================================================
        // ANCIENT MAGICKS - COMBAT
        // =====================================================================
        addSpell("Smoke Rush",    ANCIENT, COMBAT, 50, false, 30,  XpMode.COMBAT, rune(AIR, 1), rune(FIRE, 1), rune(CHAOS, 2), rune(DEATH, 2));
        addSpell("Smoke Burst",   ANCIENT, COMBAT, 62, false, 36,  XpMode.COMBAT, rune(AIR, 2), rune(FIRE, 2), rune(CHAOS, 4), rune(DEATH, 2));
        addSpell("Smoke Blitz",   ANCIENT, COMBAT, 74, false, 42,  XpMode.COMBAT, rune(AIR, 2), rune(FIRE, 2), rune(BLOOD, 2), rune(DEATH, 2));
        addSpell("Smoke Barrage", ANCIENT, COMBAT, 86, false, 48,  XpMode.COMBAT, rune(AIR, 4), rune(FIRE, 4), rune(BLOOD, 2), rune(DEATH, 4));
        addSpell("Shadow Rush",   ANCIENT, COMBAT, 52, false, 31,  XpMode.COMBAT, rune(AIR, 1), rune(EARTH, 1), rune(CHAOS, 2), rune(DEATH, 2));
        addSpell("Shadow Burst",  ANCIENT, COMBAT, 64, false, 37,  XpMode.COMBAT, rune(AIR, 1), rune(EARTH, 2), rune(CHAOS, 4), rune(DEATH, 2));
        addSpell("Shadow Blitz",  ANCIENT, COMBAT, 76, false, 43,  XpMode.COMBAT, rune(AIR, 2), rune(EARTH, 2), rune(BLOOD, 2), rune(DEATH, 2));
        addSpell("Shadow Barrage",ANCIENT, COMBAT, 88, false, 49,  XpMode.COMBAT, rune(AIR, 4), rune(EARTH, 4), rune(BLOOD, 2), rune(DEATH, 4));
        addSpell("Blood Rush",    ANCIENT, COMBAT, 56, false, 33,  XpMode.COMBAT, rune(BLOOD, 1), rune(CHAOS, 2), rune(DEATH, 2));
        addSpell("Blood Burst",   ANCIENT, COMBAT, 68, false, 39,  XpMode.COMBAT, rune(BLOOD, 2), rune(CHAOS, 4), rune(DEATH, 2));
        addSpell("Blood Blitz",   ANCIENT, COMBAT, 80, false, 45,  XpMode.COMBAT, rune(BLOOD, 4), rune(DEATH, 2));
        addSpell("Blood Barrage", ANCIENT, COMBAT, 92, false, 51,  XpMode.COMBAT, rune(BLOOD, 4), rune(DEATH, 4), rune(SOUL, 1));
        addSpell("Ice Rush",      ANCIENT, COMBAT, 58, false, 34,  XpMode.COMBAT, rune(WATER, 2), rune(CHAOS, 2), rune(DEATH, 2));
        addSpell("Ice Burst",     ANCIENT, COMBAT, 70, false, 40,  XpMode.COMBAT, rune(WATER, 4), rune(CHAOS, 4), rune(DEATH, 2));
        addSpell("Ice Blitz",     ANCIENT, COMBAT, 82, false, 46,  XpMode.COMBAT, rune(WATER, 3), rune(BLOOD, 2), rune(DEATH, 2));
        addSpell("Ice Barrage",   ANCIENT, COMBAT, 94, false, 52,  XpMode.COMBAT, rune(WATER, 6), rune(BLOOD, 2), rune(DEATH, 4));

        // =====================================================================
        // ANCIENT MAGICKS - TELEPORTS
        // =====================================================================
        addSpell("Paddewwa Teleport",    ANCIENT, TELEPORT, 54, true,  64,  XpMode.NORMAL, rune(AIR, 1), rune(FIRE, 1), rune(LAW, 2));
        addSpell("Senntisten Teleport",  ANCIENT, TELEPORT, 60, true,  70,  XpMode.NORMAL, rune(LAW, 2), rune(SOUL, 1));
        addSpell("Kharyrll Teleport",    ANCIENT, TELEPORT, 66, true,  76,  XpMode.NORMAL, rune(BLOOD, 1), rune(LAW, 2));
        addSpell("Lassar Teleport",      ANCIENT, TELEPORT, 72, true,  82,  XpMode.NORMAL, rune(WATER, 4), rune(LAW, 2));
        addSpell("Dareeyak Teleport",    ANCIENT, TELEPORT, 78, true,  88,  XpMode.NORMAL, rune(AIR, 2), rune(FIRE, 3), rune(LAW, 2));
        addSpell("Carrallangar Teleport",ANCIENT, TELEPORT, 84, true,  94,  XpMode.NORMAL, rune(LAW, 2), rune(SOUL, 2));
        addSpell("Annakarl Teleport",    ANCIENT, TELEPORT, 90, true,  100, XpMode.NORMAL, rune(BLOOD, 2), rune(LAW, 2));
        addSpell("Ghorrock Teleport",    ANCIENT, TELEPORT, 96, true,  106, XpMode.NORMAL, rune(WATER, 8), rune(LAW, 2));

        // =====================================================================
        // LUNAR SPELLBOOK - UTILITY
        // =====================================================================
        addSpell("Tan Leather",      LUNAR, UTILITY, 78, true,  81, XpMode.NORMAL, rune(FIRE, 2), rune(ASTRAL, 2), rune(NATURE, 1));
        addSpell("Plank Make",       LUNAR, UTILITY, 86, true,  90, XpMode.NORMAL, rune(EARTH, 15), rune(ASTRAL, 2), rune(NATURE, 1));
        addSpell("Spin Flax",        LUNAR, UTILITY, 76, true,  75, XpMode.NORMAL, rune(AIR, 5), rune(ASTRAL, 2), rune(NATURE, 1));
        addSpell("String Jewellery", LUNAR, UTILITY, 80, true,  83, XpMode.NORMAL, rune(WATER, 5), rune(EARTH, 10), rune(ASTRAL, 2));
        addSpell("Superglass Make",  LUNAR, UTILITY, 77, true,  78, XpMode.NORMAL, rune(AIR, 10), rune(FIRE, 6), rune(ASTRAL, 2));
        addSpell("Bake Pie",         LUNAR, UTILITY, 65, false, 60, XpMode.NORMAL, rune(FIRE, 5), rune(WATER, 4), rune(ASTRAL, 1));
        addSpell("Cure Plant",       LUNAR, UTILITY, 66, false, 67, XpMode.NORMAL, rune(EARTH, 8), rune(ASTRAL, 1));
        addSpell("Humidify",         LUNAR, UTILITY, 68, false, 65, XpMode.NORMAL, rune(WATER, 3), rune(FIRE, 1), rune(ASTRAL, 1));
        addSpell("Hunter Kit",       LUNAR, UTILITY, 71, false, 70, XpMode.NORMAL, rune(EARTH, 2), rune(ASTRAL, 2));
        addSpell("Fertile Soil",     LUNAR, UTILITY, 83, false, 87, XpMode.NORMAL, rune(EARTH, 15), rune(ASTRAL, 3), rune(NATURE, 2));
        addSpell("Magic Imbue",      LUNAR, UTILITY, 82, false, 82, XpMode.NORMAL, rune(FIRE, 7), rune(WATER, 7), rune(ASTRAL, 2));

        // =====================================================================
        // LUNAR SPELLBOOK - SUPPORT
        // =====================================================================
        addSpell("Cure Other",             LUNAR, SUPPORT, 68, false, 68,  XpMode.NORMAL, rune(EARTH, 10), rune(ASTRAL, 1));
        addSpell("Cure Me",                LUNAR, SUPPORT, 71, false, 69,  XpMode.NORMAL, rune(COSMIC, 2), rune(ASTRAL, 2));
        addSpell("Cure Group",             LUNAR, SUPPORT, 74, false, 74,  XpMode.NORMAL, rune(COSMIC, 2), rune(ASTRAL, 2));
        addSpell("Dream",                  LUNAR, SUPPORT, 79, false, 79,  XpMode.NORMAL, rune(COSMIC, 1), rune(ASTRAL, 2), rune(BODY, 5));
        addSpell("Heal Other",             LUNAR, SUPPORT, 92, false, 92,  XpMode.NORMAL, rune(BLOOD, 1), rune(ASTRAL, 3), rune(LAW, 3));
        addSpell("Heal Group",             LUNAR, SUPPORT, 95, false, 95,  XpMode.NORMAL, rune(BLOOD, 3), rune(ASTRAL, 4), rune(LAW, 6));
        addSpell("Energy Transfer",        LUNAR, SUPPORT, 91, false, 100, XpMode.NORMAL, rune(ASTRAL, 3), rune(LAW, 2), rune(NATURE, 1));
        addSpell("Vengeance Other",        LUNAR, SUPPORT, 93, false, 108, XpMode.NORMAL, rune(EARTH, 10), rune(ASTRAL, 3), rune(DEATH, 2));
        addSpell("Vengeance",              LUNAR, SUPPORT, 94, false, 112, XpMode.NORMAL, rune(EARTH, 10), rune(ASTRAL, 4), rune(DEATH, 2));
        addSpell("Vengeance Group",        LUNAR, SUPPORT, 95, false, 120, XpMode.NORMAL, rune(EARTH, 10), rune(ASTRAL, 4), rune(DEATH, 3));
        addSpell("Boost Potion Share",     LUNAR, SUPPORT, 84, false, 84,  XpMode.NORMAL, rune(WATER, 12), rune(ASTRAL, 3));
        addSpell("Stat Restore Pot Share", LUNAR, SUPPORT, 81, false, 80,  XpMode.NORMAL, rune(WATER, 10), rune(EARTH, 10), rune(ASTRAL, 2));

        // =====================================================================
        // LUNAR SPELLBOOK - TELEPORTS
        // =====================================================================
        addSpell("Moonclan Teleport",      LUNAR, TELEPORT, 69, true,  66, XpMode.NORMAL, rune(EARTH, 2), rune(ASTRAL, 2), rune(LAW, 1));
        addSpell("Ourania Teleport",       LUNAR, TELEPORT, 71, false, 69, XpMode.NORMAL, rune(EARTH, 6), rune(ASTRAL, 2), rune(LAW, 1));
        addSpell("Waterbirth Teleport",    LUNAR, TELEPORT, 72, true,  71, XpMode.NORMAL, rune(WATER, 1), rune(ASTRAL, 2), rune(LAW, 1));
        addSpell("Barbarian Teleport",     LUNAR, TELEPORT, 75, true,  76, XpMode.NORMAL, rune(FIRE, 3), rune(ASTRAL, 2), rune(LAW, 2));
        addSpell("Khazard Teleport",       LUNAR, TELEPORT, 78, true,  78, XpMode.NORMAL, rune(WATER, 4), rune(ASTRAL, 2), rune(LAW, 2));
        addSpell("Fishing Guild Teleport", LUNAR, TELEPORT, 85, true,  84, XpMode.NORMAL, rune(WATER, 10), rune(ASTRAL, 3), rune(LAW, 3));
        addSpell("Catherby Teleport",      LUNAR, TELEPORT, 87, true,  87, XpMode.NORMAL, rune(WATER, 10), rune(ASTRAL, 3), rune(LAW, 3));
        addSpell("Ice Plateau Teleport",   LUNAR, TELEPORT, 89, true,  89, XpMode.NORMAL, rune(WATER, 8), rune(ASTRAL, 3), rune(LAW, 3));
        addSpell("Telegroup Moonclan",     LUNAR, TELEPORT, 70, false, 70, XpMode.NORMAL, rune(EARTH, 4), rune(ASTRAL, 2), rune(LAW, 1));
        addSpell("Telegroup Waterbirth",   LUNAR, TELEPORT, 73, false, 74, XpMode.NORMAL, rune(WATER, 5), rune(ASTRAL, 2), rune(LAW, 1));
        addSpell("Telegroup Barbarian",    LUNAR, TELEPORT, 76, false, 79, XpMode.NORMAL, rune(FIRE, 6), rune(ASTRAL, 2), rune(LAW, 2));
        addSpell("Telegroup Khazard",      LUNAR, TELEPORT, 79, false, 81, XpMode.NORMAL, rune(WATER, 8), rune(ASTRAL, 2), rune(LAW, 2));
        addSpell("Telegroup Fishing Guild",LUNAR, TELEPORT, 86, false, 87, XpMode.NORMAL, rune(WATER, 14), rune(ASTRAL, 3), rune(LAW, 3));
        addSpell("Telegroup Catherby",     LUNAR, TELEPORT, 88, false, 90, XpMode.NORMAL, rune(WATER, 12), rune(ASTRAL, 3), rune(LAW, 3));
        addSpell("Telegroup Ice Plateau",  LUNAR, TELEPORT, 90, false, 92, XpMode.NORMAL, rune(WATER, 16), rune(ASTRAL, 3), rune(LAW, 3));

        // =====================================================================
        // ARCEUUS SPELLBOOK - COMBAT
        // =====================================================================
        addSpell("Ghostly Grasp",      ARCEUUS, COMBAT, 35, false, 30, XpMode.COMBAT, rune(AIR, 4), rune(CHAOS, 1));
        addSpell("Skeletal Grasp",     ARCEUUS, COMBAT, 56, false, 46, XpMode.COMBAT, rune(EARTH, 4), rune(DEATH, 1));
        addSpell("Undead Grasp",       ARCEUUS, COMBAT, 79, false, 70, XpMode.COMBAT, rune(FIRE, 4), rune(BLOOD, 1));
        addSpell("Inferior Demonbane", ARCEUUS, COMBAT, 44, false, 27, XpMode.COMBAT, rune(FIRE, 2), rune(CHAOS, 1));
        addSpell("Superior Demonbane", ARCEUUS, COMBAT, 62, false, 46, XpMode.COMBAT, rune(FIRE, 4), rune(DEATH, 1));
        addSpell("Dark Demonbane",     ARCEUUS, COMBAT, 82, false, 70, XpMode.COMBAT, rune(FIRE, 8), rune(BLOOD, 1), rune(SOUL, 1));
        addSpell("Lesser Corruption",  ARCEUUS, COMBAT, 64, false, 39, XpMode.COMBAT, rune(SOUL, 2));
        addSpell("Greater Corruption", ARCEUUS, COMBAT, 85, false, 95, XpMode.COMBAT, rune(SOUL, 3), rune(BLOOD, 1));

        // =====================================================================
        // ARCEUUS SPELLBOOK - REANIMATE
        // =====================================================================
        addSpell("Reanimate Goblin",      ARCEUUS, UTILITY, 3,  true,  6.5,  XpMode.REANIMATE, rune(BODY, 2), rune(NATURE, 1));
        addSpell("Reanimate Monkey",      ARCEUUS, UTILITY, 7,  true,  8,    XpMode.REANIMATE, rune(BODY, 2), rune(NATURE, 1), rune(COSMIC, 1));
        addSpell("Reanimate Imp",         ARCEUUS, UTILITY, 12, true,  11,   XpMode.REANIMATE, rune(BODY, 2), rune(NATURE, 1));
        addSpell("Reanimate Dog",         ARCEUUS, UTILITY, 15, true,  12,   XpMode.REANIMATE, rune(BODY, 2), rune(NATURE, 1), rune(SOUL, 1));
        addSpell("Reanimate Chaos Druid", ARCEUUS, UTILITY, 30, true,  16.5, XpMode.REANIMATE, rune(BODY, 2), rune(NATURE, 2), rune(SOUL, 1));
        addSpell("Reanimate Giant",       ARCEUUS, UTILITY, 37, true,  21.5, XpMode.REANIMATE, rune(BODY, 2), rune(NATURE, 2), rune(SOUL, 2));
        addSpell("Reanimate Ogre",        ARCEUUS, UTILITY, 40, true,  25,   XpMode.REANIMATE, rune(BODY, 2), rune(NATURE, 2), rune(DEATH, 1));
        addSpell("Reanimate Elf",         ARCEUUS, UTILITY, 43, true,  32.5, XpMode.REANIMATE, rune(NATURE, 3), rune(SOUL, 3), rune(BLOOD, 1));
        addSpell("Reanimate Troll",       ARCEUUS, UTILITY, 46, true,  35,   XpMode.REANIMATE, rune(NATURE, 3), rune(SOUL, 2), rune(BLOOD, 1));
        addSpell("Reanimate Horror",      ARCEUUS, UTILITY, 52, true,  40.5, XpMode.REANIMATE, rune(NATURE, 3), rune(SOUL, 2), rune(BLOOD, 2));
        addSpell("Reanimate Kalphite",    ARCEUUS, UTILITY, 57, true,  43,   XpMode.REANIMATE, rune(NATURE, 3), rune(SOUL, 3), rune(BLOOD, 2));
        addSpell("Reanimate Dagannoth",   ARCEUUS, UTILITY, 62, true,  48,   XpMode.REANIMATE, rune(NATURE, 3), rune(SOUL, 3), rune(BLOOD, 3));
        addSpell("Reanimate Bloodveld",   ARCEUUS, UTILITY, 65, true,  50.5, XpMode.REANIMATE, rune(NATURE, 2), rune(SOUL, 2), rune(BLOOD, 4));
        addSpell("Reanimate TzHaar",      ARCEUUS, UTILITY, 69, true,  55.5, XpMode.REANIMATE, rune(NATURE, 3), rune(SOUL, 3), rune(BLOOD, 4));
        addSpell("Reanimate Demon",       ARCEUUS, UTILITY, 72, true,  58.5, XpMode.REANIMATE, rune(NATURE, 4), rune(SOUL, 4), rune(BLOOD, 4));
        addSpell("Reanimate Aviansie",    ARCEUUS, UTILITY, 78, true,  60,   XpMode.REANIMATE, rune(NATURE, 4), rune(SOUL, 3), rune(BLOOD, 5));
        addSpell("Reanimate Abyssal",     ARCEUUS, UTILITY, 85, true,  65.5, XpMode.REANIMATE, rune(NATURE, 4), rune(SOUL, 4), rune(BLOOD, 5));
        addSpell("Reanimate Dragon",      ARCEUUS, UTILITY, 93, true,  78.5, XpMode.REANIMATE, rune(NATURE, 4), rune(SOUL, 4), rune(BLOOD, 6));

        // =====================================================================
        // ARCEUUS SPELLBOOK - UTILITY (non-reanimate)
        // =====================================================================
        addSpell("Basic Reanimation",          ARCEUUS, UTILITY, 16, false, 32,  XpMode.REANIMATE, rune(BODY, 4), rune(NATURE, 2));
        addSpell("Adept Reanimation",          ARCEUUS, UTILITY, 41, false, 50,  XpMode.REANIMATE, rune(BODY, 4), rune(NATURE, 3), rune(SOUL, 2));
        addSpell("Expert Reanimation",         ARCEUUS, UTILITY, 72, false, 70,  XpMode.REANIMATE, rune(NATURE, 4), rune(BLOOD, 1), rune(SOUL, 3));
        addSpell("Master Reanimation",         ARCEUUS, UTILITY, 90, false, 120, XpMode.REANIMATE, rune(NATURE, 4), rune(BLOOD, 2), rune(SOUL, 4));
        addSpell("Demonic Offering",           ARCEUUS, UTILITY, 84, false, 175, XpMode.NORMAL,    rune(SOUL, 1));
        addSpell("Sinister Offering",          ARCEUUS, UTILITY, 92, false, 180, XpMode.NORMAL,    rune(BLOOD, 1), rune(SOUL, 1));
        addSpell("Shadow Veil",                ARCEUUS, UTILITY, 47, false, 50,  XpMode.NORMAL,    rune(EARTH, 5), rune(FIRE, 5), rune(COSMIC, 1));
        addSpell("Vile Vigour",                ARCEUUS, UTILITY, 66, false, 60,  XpMode.NORMAL,    rune(SOUL, 3));
        addSpell("Dark Lure",                  ARCEUUS, UTILITY, 50, false, 50,  XpMode.NORMAL,    rune(NATURE, 1), rune(DEATH, 1));
        addSpell("Mark of Darkness",           ARCEUUS, UTILITY, 59, false, 50,  XpMode.NORMAL,    rune(SOUL, 2), rune(BLOOD, 1));
        addSpell("Ward of Arceuus",            ARCEUUS, UTILITY, 73, false, 76,  XpMode.NORMAL,    rune(COSMIC, 1), rune(SOUL, 2));
        addSpell("Resurrect Lesser Ghost",     ARCEUUS, UTILITY, 38, false, 38,  XpMode.NORMAL,    rune(MIND, 5), rune(COSMIC, 1));
        addSpell("Resurrect Lesser Skeleton",  ARCEUUS, UTILITY, 38, false, 38,  XpMode.NORMAL,    rune(MIND, 5), rune(COSMIC, 1));
        addSpell("Resurrect Lesser Zombie",    ARCEUUS, UTILITY, 38, false, 38,  XpMode.NORMAL,    rune(MIND, 5), rune(COSMIC, 1));
        addSpell("Resurrect Superior Ghost",   ARCEUUS, UTILITY, 57, false, 57,  XpMode.NORMAL,    rune(DEATH, 2), rune(COSMIC, 1), rune(SOUL, 2));
        addSpell("Resurrect Superior Skeleton",ARCEUUS, UTILITY, 57, false, 57,  XpMode.NORMAL,    rune(DEATH, 2), rune(COSMIC, 1), rune(SOUL, 2));
        addSpell("Resurrect Superior Zombie",  ARCEUUS, UTILITY, 57, false, 57,  XpMode.NORMAL,    rune(DEATH, 2), rune(COSMIC, 1), rune(SOUL, 2));
        addSpell("Resurrect Greater Ghost",    ARCEUUS, UTILITY, 76, false, 76,  XpMode.NORMAL,    rune(BLOOD, 2), rune(COSMIC, 1), rune(SOUL, 3));
        addSpell("Resurrect Greater Skeleton", ARCEUUS, UTILITY, 76, false, 76,  XpMode.NORMAL,    rune(BLOOD, 2), rune(COSMIC, 1), rune(SOUL, 3));
        addSpell("Resurrect Greater Zombie",   ARCEUUS, UTILITY, 76, false, 76,  XpMode.NORMAL,    rune(BLOOD, 2), rune(COSMIC, 1), rune(SOUL, 3));

        // =====================================================================
        // ARCEUUS SPELLBOOK - TELEPORTS
        // =====================================================================
        addSpell("Arceuus Library Teleport",      ARCEUUS, TELEPORT, 6,  false, 27, XpMode.NORMAL, rune(EARTH, 2), rune(LAW, 1));
        addSpell("Draynor Manor Teleport",        ARCEUUS, TELEPORT, 17, false, 37, XpMode.NORMAL, rune(EARTH, 1), rune(WATER, 1), rune(LAW, 1));
        addSpell("Battlefront Teleport",          ARCEUUS, TELEPORT, 23, false, 46, XpMode.NORMAL, rune(EARTH, 1), rune(FIRE, 1), rune(LAW, 1));
        addSpell("Mind Altar Teleport",           ARCEUUS, TELEPORT, 28, false, 55, XpMode.NORMAL, rune(MIND, 2), rune(LAW, 1));
        addSpell("Respawn Teleport",              ARCEUUS, TELEPORT, 34, false, 65, XpMode.NORMAL, rune(LAW, 1), rune(SOUL, 1));
        addSpell("Salve Graveyard Teleport",      ARCEUUS, TELEPORT, 40, false, 60, XpMode.NORMAL, rune(LAW, 2), rune(SOUL, 2));
        addSpell("Fenkenstrain's Castle Teleport",ARCEUUS, TELEPORT, 48, false, 62, XpMode.NORMAL, rune(EARTH, 1), rune(LAW, 1), rune(SOUL, 1));
        addSpell("West Ardougne Teleport",        ARCEUUS, TELEPORT, 61, false, 68, XpMode.NORMAL, rune(LAW, 2), rune(SOUL, 2));
        addSpell("Harmony Island Teleport",       ARCEUUS, TELEPORT, 65, false, 74, XpMode.NORMAL, rune(LAW, 1), rune(NATURE, 1), rune(SOUL, 1));
        addSpell("Cemetery Teleport",             ARCEUUS, TELEPORT, 71, false, 78, XpMode.NORMAL, rune(LAW, 1), rune(BLOOD, 1), rune(SOUL, 1));
        addSpell("Barrows Teleport",              ARCEUUS, TELEPORT, 83, false, 83, XpMode.NORMAL, rune(LAW, 2), rune(BLOOD, 1), rune(SOUL, 2));
        addSpell("Ape Atoll Teleport (Arceuus)",  ARCEUUS, TELEPORT, 90, false, 90, XpMode.NORMAL, rune(LAW, 2), rune(BLOOD, 2), rune(SOUL, 2));
    }

    private static RuneRequirement rune(Rune rune, int qty)
    {
        return new RuneRequirement(rune, qty);
    }

    private static void addSpell(String name, Spellbook book, SpellCategory cat, int level,
                                 boolean profitLoss, double xpPerCast, XpMode xpMode,
                                 RuneRequirement... reqs)
    {
        ALL_SPELLS.add(new SpellData(name, book, cat, level, profitLoss, xpPerCast, xpMode, reqs));
    }

    public static List<SpellData> getAllSpells()
    {
        return Collections.unmodifiableList(ALL_SPELLS);
    }
}

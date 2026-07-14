package com.castcalc;

import net.runelite.api.ItemID;

public enum Rune
{
    AIR("Air", ItemID.AIR_RUNE),
    WATER("Water", ItemID.WATER_RUNE),
    EARTH("Earth", ItemID.EARTH_RUNE),
    FIRE("Fire", ItemID.FIRE_RUNE),
    MIND("Mind", ItemID.MIND_RUNE),
    BODY("Body", ItemID.BODY_RUNE),
    COSMIC("Cosmic", ItemID.COSMIC_RUNE),
    CHAOS("Chaos", ItemID.CHAOS_RUNE),
    NATURE("Nature", ItemID.NATURE_RUNE),
    LAW("Law", ItemID.LAW_RUNE),
    DEATH("Death", ItemID.DEATH_RUNE),
    ASTRAL("Astral", ItemID.ASTRAL_RUNE),
    BLOOD("Blood", ItemID.BLOOD_RUNE),
    SOUL("Soul", ItemID.SOUL_RUNE),
    WRATH("Wrath", ItemID.WRATH_RUNE);

    private final String name;
    private final int itemId;

    Rune(String name, int itemId)
    {
        this.name = name;
        this.itemId = itemId;
    }

    public String getName()
    {
        return name;
    }

    public int getItemId()
    {
        return itemId;
    }

    @Override
    public String toString()
    {
        return name;
    }
}

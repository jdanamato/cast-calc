package com.castcalc;

public enum SpellCategory
{
    COMBAT("Combat"),
    TELEPORT("Teleport"),
    UTILITY("Utility"),
    CURSE("Curse"),
    SUPPORT("Support");

    private final String name;

    SpellCategory(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}

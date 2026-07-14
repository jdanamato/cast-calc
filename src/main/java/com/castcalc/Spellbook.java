package com.castcalc;

public enum Spellbook
{
    STANDARD("Standard"),
    ANCIENT("Ancient"),
    LUNAR("Lunar"),
    ARCEUUS("Arceuus");

    private final String name;

    Spellbook(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}

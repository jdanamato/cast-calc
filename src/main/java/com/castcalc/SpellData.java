package com.castcalc;

import java.util.Arrays;
import java.util.List;

public class SpellData
{
    private final String name;
    private final Spellbook spellbook;
    private final SpellCategory category;
    private final int levelRequired;
    private final boolean hasProfitLoss;
    private final double xpPerCast;
    private final XpMode xpMode;
    private final List<RuneRequirement> runeRequirements;

    public SpellData(String name, Spellbook spellbook, SpellCategory category,
                     int levelRequired, boolean hasProfitLoss,
                     double xpPerCast, XpMode xpMode,
                     RuneRequirement... requirements)
    {
        this.name = name;
        this.spellbook = spellbook;
        this.category = category;
        this.levelRequired = levelRequired;
        this.hasProfitLoss = hasProfitLoss;
        this.xpPerCast = xpPerCast;
        this.xpMode = xpMode;
        this.runeRequirements = Arrays.asList(requirements);
    }

    public String getName() { return name; }
    public Spellbook getSpellbook() { return spellbook; }
    public SpellCategory getCategory() { return category; }
    public int getLevelRequired() { return levelRequired; }
    public boolean hasProfitLoss() { return hasProfitLoss; }
    public double getXpPerCast() { return xpPerCast; }
    public XpMode getXpMode() { return xpMode; }
    public List<RuneRequirement> getRuneRequirements() { return runeRequirements; }

    @Override
    public String toString() { return name; }
}

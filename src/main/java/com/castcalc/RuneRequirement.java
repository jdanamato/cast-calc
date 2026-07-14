package com.castcalc;

public class RuneRequirement
{
    private final Rune rune;
    private final int quantity;

    public RuneRequirement(Rune rune, int quantity)
    {
        this.rune = rune;
        this.quantity = quantity;
    }

    public Rune getRune()
    {
        return rune;
    }

    public int getQuantity()
    {
        return quantity;
    }
}

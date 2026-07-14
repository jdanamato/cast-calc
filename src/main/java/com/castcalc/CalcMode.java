package com.castcalc;

/**
 * The three modes of the level/goal calculator.
 * Each mode takes a different input and shows different output per spell row.
 */
public enum CalcMode
{
    CASTS("Casts",
        "Enter a number of casts to see total cost and XP gained per spell."),

    LEVEL("Level",
        "Enter a target level (or XP) to see total cost and casts needed from your current level."),

    COST("Cost",
        "Enter a GP budget to see how many casts you can afford and what level you'd reach.");

    private final String label;
    private final String helperText;

    CalcMode(String label, String helperText)
    {
        this.label = label;
        this.helperText = helperText;
    }

    public String getHelperText()
    {
        return helperText;
    }

    @Override
    public String toString()
    {
        return label;
    }
}

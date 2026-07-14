package com.castcalc;

/**
 * Describes how a spell's Magic XP should be treated in the level calculator.
 *
 * NORMAL   - Has a clear fixed XP value. Fully calculable in all three modes.
 * COMBAT   - Combat spell. XP per cast is variable in practice (splash vs hit).
 *            Muted in Level mode with "Combat spell" label.
 * REANIMATE - Gives a small amount of Magic XP but not a practical training method.
 *            Muted in Level mode with "Negligible XP" label.
 * VARIES   - XP changes depending on context (e.g. Enchant Crossbow Bolt by bolt type).
 *            Muted in Level mode with "XP varies" label.
 */
public enum XpMode
{
    NORMAL,
    COMBAT,
    REANIMATE,
    VARIES;

    /** Whether this spell is fully calculable in Level mode. */
    public boolean isCalculable()
    {
        return this == NORMAL;
    }

    /**
     * Short label shown in Level mode for non-calculable spells.
     * Returns null for NORMAL (no label needed).
     */
    public String getMutedLabel()
    {
        switch (this)
        {
            case COMBAT:    return "Combat spell";
            case REANIMATE: return "Negligible Magic XP";
            case VARIES:    return "XP varies";
            default:        return null;
        }
    }
}

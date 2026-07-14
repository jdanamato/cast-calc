package com.castcalc;

/**
 * Precomputed OSRS XP table for levels 1-99.
 *
 * Formula: XP(n) = floor(1/4 * sum(i=1 to n-1, floor(i + 300 * 2^(i/7))))
 * Source: OSRS Wiki - https://oldschool.runescape.wiki/w/Experience
 *
 * Used by the level calculator to convert between levels and XP values.
 */
public final class MagicXpTable
{
    private MagicXpTable() {}

    /** XP_TABLE[level] = total XP required to reach that level. Index 1 = 0 XP. */
    private static final long[] XP_TABLE = new long[100];

    static
    {
        XP_TABLE[1] = 0;
        for (int level = 2; level <= 99; level++)
        {
            long points = 0;
            for (int i = 1; i < level; i++)
            {
                points += (long) Math.floor(i + 300.0 * Math.pow(2.0, i / 7.0));
            }
            XP_TABLE[level] = points / 4;
        }
    }

    /** XP required to reach the given level (clamped to 1-99). */
    public static long xpForLevel(int level)
    {
        if (level <= 1) return 0;
        if (level >= 99) return XP_TABLE[99];
        return XP_TABLE[level];
    }

    /** Current level at the given cumulative XP total. */
    public static int levelForXp(long xp)
    {
        if (xp <= 0) return 1;
        for (int level = 99; level >= 2; level--)
        {
            if (xp >= XP_TABLE[level]) return level;
        }
        return 1;
    }

    /** Formats an XP value as a short string (e.g. 737,627 -> "737.6K"). */
    public static String formatXp(long xp)
    {
        if (xp >= 1_000_000) return String.format("%.1fM", xp / 1_000_000.0);
        if (xp >= 1_000)     return String.format("%.1fK", xp / 1_000.0);
        return String.valueOf(xp);
    }
}

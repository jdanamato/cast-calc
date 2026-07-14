package com.castcalc;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("castcalc")
public interface CastCalcConfig extends Config
{
    @ConfigSection(
        name = "General",
        description = "General plugin settings",
        position = 0
    )
    String generalSection = "general";

    @ConfigItem(
        keyName = "useEquippedGear",
        name = "Use Equipped Gear",
        description = "Factor in equipped rune-saving gear (staves, tomes, etc.) when calculating costs",
        section = generalSection,
        position = 0
    )
    default boolean useEquippedGear()
    {
        return true;
    }

    @ConfigItem(
        keyName = "showRuneBreakdown",
        name = "Show Rune Breakdown",
        description = "Show per-rune cost breakdown in spell details",
        section = generalSection,
        position = 1
    )
    default boolean showRuneBreakdown()
    {
        return true;
    }

    @ConfigItem(
        keyName = "defaultSpellbook",
        name = "Default Spellbook Filter",
        description = "Which spellbook to show by default when opening the panel",
        section = generalSection,
        position = 2
    )
    default SpellbookFilter defaultSpellbook()
    {
        return SpellbookFilter.ALL;
    }

    @ConfigItem(
        keyName = "showLevel",
        name = "Show Level Requirement",
        description = "Display the magic level required next to each spell",
        section = generalSection,
        position = 3
    )
    default boolean showLevel()
    {
        return true;
    }

    @ConfigItem(
        keyName = "highlightProfitable",
        name = "Highlight Profitable Spells",
        description = "Show a green indicator on utility spells that are profitable",
        section = generalSection,
        position = 4
    )
    default boolean highlightProfitable()
    {
        return true;
    }

    @ConfigItem(
        keyName = "detailTextSize",
        name = "Expanded Card Text Size",
        description = "Text size used inside expanded spell details (rune breakdown, profit/loss)",
        section = generalSection,
        position = 5
    )
    default DetailTextSize detailTextSize()
    {
        return DetailTextSize.SMALL;
    }

    enum DetailTextSize
    {
        SMALL("Small"),
        LARGE("Large");

        private final String name;

        DetailTextSize(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    enum SpellbookFilter
    {
        ALL("All"),
        STANDARD("Standard"),
        ANCIENT("Ancient"),
        LUNAR("Lunar"),
        ARCEUUS("Arceuus");

        private final String name;

        SpellbookFilter(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }
}

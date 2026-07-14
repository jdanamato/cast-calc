package com.castcalc;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.awt.Font;

/**
 * Centralized style configuration for the Cast Calc panel.
 *
 * Think of this like a CSS file or design tokens. When you change a value here,
 * the change propagates everywhere that uses it.
 *
 * Organized by section:
 *   - COLORS        : color tokens
 *   - TYPOGRAPHY    : font sizes and font factories
 *   - SPACING       : padding and gap values
 *   - LAYOUT        : fixed widths, column reservations
 *   - COMPONENTS    : pre-built borders and helpers
 *
 */
public final class CastCalcStyle
{
    private CastCalcStyle() {} // utility class, no instances

    // COLORS

    public static final Color BACKGROUND       = ColorScheme.DARK_GRAY_COLOR;
    public static final Color PANEL            = ColorScheme.DARKER_GRAY_COLOR;
    public static final Color PANEL_HOVER      = ColorScheme.DARKER_GRAY_HOVER_COLOR;
    public static final Color PANEL_EXPANDED   = ColorScheme.DARKER_GRAY_HOVER_COLOR;
    public static final Color BORDER           = ColorScheme.MEDIUM_GRAY_COLOR;

    public static final Color TEXT_PRIMARY     = Color.WHITE;
    public static final Color TEXT_MUTED       = ColorScheme.LIGHT_GRAY_COLOR;
    public static final Color TEXT_HIGHLIGHT   = ColorScheme.TEXT_COLOR;
    public static final Color TEXT_PROFIT_TAG  = ColorScheme.TEXT_COLOR; // for P&L subtitle

    public static final Color PROFIT           = ColorScheme.GRAND_EXCHANGE_PRICE;
    public static final Color LOSS             = ColorScheme.GRAND_EXCHANGE_ALCH;
    public static final Color COST_PL          = ColorScheme.GRAND_EXCHANGE_PRICE;

    // TYPOGRAPHY

    /** Used for the panel title ("Cast Calc"). */
    public static final int TITLE_SIZE         = 16;

    /** Used for section headers (selected spell name, "Profit / Loss Calculator"). */
    public static final int SECTION_SIZE       = 14;

    /** Used for everything else: rows, subtitles, labels, dropdowns. */
    public static final int BASE_SIZE          = 14;

    /** Used for dropdown menus and combo boxes. Defaults to BASE_SIZE. */
    public static final int DROPDOWN_FONT_SIZE = 16;

    /** Used for text inputs (search fields). Defaults to BASE_SIZE. */
    public static final int INPUT_FONT_SIZE    = 14;

    /** Expanded-card detail text size when the "Small" setting is active (default). */
    public static final int DETAIL_SIZE_SMALL  = 14;

    /** Expanded-card detail text size when the "Large" setting is active. */
    public static final int DETAIL_SIZE_LARGE  = 16;

    public static Font titleFont()
    {
        return FontManager.getRunescapeBoldFont().deriveFont((float) TITLE_SIZE);
    }

    public static Font sectionFont()
    {
        return FontManager.getRunescapeBoldFont().deriveFont((float) SECTION_SIZE);
    }

    public static Font baseFont()
    {
        return FontManager.getRunescapeFont().deriveFont((float) BASE_SIZE);
    }

    public static Font boldFont()
    {
        return FontManager.getRunescapeBoldFont().deriveFont((float) BASE_SIZE);
    }

    public static Font baseFont(int size)
    {
        return FontManager.getRunescapeFont().deriveFont((float) size);
    }

    public static Font boldFont(int size)
    {
        return FontManager.getRunescapeBoldFont().deriveFont((float) size);
    }

    public static Font dropdownFont()
    {
        return FontManager.getRunescapeFont().deriveFont((float) DROPDOWN_FONT_SIZE);
    }

    public static Font inputFont()
    {
        return FontManager.getRunescapeFont().deriveFont((float) INPUT_FONT_SIZE);
    }

    /**
     * Produces an HTML-wrapped string that JLabel will word-wrap, with
     * the base font size baked in (setFont() alone doesn't size HTML content).
     */
    public static String wrapText(String text, int widthPx, boolean bold)
    {
        String weight = bold ? "bold" : "normal";
        return "<html><body style='width:" + widthPx + "px; font-size:" + BASE_SIZE
            + "pt; font-weight:" + weight + "'>" + escapeHtml(text) + "</body></html>";
    }

    private static String escapeHtml(String s)
    {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // SPACING

    /** Padding inside each spell row. */
    public static final int ROW_PADDING_VERTICAL   = 8;
    public static final int ROW_PADDING_HORIZONTAL = 10;

    /** Padding inside the outer panel. */
    public static final int PANEL_PADDING          = 8;

    /** Gap between stacked rows. */
    public static final int ROW_GAP                = 2;

    /** Gap between major sections (header → list, list → detail). */
    public static final int SECTION_GAP            = 8;

    /** Gap between related elements within a section. */
    public static final int ELEMENT_GAP            = 4;

    /** Horizontal gap between adjacent inline elements (dropdown columns, label + value). */
    public static final int INLINE_GAP             = 6;

    /** Gap between a row icon and its text labels. */
    public static final int ICON_TEXT_GAP          = 4;

    /** Vertical padding for empty-state and loading messages. */
    public static final int EMPTY_STATE_PADDING    = 12;

    // LAYOUT

    /** Height of text input fields (search, alch search). */
    public static final int INPUT_HEIGHT           = 32;

    /** Height of dropdown rows (spellbook filter, category filter, sort). */
    public static final int DROPDOWN_HEIGHT        = 28;

    /** Size of icons throughout the panel (spell list rows, P&L table inputs/outputs), in pixels. */
    public static final int ICON_SIZE              = 20;

    /**
     * Width reserved for the cost/profit column on the right side of rows.
     * Needs to fit strings like "+1,234 gp" at current font size.
     */
    public static final int COST_COLUMN_WIDTH      = 110;

    /**
     * Width of the wrapping text column in the main spell list.
     * Panel is ~225px wide, minus padding, minus COST_COLUMN_WIDTH.
     */
    public static final int SPELL_ROW_TEXT_WIDTH   = 90;

    /**
     * Width of wrapping text in preset rows (inside the P&L section).
     */
    public static final int PRESET_ROW_TEXT_WIDTH  = 90;

    /**
     * Width of wrapping text in alch search result rows (has item icon on left).
     */
    public static final int ALCH_ROW_TEXT_WIDTH    = 85;

    // COMPONENTS

    /** Standard border for a card-style row: 1px outline + inner padding. */
    public static Border rowBorder()
    {
        return new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, BORDER),
            new EmptyBorder(ROW_PADDING_VERTICAL, ROW_PADDING_HORIZONTAL,
                            ROW_PADDING_VERTICAL, ROW_PADDING_HORIZONTAL)
        );
    }

    /** Lighter border for the gear effects panel at the top. */
    public static Border headerChipBorder()
    {
        return new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, BORDER),
            new EmptyBorder(4, 6, 4, 6)
        );
    }

    /** Padding for text input fields (search, etc.). */
    public static Border inputBorder()
    {
        return new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, BORDER),
            new EmptyBorder(6, 8, 6, 8)
        );
    }
}

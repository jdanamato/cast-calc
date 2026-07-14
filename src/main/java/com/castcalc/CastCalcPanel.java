package com.castcalc;

import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.AsyncBufferedImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.castcalc.CastCalcStyle.*;

/**
 * Cast Calc side panel.
 *
 * Layout: a vertical stack of (header) + (filterable spell list).
 * Each spell row is clickable; clicking expands inline detail content
 * directly below the row. Multiple rows can be expanded simultaneously.
 *
 * The whole panel is scrolled by RuneLite's PluginPanel parent scroll pane
 * (we do NOT add our own JScrollPane).
 *
 * All visual styling (colors, fonts, spacing) lives in {@link CastCalcStyle}.
 */
public class CastCalcPanel extends PluginPanel
{
    private final Client client;
    private final CastCalcConfig config;
    private final CostCalculator calculator;
    private final ItemManager itemManager;
    private final ClientThread clientThread;
    private final SpriteManager spriteManager;

    private final JTextField searchField = new JTextField();
    private final JComboBox<CastCalcConfig.SpellbookFilter> spellbookFilter = new JComboBox<>(CastCalcConfig.SpellbookFilter.values());
    private final JComboBox<SpellCategory> categoryFilter = new JComboBox<>();

    private final JPanel spellListPanel = new JPanel();
    private JPanel gearEffectsPanel;

    /** Names of spells whose detail is currently expanded. */
    private final Set<String> expandedSpells = new HashSet<>();

    private String currentSort = "Name";
    private boolean showProfitLossOnly = false;
    private boolean initialized = false;

    // Level calculator state
    private int currentMagicLevel = 1;
    private CalcMode calcMode = CalcMode.CASTS;
    private boolean calcVisible = false;
    private boolean updatingLevelFields = false;

    // Calc UI components — wired up in buildCalcPanel()
    private JPanel calcSection;
    private JTextField castsInputField;
    private JTextField levelInputField;
    private JTextField xpInputField;
    private JTextField costInputField;
    private JPanel calcInputArea;
    private JLabel helperTextLabel;
    private JLabel currentLevelLabel;

    public CastCalcPanel(Client client, CastCalcConfig config, CostCalculator calculator,
                         ItemManager itemManager, ClientThread clientThread, SpriteManager spriteManager)
    {
        this.client = client;
        this.config = config;
        this.calculator = calculator;
        this.itemManager = itemManager;
        this.clientThread = clientThread;
        this.spriteManager = spriteManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));

        JPanel header = buildHeaderPanel();
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(header);

        spellListPanel.setLayout(new BoxLayout(spellListPanel, BoxLayout.Y_AXIS));
        spellListPanel.setBackground(BACKGROUND);
        spellListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(spellListPanel);

        JLabel loading = new JLabel("Loading spells...");
        loading.setForeground(TEXT_MUTED);
        loading.setFont(baseFont());
        loading.setBorder(new EmptyBorder(EMPTY_STATE_PADDING, 0, EMPTY_STATE_PADDING, 0));
        loading.setAlignmentX(Component.LEFT_ALIGNMENT);
        spellListPanel.add(loading);
    }

    public void initialize()
    {
        initialized = true;
        spellbookFilter.setSelectedItem(config.defaultSpellbook());
        refreshSpellList();
    }

    /**
     * Called whenever the player's Magic level changes (on login, or on level-up).
     * Updates the level display in the calc section and refreshes the spell list
     * if the calculator is active.
     */
    public void setCurrentMagicLevel(int level)
    {
        if (level <= 0) return; // not logged in yet
        this.currentMagicLevel = level;
        if (currentLevelLabel != null)
        {
            long xp = MagicXpTable.xpForLevel(level);
            currentLevelLabel.setText("Your level: " + level + " (" + MagicXpTable.formatXp(xp) + " XP)");
        }
        // Refresh helper text in Level mode since it includes the current level number
        if (helperTextLabel != null && calcMode == CalcMode.LEVEL)
        {
            helperTextLabel.setText(calcMode.getHelperText());
        }
        if (calcVisible && initialized)
        {
            refreshSpellList();
        }
    }

    // HEADER

    private JPanel buildHeaderPanel()
    {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BACKGROUND);

        // Title row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(BACKGROUND);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.setBorder(new EmptyBorder(0, 0, SECTION_GAP, 0));

        JLabel title = new JLabel("Cast Calc");
        title.setFont(titleFont());
        title.setForeground(TEXT_HIGHLIGHT);
        titleRow.add(title, BorderLayout.WEST);

        JLabel refreshBtn = new JLabel("Refresh");
        refreshBtn.setFont(baseFont());
        refreshBtn.setForeground(TEXT_MUTED);
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                updatePrices();
                refreshBtn.setText("Updated!");
                Timer resetTimer = new Timer(2000, evt -> refreshBtn.setText("Refresh"));
                resetTimer.setRepeats(false);
                resetTimer.start();
            }

            @Override public void mouseEntered(MouseEvent e) { refreshBtn.setForeground(TEXT_HIGHLIGHT); }
            @Override public void mouseExited(MouseEvent e) { refreshBtn.setForeground(TEXT_MUTED); }
        });
        titleRow.add(refreshBtn, BorderLayout.EAST);
        header.add(titleRow);

        gearEffectsPanel = buildGearEffectsPanel();
        gearEffectsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(gearEffectsPanel);
        header.add(Box.createVerticalStrut(SECTION_GAP));

        searchField.setBackground(PANEL);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(TEXT_PRIMARY);
        searchField.setFont(inputFont());
        searchField.setBorder(inputBorder());
        searchField.setToolTipText("Search spells...");
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, INPUT_HEIGHT));
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchField.addKeyListener(new KeyAdapter()
        {
            @Override public void keyReleased(KeyEvent e) { refreshSpellList(); }
        });
        header.add(searchField);
        header.add(Box.createVerticalStrut(ELEMENT_GAP + 2));

        JPanel filterRow = new JPanel(new GridLayout(1, 2, INLINE_GAP, 0));
        filterRow.setBackground(BACKGROUND);
        filterRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, DROPDOWN_HEIGHT));
        filterRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        styleComboBox(spellbookFilter);
        spellbookFilter.addActionListener(e -> refreshSpellList());
        filterRow.add(spellbookFilter);

        categoryFilter.addItem(null);
        for (SpellCategory cat : SpellCategory.values())
        {
            categoryFilter.addItem(cat);
        }
        categoryFilter.setRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "All Types" : value.toString());
                return this;
            }
        });
        styleComboBox(categoryFilter);
        categoryFilter.addActionListener(e -> refreshSpellList());
        filterRow.add(categoryFilter);

        header.add(filterRow);
        header.add(Box.createVerticalStrut(ELEMENT_GAP));

        JPanel sortRow = new JPanel(new BorderLayout(INLINE_GAP, 0));
        sortRow.setBackground(BACKGROUND);
        sortRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, DROPDOWN_HEIGHT));
        sortRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] sortOptions = {"Name", "Cost (low)", "Cost (high)", "Level (low)", "Level (high)", "Profit (high)", "Profit (low)"};
        JComboBox<String> sortBox = new JComboBox<>(sortOptions);
        styleComboBox(sortBox);
        sortBox.addActionListener(e ->
        {
            currentSort = (String) sortBox.getSelectedItem();
            refreshSpellList();
        });
        JLabel sortLabel = new JLabel("Sort:");
        sortLabel.setFont(baseFont());
        sortLabel.setForeground(TEXT_MUTED);
        sortRow.add(sortLabel, BorderLayout.WEST);
        sortRow.add(sortBox, BorderLayout.CENTER);

        header.add(sortRow);
        header.add(Box.createVerticalStrut(ELEMENT_GAP));

        JCheckBox plOnlyToggle = new JCheckBox("Show P&L spells only");
        plOnlyToggle.setFont(baseFont());
        plOnlyToggle.setForeground(TEXT_MUTED);
        plOnlyToggle.setBackground(BACKGROUND);
        plOnlyToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        plOnlyToggle.setToolTipText("Profit & Loss. Filters the list to only show spells where you can calculate whether casting makes or loses GP.");
        plOnlyToggle.addActionListener(e ->
        {
            showProfitLossOnly = plOnlyToggle.isSelected();
            refreshSpellList();
        });
        header.add(plOnlyToggle);
        header.add(Box.createVerticalStrut(ELEMENT_GAP));

        // Level Calculator toggle
        JCheckBox calcToggle = new JCheckBox("Level Calculator");
        calcToggle.setFont(baseFont());
        calcToggle.setForeground(TEXT_MUTED);
        calcToggle.setBackground(BACKGROUND);
        calcToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        calcToggle.setToolTipText("Calculate runes and cost needed to reach a target level or XP.");
        calcToggle.addActionListener(e ->
        {
            calcVisible = calcToggle.isSelected();
            calcSection.setVisible(calcVisible);
            refreshSpellList();
        });
        header.add(calcToggle);
        header.add(Box.createVerticalStrut(ELEMENT_GAP));

        calcSection = buildCalcPanel();
        calcSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        calcSection.setVisible(false);
        header.add(calcSection);
        header.add(Box.createVerticalStrut(SECTION_GAP));

        return header;
    }

    private JPanel buildGearEffectsPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL);
        panel.setBorder(headerChipBorder());
        return panel;
    }

    private void rebuildGearEffects()
    {
        if (gearEffectsPanel == null) return;
        gearEffectsPanel.removeAll();

        if (config.useEquippedGear() && client != null)
        {
            List<String> effects = RuneSavingGear.getCachedEffects();
            if (effects.isEmpty())
            {
                addGearLabel("No rune-saving gear detected", TEXT_MUTED);
            }
            else
            {
                for (String effect : effects)
                {
                    addGearLabel(effect, PROFIT);
                }
            }
        }
        else
        {
            addGearLabel("Gear detection: OFF", TEXT_MUTED);
        }

        gearEffectsPanel.revalidate();
        gearEffectsPanel.repaint();
    }

    private void addGearLabel(String text, Color color)
    {
        JLabel label = new JLabel(text);
        label.setFont(baseFont());
        label.setForeground(color);
        gearEffectsPanel.add(label);
    }

    // =========================================================================
    // LEVEL CALCULATOR PANEL
    // =========================================================================

    private JPanel buildCalcPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Mode dropdown row
        JPanel modeRow = new JPanel(new BorderLayout(INLINE_GAP, 0));
        modeRow.setBackground(BACKGROUND);
        modeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        modeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, DROPDOWN_HEIGHT));

        JLabel modeLabel = new JLabel("Mode:");
        modeLabel.setFont(baseFont());
        modeLabel.setForeground(TEXT_MUTED);
        modeRow.add(modeLabel, BorderLayout.WEST);

        JComboBox<CalcMode> modeDropdown = new JComboBox<>(CalcMode.values());
        styleComboBox(modeDropdown);
        modeDropdown.addActionListener(e ->
        {
            calcMode = (CalcMode) modeDropdown.getSelectedItem();
            switchCalcInputArea();
            refreshSpellList();
        });
        modeRow.add(modeDropdown, BorderLayout.CENTER);
        panel.add(modeRow);
        panel.add(Box.createVerticalStrut(ELEMENT_GAP));

        // Current level display
        currentLevelLabel = new JLabel("Log in to see your current level");
        currentLevelLabel.setFont(baseFont());
        currentLevelLabel.setForeground(TEXT_MUTED);
        currentLevelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(currentLevelLabel);
        panel.add(Box.createVerticalStrut(ELEMENT_GAP));

        // Dynamic input area (swaps when mode changes)
        calcInputArea = new JPanel();
        calcInputArea.setLayout(new BoxLayout(calcInputArea, BoxLayout.Y_AXIS));
        calcInputArea.setBackground(BACKGROUND);
        calcInputArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(calcInputArea);
        panel.add(Box.createVerticalStrut(ELEMENT_GAP));

        // Helper text describing the current mode
        helperTextLabel = new JLabel();
        helperTextLabel.setFont(baseFont());
        helperTextLabel.setForeground(TEXT_MUTED);
        helperTextLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(helperTextLabel);
        panel.add(Box.createVerticalStrut(ELEMENT_GAP));

        switchCalcInputArea(); // initialise for default mode (CASTS)
        return panel;
    }

    /**
     * Rebuilds the input area inside the calc panel for the current mode.
     * Called on init and whenever the user changes the mode dropdown.
     */
    private void switchCalcInputArea()
    {
        if (calcInputArea == null) return;
        calcInputArea.removeAll();

        switch (calcMode)
        {
            case CASTS:
                castsInputField = createCalcTextField("e.g. 1000");
                addDocumentListener(castsInputField, () -> refreshSpellList());
                calcInputArea.add(calcLabeledRow("Casts:", castsInputField));
                break;

            case LEVEL:
                levelInputField = createCalcTextField("1 - 99");
                xpInputField    = createCalcTextField("e.g. 737,627");
                calcInputArea.add(calcLabeledRow("Target level:", levelInputField));
                calcInputArea.add(Box.createVerticalStrut(ELEMENT_GAP));
                calcInputArea.add(calcLabeledRow("Target XP:",    xpInputField));
                addLevelXpSync();
                break;

            case COST:
                costInputField = createCalcTextField("e.g. 500k");
                addDocumentListener(costInputField, () -> refreshSpellList());
                calcInputArea.add(calcLabeledRow("Budget:", costInputField));
                break;
        }

        if (helperTextLabel != null)
        {
            helperTextLabel.setText(calcMode.getHelperText());
        }

        calcInputArea.revalidate();
        calcInputArea.repaint();
    }

    /** Creates a styled single-line text field for the calc panel. */
    private JTextField createCalcTextField(String tooltip)
    {
        JTextField field = new JTextField();
        field.setBackground(PANEL);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setFont(inputFont());
        field.setBorder(inputBorder());
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, INPUT_HEIGHT));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setToolTipText(tooltip);
        return field;
    }

    /** Builds a two-column row: label on the left, field on the right. */
    private JPanel calcLabeledRow(String labelText, JTextField field)
    {
        JPanel row = new JPanel(new BorderLayout(INLINE_GAP, 0));
        row.setBackground(BACKGROUND);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, INPUT_HEIGHT));

        JLabel label = new JLabel(labelText);
        label.setFont(baseFont());
        label.setForeground(TEXT_MUTED);
        label.setPreferredSize(new Dimension(80, INPUT_HEIGHT));
        row.add(label, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    /** Adds a simple DocumentListener that fires onChange each time the field content changes. */
    private void addDocumentListener(JTextField field, Runnable onChange)
    {
        field.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override public void insertUpdate(DocumentEvent e) { onChange.run(); }
            @Override public void removeUpdate(DocumentEvent e) { onChange.run(); }
            @Override public void changedUpdate(DocumentEvent e) { onChange.run(); }
        });
    }

    /**
     * Wires the level and XP fields so they stay in sync.
     * Typing a level updates the XP field and vice versa.
     * Uses updatingLevelFields flag to prevent infinite feedback loops.
     */
    private void addLevelXpSync()
    {
        // Level -> XP
        levelInputField.getDocument().addDocumentListener(new DocumentListener()
        {
            private void update()
            {
                if (updatingLevelFields) return;
                try
                {
                    int level = Integer.parseInt(levelInputField.getText().trim());
                    if (level >= 1 && level <= 99)
                    {
                        updatingLevelFields = true;
                        xpInputField.setText(String.valueOf(MagicXpTable.xpForLevel(level)));
                        updatingLevelFields = false;
                    }
                }
                catch (NumberFormatException ignored) {}
                refreshSpellList();
            }
            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        });

        // XP -> Level
        xpInputField.getDocument().addDocumentListener(new DocumentListener()
        {
            private void update()
            {
                if (updatingLevelFields) return;
                try
                {
                    long xp = Long.parseLong(xpInputField.getText().trim().replaceAll("[,\\s]", ""));
                    int level = MagicXpTable.levelForXp(xp);
                    updatingLevelFields = true;
                    levelInputField.setText(String.valueOf(level));
                    updatingLevelFields = false;
                }
                catch (NumberFormatException ignored) {}
                refreshSpellList();
            }
            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        });
    }

    // =========================================================================
    // CALC VALUE HELPERS
    // =========================================================================

    /**
     * Returns the right-column value string for a spell row when the
     * level calculator is active. Falls back to cost-per-cast display
     * if inputs are missing or invalid.
     */
    private String calcRightValue(SpellData spell, long costPerCast)
    {
        switch (calcMode)
        {
            case CASTS:
            {
                long n = parseCastsInput();
                if (n <= 0) return formatGp(costPerCast) + " gp";
                return formatGp(costPerCast * n) + " gp";
            }
            case LEVEL:
            {
                if (!spell.getXpMode().isCalculable())
                {
                    String label = spell.getXpMode().getMutedLabel();
                    return label != null ? label : "-";
                }
                long targetXp = parseTargetXp();
                long currentXp = MagicXpTable.xpForLevel(currentMagicLevel);
                if (targetXp <= currentXp || spell.getXpPerCast() <= 0)
                    return formatGp(costPerCast) + " gp";
                long castsNeeded = (long) Math.ceil((targetXp - currentXp) / spell.getXpPerCast());
                return formatGp(costPerCast * castsNeeded) + " gp";
            }
            case COST:
            {
                long budget = parseCostInput();
                if (budget <= 0) return formatGp(costPerCast) + " gp";
                if (costPerCast == 0) return "Free";
                return formatCasts(budget / costPerCast) + " casts";
            }
            default:
                return formatGp(costPerCast) + " gp";
        }
    }

    /** Returns the right-column text color for a spell row in calc mode. */
    private Color calcRightColor(SpellData spell)
    {
        if (calcMode == CalcMode.LEVEL && !spell.getXpMode().isCalculable()) return TEXT_MUTED;
        if (calcMode == CalcMode.COST && calculator.calculateCastCost(spell, config.useEquippedGear()) == 0) return PROFIT;
        return TEXT_HIGHLIGHT;
    }

    /**
     * Returns the optional secondary line shown below the subtitle in calc mode.
     * Returns null if nothing should be shown (e.g. non-calculable spell in Level mode).
     */
    private String calcSecondaryLine(SpellData spell, long costPerCast)
    {
        switch (calcMode)
        {
            case CASTS:
            {
                long n = parseCastsInput();
                if (n <= 0 || !spell.getXpMode().isCalculable()) return null;
                long xpGained = (long) (spell.getXpPerCast() * n);
                long currentXp = MagicXpTable.xpForLevel(currentMagicLevel);
                int levelReached = MagicXpTable.levelForXp(currentXp + xpGained);
                return MagicXpTable.formatXp(xpGained) + " XP  ->  Lvl " + levelReached;
            }
            case LEVEL:
            {
                if (!spell.getXpMode().isCalculable()) return null;
                long targetXp = parseTargetXp();
                long currentXp = MagicXpTable.xpForLevel(currentMagicLevel);
                if (targetXp <= currentXp || spell.getXpPerCast() <= 0) return null;
                long castsNeeded = (long) Math.ceil((targetXp - currentXp) / spell.getXpPerCast());
                return formatCasts(castsNeeded) + " casts";
            }
            case COST:
            {
                long budget = parseCostInput();
                if (budget <= 0 || !spell.getXpMode().isCalculable()) return null;
                long casts = costPerCast > 0 ? budget / costPerCast : 0;
                long xpGained = (long) (spell.getXpPerCast() * casts);
                long currentXp = MagicXpTable.xpForLevel(currentMagicLevel);
                int levelReached = MagicXpTable.levelForXp(currentXp + xpGained);
                return MagicXpTable.formatXp(xpGained) + " XP  ->  Lvl " + levelReached;
            }
            default:
                return null;
        }
    }

    // =========================================================================
    // INPUT PARSERS
    // =========================================================================

    /** Parses the Casts input field. Supports shorthand (1k, 500k, 1m). */
    private long parseCastsInput()
    {
        return castsInputField == null ? 0 : parseShorthand(castsInputField.getText());
    }

    /** Parses the target level field (1-99). Returns 0 if invalid. */
    private int parseTargetLevel()
    {
        if (levelInputField == null) return 0;
        try
        {
            int level = Integer.parseInt(levelInputField.getText().trim());
            return (level >= 1 && level <= 99) ? level : 0;
        }
        catch (NumberFormatException e) { return 0; }
    }

    /**
     * Parses the XP input field. If valid, returns the raw XP value.
     * Falls back to xpForLevel(parseTargetLevel()) if the XP field is empty.
     */
    private long parseTargetXp()
    {
        if (xpInputField != null)
        {
            try
            {
                long xp = Long.parseLong(xpInputField.getText().trim().replaceAll("[,\\s]", ""));
                if (xp > 0) return xp;
            }
            catch (NumberFormatException ignored) {}
        }
        int level = parseTargetLevel();
        return level > 0 ? MagicXpTable.xpForLevel(level) : 0;
    }

    /** Parses the Cost/budget input field. Supports shorthand (500k, 1.5m). */
    private long parseCostInput()
    {
        return costInputField == null ? 0 : parseShorthand(costInputField.getText());
    }

    /**
     * Parses a number string that may include a k/m suffix shorthand.
     * Examples: "1000" -> 1000, "1k" -> 1000, "1.5m" -> 1500000.
     */
    private long parseShorthand(String text)
    {
        if (text == null) return 0;
        String t = text.trim().toLowerCase().replaceAll("[,\\s]", "");
        if (t.isEmpty()) return 0;
        try
        {
            if (t.endsWith("m")) return (long) (Double.parseDouble(t.substring(0, t.length() - 1)) * 1_000_000);
            if (t.endsWith("k")) return (long) (Double.parseDouble(t.substring(0, t.length() - 1)) * 1_000);
            return Long.parseLong(t);
        }
        catch (NumberFormatException e) { return 0; }
    }

    /** Formats a cast count as a short string (e.g. 4231 -> "4.2K"). */
    private static String formatCasts(long casts)
    {
        if (casts >= 1_000_000) return String.format("%.1fM", casts / 1_000_000.0);
        if (casts >= 10_000)    return String.format("%.0fK", casts / 1_000.0);
        if (casts >= 1_000)     return String.format("%.1fK", casts / 1_000.0);
        return String.format("%,d", casts);
    }

    public void refreshSpellList()
    {
        if (!initialized) return;
        spellListPanel.removeAll();

        String query = searchField.getText().toLowerCase().trim();
        CastCalcConfig.SpellbookFilter bookFilter = (CastCalcConfig.SpellbookFilter) spellbookFilter.getSelectedItem();
        SpellCategory catFilter = (SpellCategory) categoryFilter.getSelectedItem();

        List<SpellData> filtered = SpellDatabase.getAllSpells().stream()
            .filter(spell ->
            {
                if (bookFilter != null && bookFilter != CastCalcConfig.SpellbookFilter.ALL)
                {
                    if (!spell.getSpellbook().toString().equals(bookFilter.toString())) return false;
                }
                if (catFilter != null && spell.getCategory() != catFilter) return false;
                if (!query.isEmpty() && !spell.getName().toLowerCase().contains(query)) return false;
                if (showProfitLossOnly && !spell.hasProfitLoss()) return false;
                return true;
            })
            .collect(Collectors.toList());

        boolean useGear = config.useEquippedGear();
        switch (currentSort)
        {
            case "Cost (low)":
                filtered.sort((a, b) -> Long.compare(safeCost(a, useGear), safeCost(b, useGear)));
                break;
            case "Cost (high)":
                filtered.sort((a, b) -> Long.compare(safeCost(b, useGear), safeCost(a, useGear)));
                break;
            case "Level (low)":
                filtered.sort((a, b) -> Integer.compare(a.getLevelRequired(), b.getLevelRequired()));
                break;
            case "Level (high)":
                filtered.sort((a, b) -> Integer.compare(b.getLevelRequired(), a.getLevelRequired()));
                break;
            case "Profit (high)":
                // Non-P&L spells go to the bottom; P&L spells sorted by best profit descending
                filtered.sort((a, b) ->
                {
                    boolean aPL = a.hasProfitLoss();
                    boolean bPL = b.hasProfitLoss();
                    if (aPL && !bPL) return -1;
                    if (!aPL && bPL) return 1;
                    if (!aPL) return a.getName().compareToIgnoreCase(b.getName());
                    return Long.compare(bestProfit(b, useGear), bestProfit(a, useGear));
                });
                break;
            case "Profit (low)":
                // Non-P&L spells go to the bottom; P&L spells sorted by worst profit ascending
                filtered.sort((a, b) ->
                {
                    boolean aPL = a.hasProfitLoss();
                    boolean bPL = b.hasProfitLoss();
                    if (aPL && !bPL) return -1;
                    if (!aPL && bPL) return 1;
                    if (!aPL) return a.getName().compareToIgnoreCase(b.getName());
                    return Long.compare(bestProfit(a, useGear), bestProfit(b, useGear));
                });
                break;
            default:
                filtered.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                break;
        }

        JLabel countLabel = new JLabel(filtered.size() + " spell" + (filtered.size() != 1 ? "s" : ""));
        countLabel.setFont(baseFont());
        countLabel.setForeground(TEXT_MUTED);
        countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        countLabel.setBorder(new EmptyBorder(0, 2, ELEMENT_GAP, 0));
        spellListPanel.add(countLabel);

        if (filtered.isEmpty())
        {
            JLabel noResults = new JLabel("No spells found");
            noResults.setForeground(TEXT_MUTED);
            noResults.setFont(baseFont());
            noResults.setBorder(new EmptyBorder(EMPTY_STATE_PADDING, 0, EMPTY_STATE_PADDING, 0));
            noResults.setAlignmentX(Component.LEFT_ALIGNMENT);
            spellListPanel.add(noResults);
        }
        else
        {
            for (SpellData spell : filtered)
            {
                try
                {
                    spellListPanel.add(buildSpellRow(spell));
                    if (expandedSpells.contains(spell.getName()))
                    {
                        spellListPanel.add(buildSpellDetail(spell));
                    }
                    spellListPanel.add(Box.createVerticalStrut(ROW_GAP));
                }
                catch (Exception ex)
                {
                    JLabel errLabel = new JLabel(spell.getName() + " (error)");
                    errLabel.setFont(baseFont());
                    errLabel.setForeground(LOSS);
                    errLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    spellListPanel.add(errLabel);
                    spellListPanel.add(Box.createVerticalStrut(ROW_GAP));
                }
            }
        }

        spellListPanel.revalidate();
        spellListPanel.repaint();
    }

    private JPanel buildSpellRow(SpellData spell)
    {
        boolean useGear = config.useEquippedGear();
        long cost = calculator.calculateCastCost(spell, useGear);
        boolean isExpanded = expandedSpells.contains(spell.getName());

        String subtitle = spell.getSpellbook().toString();
        if (config.showLevel()) subtitle += " - Lvl " + spell.getLevelRequired();
        subtitle += " - " + spell.getCategory().toString();
        if (spell.hasProfitLoss()) subtitle += " (P&L)";

        Color subtitleColor = spell.hasProfitLoss() && config.highlightProfitable()
            ? TEXT_PROFIT_TAG : TEXT_MUTED;

        // Decide right-column value and secondary line based on calc mode
        String rightValue;
        Color rightColor;
        String secondaryLine;
        if (calcVisible)
        {
            rightValue   = calcRightValue(spell, cost);
            rightColor   = calcRightColor(spell);
            secondaryLine = calcSecondaryLine(spell, cost);
        }
        else
        {
            rightValue    = formatGp(cost) + " gp";
            rightColor    = TEXT_HIGHLIGHT;
            secondaryLine = null;
        }

        JLabel iconLabel = buildSpellIconLabel(spell.getName());

        JPanel row = buildHorizontalCardRow(
            iconLabel,
            spell.getName(), subtitle, subtitleColor,
            rightValue, rightColor,
            secondaryLine
        );

        if (isExpanded)
        {
            setRowBackground(row, PANEL_EXPANDED);
        }

        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                toggleExpanded(spell);
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                if (!expandedSpells.contains(spell.getName()))
                {
                    setRowBackground(row, PANEL_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                if (!expandedSpells.contains(spell.getName()))
                {
                    setRowBackground(row, PANEL);
                }
            }
        });

        return row;
    }

    /**
     * Creates a 24x24 JLabel that will hold the spell's sprite icon.
     * The icon loads asynchronously via SpriteManager and updates the label
     * when ready. If no sprite is mapped for the spell, returns a blank label
     * of the same size as a placeholder.
     */
    private JLabel buildSpellIconLabel(String spellName)
    {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        label.setMinimumSize(new Dimension(ICON_SIZE, ICON_SIZE));
        label.setMaximumSize(new Dimension(ICON_SIZE, ICON_SIZE));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        int spriteId = SpellIcons.getSpriteId(spellName);
        if (spriteId >= 0 && spriteManager != null)
        {
            spriteManager.getSpriteAsync(spriteId, 0, sprite ->
            {
                if (sprite == null) return;
                Image scaled = sprite.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
                SwingUtilities.invokeLater(() -> label.setIcon(new ImageIcon(scaled)));
            });
        }
        return label;
    }

    /**
     * Creates a horizontal card row: [icon] [title/subtitle stacked] [cost].
     * Replaces the older vertical-only buildCardRow for cases where we have
     * an icon to display.
     */
    private JPanel buildHorizontalCardRow(JComponent iconComponent,
                                          String title, String subtitle, Color subtitleColor,
                                          String rightValue, Color rightColor,
                                          String secondaryLine)
    {
        JPanel row = new JPanel(new BorderLayout(INLINE_GAP, 0));
        row.setBackground(PANEL);
        row.setBorder(rowBorder());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Icon column + gap
        if (iconComponent != null)
        {
            JPanel iconWrap = new JPanel(new BorderLayout());
            iconWrap.setBackground(PANEL);
            iconWrap.setBorder(new EmptyBorder(0, 0, 0, ICON_TEXT_GAP));
            iconWrap.setPreferredSize(new Dimension(ICON_SIZE + ICON_TEXT_GAP, ICON_SIZE));
            iconWrap.add(iconComponent, BorderLayout.CENTER);
            row.add(iconWrap, BorderLayout.WEST);
        }

        // Center column: title + subtitle + optional secondary line
        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setBackground(PANEL);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(boldFont());
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setToolTipText(title);
        configureTruncation(titleLabel);
        textCol.add(titleLabel);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(baseFont());
        subtitleLabel.setForeground(subtitleColor);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setToolTipText(subtitle);
        configureTruncation(subtitleLabel);
        textCol.add(subtitleLabel);

        // Secondary line: shown below subtitle in calc mode
        if (secondaryLine != null)
        {
            JLabel secondaryLabel = new JLabel(secondaryLine);
            secondaryLabel.setFont(baseFont());
            secondaryLabel.setForeground(TEXT_MUTED);
            secondaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            configureTruncation(secondaryLabel, secondaryLine);
            textCol.add(secondaryLabel);
        }

        row.add(textCol, BorderLayout.CENTER);

        // Right column: cost / calc value
        JLabel valueLabel = new JLabel(rightValue);
        valueLabel.setFont(boldFont());
        valueLabel.setForeground(rightColor);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        valueLabel.setVerticalAlignment(SwingConstants.TOP);
        row.add(valueLabel, BorderLayout.EAST);

        forwardAllMouseEventsToRow(row);

        return row;
    }

    private void toggleExpanded(SpellData spell)
    {
        if (expandedSpells.contains(spell.getName()))
        {
            expandedSpells.remove(spell.getName());
        }
        else
        {
            expandedSpells.add(spell.getName());
        }
        refreshSpellList();
    }

    private JPanel buildCardRow(String title, String subtitle, Color subtitleColor,
                                String rightValue, Color rightColor)
    {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBackground(PANEL);
        row.setBorder(rowBorder());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(detailBoldFont());
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setToolTipText(title);
        configureTruncation(titleLabel);
        row.add(titleLabel);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(detailFont());
        subtitleLabel.setForeground(subtitleColor);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setToolTipText(subtitle);
        configureTruncation(subtitleLabel);
        row.add(subtitleLabel);

        row.add(Box.createVerticalStrut(2));

        JLabel valueLabel = new JLabel(rightValue);
        valueLabel.setFont(detailBoldFont());
        valueLabel.setForeground(rightColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        configureTruncation(valueLabel, rightValue);
        row.add(valueLabel);

        forwardAllMouseEventsToRow(row);

        return row;
    }

    /**
     * Recursively attaches a mouse forwarder to a component and all its
     * descendants so clicks/hover on any part of a row bubble up to the
     * row's own MouseListener. Equivalent to CSS pointer-events: none on
     * all children.
     */
    private void forwardMouseEventsToParent(JComponent child)
    {
        MouseAdapter forwarder = new MouseAdapter()
        {
            @Override public void mouseClicked(MouseEvent e) { redispatch(e); }
            @Override public void mousePressed(MouseEvent e) { redispatch(e); }
            @Override public void mouseReleased(MouseEvent e) { redispatch(e); }
            @Override public void mouseEntered(MouseEvent e) { redispatch(e); }
            @Override public void mouseExited(MouseEvent e) { redispatch(e); }

            private void redispatch(MouseEvent e)
            {
                Container parent = child.getParent();
                if (parent != null)
                {
                    parent.dispatchEvent(SwingUtilities.convertMouseEvent(child, e, parent));
                }
            }
        };
        child.addMouseListener(forwarder);
    }

    /**
     * Recursively walks the component tree rooted at root and attaches
     * forwardMouseEventsToParent to every descendant. Call this on a fully
     * built row panel so no nested component can swallow a click.
     */
    private void forwardAllMouseEventsToRow(JComponent root)
    {
        for (Component c : root.getComponents())
        {
            if (c instanceof JComponent)
            {
                forwardMouseEventsToParent((JComponent) c);
                forwardAllMouseEventsToRow((JComponent) c);
            }
        }
    }

    private void configureTruncation(JLabel label)
    {
        label.setMinimumSize(new Dimension(1, label.getPreferredSize().height));
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height));
    }

    /**
     * Same as configureTruncation(JLabel), but also sets a tooltip showing
     * the full text — needed since a truncated/ellipsed label otherwise
     * gives no way to read the rest of it.
     */
    private void configureTruncation(JLabel label, String fullText)
    {
        configureTruncation(label);
        label.setToolTipText(fullText);
    }

    /**
     * Font used for text inside expanded spell details (rune breakdown,
     * profit/loss). Size follows the "Expanded Card Text Size" setting.
     */
    private Font detailFont()
    {
        int size = config.detailTextSize() == CastCalcConfig.DetailTextSize.LARGE
            ? DETAIL_SIZE_LARGE : DETAIL_SIZE_SMALL;
        return baseFont(size);
    }

    private Font detailBoldFont()
    {
        int size = config.detailTextSize() == CastCalcConfig.DetailTextSize.LARGE
            ? DETAIL_SIZE_LARGE : DETAIL_SIZE_SMALL;
        return boldFont(size);
    }

    /**
     * Builds a name + quantity text row: [Name] x[qty] — name in white,
     * the "x[qty]" suffix muted. Used throughout the Profit/Loss section
     * so item lines read consistently with the rune breakdown above it.
     */
    private JPanel buildNameQtyLabel(String name, int quantity, Color background, boolean bold)
    {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(background);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        Font font = bold ? detailBoldFont() : detailFont();

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(font);
        nameLabel.setForeground(TEXT_PRIMARY);
        configureTruncation(nameLabel, name + " x" + quantity);
        row.add(nameLabel);

        JLabel qtyLabel = new JLabel(" x" + quantity);
        qtyLabel.setFont(font);
        qtyLabel.setForeground(TEXT_MUTED);
        row.add(qtyLabel);

        forwardMouseEventsToParent(nameLabel);
        forwardMouseEventsToParent(qtyLabel);

        return row;
    }

    private void setRowBackground(JPanel row, Color color)
    {
        row.setBackground(color);
        for (Component c : row.getComponents())
        {
            if (c instanceof JComponent && !c.isOpaque())
            {
                continue;
            }
            c.setBackground(color);
        }
    }

    // INLINE DETAIL CONTENT

    private JPanel buildSpellDetail(SpellData spell)
    {
        boolean useGear = config.useEquippedGear();

        JPanel detail = new JPanel();
        detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));
        detail.setBackground(PANEL_EXPANDED);
        // Indent the detail slightly to visually associate it with its row
        detail.setBorder(new EmptyBorder(
            ROW_PADDING_VERTICAL,
            ROW_PADDING_HORIZONTAL + 6,
            ROW_PADDING_VERTICAL,
            ROW_PADDING_HORIZONTAL
        ));
        detail.setAlignmentX(Component.LEFT_ALIGNMENT);
        detail.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));

        if (config.showRuneBreakdown())
        {
            for (CostCalculator.RuneLineItem item : calculator.getRuneLineItems(spell, useGear))
            {
                detail.add(buildRuneLineItem(item));
            }
            detail.add(Box.createVerticalStrut(ELEMENT_GAP));
        }

        if (spell.hasProfitLoss())
        {
            if (config.showRuneBreakdown())
            {
                detail.add(Box.createVerticalStrut(SECTION_GAP));
            }
            buildProfitLossSection(detail, spell);
        }
        else if (!config.showRuneBreakdown())
        {
            // Neither section applies — still show something so the card
            // visibly expands instead of toggling to an empty panel.
            long cost = calculator.calculateCastCost(spell, useGear);
            JLabel costLabel = new JLabel("Cost per cast: " + formatGp(cost) + " gp");
            costLabel.setFont(detailFont());
            costLabel.setForeground(TEXT_MUTED);
            costLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            detail.add(costLabel);
        }

        return detail;
    }

    /**
     * Builds one rune line item row: [icon] Name x[qty]: [cost] gp
     * Only the "x[qty]:" portion is muted; the rune name and cost stay
     * normally colored (cost turns green if the rune is free via gear).
     */
    private JPanel buildRuneLineItem(CostCalculator.RuneLineItem item)
    {
        JPanel row = new JPanel(new BorderLayout(ICON_TEXT_GAP, 0));
        row.setBackground(PANEL_EXPANDED);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));

        JLabel icon = buildItemIconLabel(item.rune.getItemId());
        JPanel iconWrap = new JPanel(new BorderLayout());
        iconWrap.setBackground(PANEL_EXPANDED);
        iconWrap.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        iconWrap.add(icon, BorderLayout.CENTER);
        row.add(iconWrap, BorderLayout.WEST);

        JPanel textRow = new JPanel();
        textRow.setLayout(new BoxLayout(textRow, BoxLayout.X_AXIS));
        textRow.setBackground(PANEL_EXPANDED);
        textRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(item.rune.getName());
        nameLabel.setFont(detailFont());
        nameLabel.setForeground(TEXT_PRIMARY);
        textRow.add(nameLabel);

        JLabel qtyLabel = new JLabel(" x" + item.quantity + ": ");
        qtyLabel.setFont(detailFont());
        qtyLabel.setForeground(TEXT_MUTED);
        textRow.add(qtyLabel);

        JLabel costLabel = new JLabel(item.free ? "Free" : formatGp(item.cost) + " gp");
        costLabel.setFont(detailFont());
        costLabel.setForeground(item.free ? PROFIT : TEXT_PRIMARY);
        textRow.add(costLabel);

        if (!item.free && item.savePercent > 0)
        {
            JLabel saveLabel = new JLabel(String.format(" (%.0f%% save)", item.savePercent * 100));
            saveLabel.setFont(detailFont());
            saveLabel.setForeground(TEXT_MUTED);
            textRow.add(saveLabel);
        }

        row.add(textRow, BorderLayout.CENTER);
        forwardMouseEventsToParent(icon);

        return row;
    }

    // PROFIT / LOSS SECTION

    private void buildProfitLossSection(JPanel container, SpellData spell)
    {
        JLabel plHeader = new JLabel("Profit / Loss");
        plHeader.setFont(detailBoldFont());
        plHeader.setForeground(TEXT_HIGHLIGHT);
        plHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(plHeader);
        container.add(Box.createVerticalStrut(ELEMENT_GAP));

        if (spell.getName().equals("High Level Alchemy") || spell.getName().equals("Low Level Alchemy"))
        {
            buildAlchProfitSection(container, spell);
        }
        else
        {
            buildGenericConversionSection(container, spell);
        }
    }

    private void buildAlchProfitSection(JPanel container, SpellData spell)
    {
        boolean isHighAlch = spell.getName().equals("High Level Alchemy");

        JLabel hint = new JLabel("Search for an item to alch:");
        hint.setFont(detailFont());
        hint.setForeground(TEXT_MUTED);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(hint);
        container.add(Box.createVerticalStrut(ELEMENT_GAP));

        JTextField alchSearchField = new JTextField();
        alchSearchField.setBackground(PANEL);
        alchSearchField.setForeground(TEXT_PRIMARY);
        alchSearchField.setCaretColor(TEXT_PRIMARY);
        alchSearchField.setFont(inputFont());
        alchSearchField.setBorder(inputBorder());
        alchSearchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, INPUT_HEIGHT));
        alchSearchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(alchSearchField);
        container.add(Box.createVerticalStrut(ELEMENT_GAP));

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(PANEL_EXPANDED);
        resultsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(resultsPanel);

        Timer debounceTimer = new Timer(400, null);
        debounceTimer.setRepeats(false);

        alchSearchField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                debounceTimer.stop();
                debounceTimer.addActionListener(evt ->
                {
                    String query = alchSearchField.getText().trim();
                    if (query.length() < 2)
                    {
                        resultsPanel.removeAll();
                        resultsPanel.revalidate();
                        resultsPanel.repaint();
                        return;
                    }
                    searchAndDisplayAlchItems(query, resultsPanel, isHighAlch);
                });
                debounceTimer.restart();
            }
        });
    }

    private void searchAndDisplayAlchItems(String query, JPanel resultsPanel, boolean isHighAlch)
    {
        resultsPanel.removeAll();

        clientThread.invokeLater(() ->
        {
            final java.util.List<ItemSearchRow> rows = new java.util.ArrayList<>();
            try
            {
                itemManager.search(query).stream().limit(8).forEach(result ->
                {
                    int itemId = result.getId();
                    try
                    {
                        ItemComposition comp = itemManager.getItemComposition(itemId);
                        calculator.cacheItemPrice(itemId);
                        int alchValue = isHighAlch ? comp.getHaPrice() : comp.getPrice();
                        rows.add(new ItemSearchRow(itemId, comp.getName(), alchValue));
                    }
                    catch (Exception ignored) {}
                });
            }
            catch (Exception ignored) {}

            SwingUtilities.invokeLater(() -> renderAlchResults(rows, resultsPanel, isHighAlch));
        });
    }

    private static class ItemSearchRow
    {
        final int itemId;
        final String name;
        final int alchValue;
        ItemSearchRow(int itemId, String name, int alchValue)
        {
            this.itemId = itemId;
            this.name = name;
            this.alchValue = alchValue;
        }
    }

    private void renderAlchResults(java.util.List<ItemSearchRow> rows, JPanel resultsPanel, boolean isHighAlch)
    {
        resultsPanel.removeAll();
        boolean useGear = config.useEquippedGear();
        SpellData alchSpell = SpellDatabase.getAllSpells().stream()
            .filter(s -> s.getName().equals(isHighAlch ? "High Level Alchemy" : "Low Level Alchemy"))
            .findFirst().orElse(null);
        long runeCost = alchSpell != null ? calculator.calculateCastCost(alchSpell, useGear) : 0;

        for (ItemSearchRow r : rows)
        {
            int gePrice = calculator.getItemPrice(r.itemId);
            long profit = r.alchValue - gePrice - runeCost;

            String details = "GE: " + formatGp(gePrice) + " - " +
                (isHighAlch ? "HA" : "LA") + ": " + formatGp(r.alchValue) +
                " - Runes: " + formatGp(runeCost);

            JPanel row = buildCardRow(
                r.name, details, TEXT_MUTED,
                (profit >= 0 ? "+" : "") + formatGp(profit) + " gp",
                profit >= 0 ? PROFIT : LOSS
            );

            resultsPanel.add(row);
            resultsPanel.add(Box.createVerticalStrut(ROW_GAP));
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void buildGenericConversionSection(JPanel container, SpellData spell)
    {
        java.util.List<ConversionPreset> presets = ConversionPreset.getPresetsForSpell(spell.getName());

        if (presets != null && !presets.isEmpty())
        {
            for (ConversionPreset preset : presets)
            {
                addPresetResult(container, spell, preset);
            }
        }
        else
        {
            JLabel noPresets = new JLabel("No preset conversions for this spell");
            noPresets.setFont(detailFont());
            noPresets.setForeground(TEXT_MUTED);
            noPresets.setAlignmentX(Component.LEFT_ALIGNMENT);
            container.add(noPresets);
        }
    }

    private void addPresetResult(JPanel container, SpellData spell, ConversionPreset preset)
    {
        CostCalculator.PresetResult result = calculator.calculatePresetProfit(spell, preset, config.useEquippedGear());

        // The card itself — a vertical stack of table rows
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL);
        card.setBorder(rowBorder());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header row: shows what this conversion produces
        if (result.isXpOnly)
        {
            JLabel header = new JLabel("Cast cost breakdown");
            header.setFont(detailBoldFont());
            header.setForeground(TEXT_PRIMARY);
            header.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(header);
        }
        else
        {
            JPanel header = buildNameQtyLabel(preset.getOutputName(), preset.getOutputQty(), PANEL, true);
            card.add(header);
        }
        card.add(Box.createVerticalStrut(ELEMENT_GAP));

        // Input rows
        for (ConversionPreset.ItemInput input : preset.getInputs())
        {
            long itemPrice = (long) calculator.getItemPrice(input.getItemId()) * input.getQuantity();
            JLabel itemIcon = buildItemIconLabel(input.getItemId());
            addItemLineItem(card, itemIcon, input.getName(), input.getQuantity(), formatGp(itemPrice) + " gp", TEXT_MUTED);
        }

        // Runes row (no icon since runes are aggregated across types)
        addLineItem(card, null, "Runes", formatGp(result.runeCost) + " gp", TEXT_MUTED);

        // Optional fee row (Plank Make charges an NPC fee)
        if (result.extraFee > 0)
        {
            addLineItem(card, null, "Fee", formatGp(result.extraFee) + " gp", TEXT_MUTED);
        }

        // Divider
        card.add(Box.createVerticalStrut(2));
        JSeparator divider = new JSeparator();
        divider.setForeground(BORDER);
        divider.setBackground(BORDER);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(divider);
        card.add(Box.createVerticalStrut(ELEMENT_GAP));

        if (result.isXpOnly)
        {
            // XP-only conversions (Reanimate spells): show total cost
            addLineItem(card, null, "Total cost", formatGp(result.totalCost()) + " gp", TEXT_HIGHLIGHT);
        }
        else
        {
            // Output value (with output item icon)
            JLabel outputIcon = buildItemIconLabel(preset.getOutputItemId());
            addItemLineItem(card, outputIcon, preset.getOutputName(), preset.getOutputQty(), formatGp(result.outputValue) + " gp", TEXT_PRIMARY);

            // Profit row — colored green or red, no icon
            card.add(Box.createVerticalStrut(2));
            String profitText = (result.profit >= 0 ? "+" : "") + formatGp(result.profit) + " gp";
            Color profitColor = result.profit >= 0 ? PROFIT : LOSS;
            addLineItem(card, null, "Profit", profitText, profitColor);
        }

        container.add(card);
        container.add(Box.createVerticalStrut(ROW_GAP));
    }

    /**
     * Adds a single line item to a card: optional icon on the left, label in middle,
     * value on the right. Pass null for the icon if no image is needed.
     */
    private void addLineItem(JPanel card, JLabel icon, String label, String value, Color valueColor)
    {
        JPanel row = new JPanel(new BorderLayout(INLINE_GAP, 0));
        row.setBackground(card.getBackground());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Icon column — always reserve space so labels align across all rows
        JPanel iconCol = new JPanel(new BorderLayout());
        iconCol.setBackground(card.getBackground());
        iconCol.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        if (icon != null)
        {
            iconCol.add(icon, BorderLayout.CENTER);
            forwardMouseEventsToParent(icon);
        }
        row.add(iconCol, BorderLayout.WEST);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(detailFont());
        labelComp.setForeground(TEXT_MUTED);
        configureTruncation(labelComp, label);
        forwardMouseEventsToParent(labelComp);
        row.add(labelComp, BorderLayout.CENTER);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(detailFont());
        valueComp.setForeground(valueColor);
        valueComp.setHorizontalAlignment(SwingConstants.RIGHT);
        configureTruncation(valueComp, value);
        forwardMouseEventsToParent(valueComp);
        row.add(valueComp, BorderLayout.EAST);

        card.add(row);
    }

    /**
     * Adds a named item line to a card: [icon] Name x[qty]  [value] — name in
     * white, the "x[qty]" suffix muted, matching the rune breakdown style.
     */
    private void addItemLineItem(JPanel card, JLabel icon, String name, int quantity, String value, Color valueColor)
    {
        JPanel row = new JPanel(new BorderLayout(INLINE_GAP, 0));
        row.setBackground(card.getBackground());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel iconCol = new JPanel(new BorderLayout());
        iconCol.setBackground(card.getBackground());
        iconCol.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        if (icon != null)
        {
            iconCol.add(icon, BorderLayout.CENTER);
            forwardMouseEventsToParent(icon);
        }
        row.add(iconCol, BorderLayout.WEST);

        JPanel nameQty = buildNameQtyLabel(name, quantity, card.getBackground(), false);
        row.add(nameQty, BorderLayout.CENTER);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(detailFont());
        valueComp.setForeground(valueColor);
        valueComp.setHorizontalAlignment(SwingConstants.RIGHT);
        configureTruncation(valueComp, value);
        forwardMouseEventsToParent(valueComp);
        row.add(valueComp, BorderLayout.EAST);

        card.add(row);
    }

    /**
     * Builds a 24x24 JLabel for an item icon. Loads asynchronously via
     * ItemManager and shows the image when ready. Returns a blank label if
     * the item ID is invalid (negative).
     */
    private JLabel buildItemIconLabel(int itemId)
    {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        label.setMinimumSize(new Dimension(ICON_SIZE, ICON_SIZE));
        label.setMaximumSize(new Dimension(ICON_SIZE, ICON_SIZE));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        if (itemId < 0) return label;

        try
        {
            AsyncBufferedImage asyncImg = itemManager.getImage(itemId);
            // Scale to ICON_SIZE after load — addTo() sets native size which clips.
            asyncImg.onLoaded(() ->
            {
                Image scaled = asyncImg.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
                SwingUtilities.invokeLater(() -> label.setIcon(new ImageIcon(scaled)));
            });
        }
        catch (Exception ignored) {}

        return label;
    }

    // UTILITIES

    public void updatePrices()
    {
        if (!initialized) return;
        rebuildGearEffects();
        refreshSpellList();
    }

    private long safeCost(SpellData spell, boolean useGear)
    {
        try
        {
            return calculator.calculateCastCost(spell, useGear);
        }
        catch (Exception ex)
        {
            return Long.MAX_VALUE;
        }
    }

    /**
     * Returns the highest profit value across all conversion presets for a P&L
     * spell, used for sort-by-profit. Returns Long.MIN_VALUE for spells with no
     * presets (so they sort to the bottom). Skips XP-only conversions like
     * Reanimate spells, since those don't have a meaningful "profit" value.
     *
     * Note: For High/Low Alchemy, this returns 0 since alch profit depends on
     * which item the user searches — there's no fixed best-case to sort by.
     */
    private long bestProfit(SpellData spell, boolean useGear)
    {
        if (!spell.hasProfitLoss()) return Long.MIN_VALUE;

        // Alch spells have no fixed presets; treat profit as 0 for sorting purposes
        if (spell.getName().equals("High Level Alchemy") || spell.getName().equals("Low Level Alchemy"))
        {
            return 0;
        }

        java.util.List<ConversionPreset> presets = ConversionPreset.getPresetsForSpell(spell.getName());
        if (presets == null || presets.isEmpty()) return Long.MIN_VALUE;

        long best = Long.MIN_VALUE;
        for (ConversionPreset preset : presets)
        {
            try
            {
                CostCalculator.PresetResult result = calculator.calculatePresetProfit(spell, preset, useGear);
                if (result.isXpOnly) continue; // Reanimation etc. have no profit value
                if (result.profit > best) best = result.profit;
            }
            catch (Exception ignored) {}
        }
        return best;
    }

    private static String formatGp(long amount)
    {
        if (Math.abs(amount) >= 10_000_000)
        {
            return String.format("%.1fM", amount / 1_000_000.0);
        }
        else if (Math.abs(amount) >= 100_000)
        {
            return String.format("%.0fK", amount / 1_000.0);
        }
        else if (Math.abs(amount) >= 10_000)
        {
            return String.format("%.1fK", amount / 1_000.0);
        }
        return String.format("%,d", amount);
    }

    private void styleComboBox(JComboBox<?> combo)
    {
        combo.setBackground(PANEL);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(dropdownFont());
        combo.setBorder(new javax.swing.border.MatteBorder(1, 1, 1, 1, BORDER));
    }
}

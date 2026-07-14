package com.castcalc;

import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Calculates the GP cost of casting a spell, factoring in equipped
 * rune-saving gear and live GE rune prices.
 *
 * Prices are cached via refreshPriceCache() which should be called from
 * the client thread. The panel (Swing thread) reads from this cache.
 */
public class CostCalculator
{
    private final Client client;
    private final ItemManager itemManager;

    // Thread-safe price cache, populated on the client thread
    private final Map<Integer, Integer> priceCache = new ConcurrentHashMap<>();

    public CostCalculator(Client client, ItemManager itemManager)
    {
        this.client = client;
        this.itemManager = itemManager;
    }

    /**
     * Refreshes the price cache. MUST be called from the client thread.
     * Caches rune prices and any items referenced in ConversionPresets.
     */
    public void refreshPriceCache()
    {
        // Cache all rune prices
        for (Rune rune : Rune.values())
        {
            try
            {
                priceCache.put(rune.getItemId(), itemManager.getItemPrice(rune.getItemId()));
            }
            catch (Exception e)
            {
                priceCache.put(rune.getItemId(), 0);
            }
        }

        // Cache prices for all items referenced in conversion presets
        for (Map.Entry<String, java.util.List<ConversionPreset>> entry : ConversionPreset.getAllPresets().entrySet())
        {
            for (ConversionPreset preset : entry.getValue())
            {
                if (preset.getOutputItemId() > 0)
                {
                    try
                    {
                        priceCache.put(preset.getOutputItemId(),
                            itemManager.getItemPrice(preset.getOutputItemId()));
                    }
                    catch (Exception e)
                    {
                        priceCache.put(preset.getOutputItemId(), 0);
                    }
                }
                for (ConversionPreset.ItemInput input : preset.getInputs())
                {
                    try
                    {
                        priceCache.put(input.getItemId(),
                            itemManager.getItemPrice(input.getItemId()));
                    }
                    catch (Exception e)
                    {
                        priceCache.put(input.getItemId(), 0);
                    }
                }
            }
        }
    }

    /**
     * Caches a single item price. Call from client thread.
     * Used on-demand from the alch item search.
     */
    public void cacheItemPrice(int itemId)
    {
        try
        {
            priceCache.put(itemId, itemManager.getItemPrice(itemId));
        }
        catch (Exception e)
        {
            priceCache.put(itemId, 0);
        }
    }

    /**
     * Returns the cached price, or 0 if not cached.
     */
    public int getRunePrice(Rune rune)
    {
        Integer cached = priceCache.get(rune.getItemId());
        return cached != null ? cached : 0;
    }

    public int getItemPrice(int itemId)
    {
        Integer cached = priceCache.get(itemId);
        return cached != null ? cached : 0;
    }

    /**
     * Direct price lookup (unsafe from Swing thread). Used only when caller
     * is already on the client thread (e.g., from within cacheItemPrice flows).
     */
    public ItemManager getItemManager()
    {
        return itemManager;
    }

    public long calculateCastCost(SpellData spell, boolean useGearSave)
    {
        Set<Rune> providedRunes = useGearSave
            ? RuneSavingGear.getCachedProvidedRunes()
            : EnumSet.noneOf(Rune.class);

        long totalCost = 0;

        for (RuneRequirement req : spell.getRuneRequirements())
        {
            if (providedRunes.contains(req.getRune()))
            {
                continue;
            }

            int pricePerRune = getRunePrice(req.getRune());
            double saveChance = useGearSave
                ? RuneSavingGear.getRuneSaveChance(req.getRune())
                : 0.0;

            double effectiveCost = pricePerRune * req.getQuantity() * (1.0 - saveChance);
            totalCost += Math.round(effectiveCost);
        }

        return totalCost;
    }

    public Map<String, Long> getCostBreakdown(SpellData spell, boolean useGearSave)
    {
        Set<Rune> providedRunes = useGearSave
            ? RuneSavingGear.getCachedProvidedRunes()
            : EnumSet.noneOf(Rune.class);

        Map<String, Long> breakdown = new LinkedHashMap<>();

        for (RuneRequirement req : spell.getRuneRequirements())
        {
            String label = req.getQuantity() + "x " + req.getRune().getName() + " rune";

            if (providedRunes.contains(req.getRune()))
            {
                breakdown.put(label + " (free)", 0L);
                continue;
            }

            int pricePerRune = getRunePrice(req.getRune());
            double saveChance = useGearSave
                ? RuneSavingGear.getRuneSaveChance(req.getRune())
                : 0.0;

            long cost = Math.round(pricePerRune * req.getQuantity() * (1.0 - saveChance));
            if (saveChance > 0)
            {
                label += String.format(" (%.1f%% save)", saveChance * 100);
            }
            breakdown.put(label, cost);
        }

        return breakdown;
    }

    /**
     * Structured version of the rune cost breakdown, used to render icon +
     * name + quantity + cost line items in the UI. Prefer this over
     * getCostBreakdown() (which returns pre-formatted label strings) for
     * any new rendering code.
     */
    public List<RuneLineItem> getRuneLineItems(SpellData spell, boolean useGearSave)
    {
        Set<Rune> providedRunes = useGearSave
            ? RuneSavingGear.getCachedProvidedRunes()
            : EnumSet.noneOf(Rune.class);

        List<RuneLineItem> items = new java.util.ArrayList<>();

        for (RuneRequirement req : spell.getRuneRequirements())
        {
            if (providedRunes.contains(req.getRune()))
            {
                items.add(new RuneLineItem(req.getRune(), req.getQuantity(), 0L, true, 0.0));
                continue;
            }

            int pricePerRune = getRunePrice(req.getRune());
            double saveChance = useGearSave
                ? RuneSavingGear.getRuneSaveChance(req.getRune())
                : 0.0;
            long cost = Math.round(pricePerRune * req.getQuantity() * (1.0 - saveChance));

            items.add(new RuneLineItem(req.getRune(), req.getQuantity(), cost, false, saveChance));
        }

        return items;
    }

    /** One line item in a rune cost breakdown: which rune, how many, what it costs. */
    public static class RuneLineItem
    {
        public final Rune rune;
        public final int quantity;
        public final long cost;
        public final boolean free;
        public final double savePercent; // 0 if no rune-saving gear applies

        public RuneLineItem(Rune rune, int quantity, long cost, boolean free, double savePercent)
        {
            this.rune = rune;
            this.quantity = quantity;
            this.cost = cost;
            this.free = free;
            this.savePercent = savePercent;
        }
    }

    public PresetResult calculatePresetProfit(SpellData spell, ConversionPreset preset,
                                              boolean useGear)
    {
        long totalInputCost = 0;
        for (ConversionPreset.ItemInput input : preset.getInputs())
        {
            totalInputCost += (long) getItemPrice(input.getItemId()) * input.getQuantity();
        }

        long outputValue = 0;
        boolean isXpOnly = preset.getOutputItemId() == -1;
        if (!isXpOnly)
        {
            outputValue = (long) getItemPrice(preset.getOutputItemId()) * preset.getOutputQty();
        }

        long runeCost = calculateCastCost(spell, useGear);
        long profit = outputValue - totalInputCost - runeCost - preset.getExtraGpCost();

        return new PresetResult(totalInputCost, outputValue, runeCost,
            preset.getExtraGpCost(), profit, isXpOnly);
    }

    public static class PresetResult
    {
        public final long inputCost;
        public final long outputValue;
        public final long runeCost;
        public final long extraFee;
        public final long profit;
        public final boolean isXpOnly;

        public PresetResult(long inputCost, long outputValue, long runeCost,
                            long extraFee, long profit, boolean isXpOnly)
        {
            this.inputCost = inputCost;
            this.outputValue = outputValue;
            this.runeCost = runeCost;
            this.extraFee = extraFee;
            this.profit = profit;
            this.isXpOnly = isXpOnly;
        }

        public long totalCost()
        {
            return inputCost + runeCost + extraFee;
        }
    }
}

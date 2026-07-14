package com.castcalc;

import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;

import java.util.*;

/**
 * Detects equipped rune-saving gear and calculates which runes
 * are provided for free or have a chance to be saved.
 *
 * IMPORTANT: All methods that access the Client must be called
 * from the client thread (e.g., from event subscribers).
 * Use the cached version (getCachedProvidedRunes) from the Swing thread.
 */
public class RuneSavingGear
{
    // Map of weapon/shield item IDs -> set of rune types they provide for free
    private static final Map<Integer, Set<Rune>> ELEMENTAL_PROVIDERS = new HashMap<>();

    // Items that give a % chance to save runes
    public static final int BRYOPHYTAS_STAFF = ItemID.BRYOPHYTAS_STAFF;
    public static final int KODAI_WAND = ItemID.KODAI_WAND;

    // Tome of fire provides fire runes when equipped in shield slot
    public static final int TOME_OF_FIRE = ItemID.TOME_OF_FIRE;
    public static final int TOME_OF_WATER = ItemID.TOME_OF_WATER;

    // Cached state — updated from client thread, read from Swing thread
    private static Set<Rune> cachedProvidedRunes = EnumSet.noneOf(Rune.class);
    private static List<String> cachedEffects = new ArrayList<>();
    private static int cachedWeaponId = -1;

    static
    {
        // --- Air ---
        Set<Rune> air = EnumSet.of(Rune.AIR);
        ELEMENTAL_PROVIDERS.put(ItemID.STAFF_OF_AIR, air);
        ELEMENTAL_PROVIDERS.put(ItemID.AIR_BATTLESTAFF, air);
        ELEMENTAL_PROVIDERS.put(ItemID.MYSTIC_AIR_STAFF, air);

        // --- Water ---
        Set<Rune> water = EnumSet.of(Rune.WATER);
        ELEMENTAL_PROVIDERS.put(ItemID.STAFF_OF_WATER, water);
        ELEMENTAL_PROVIDERS.put(ItemID.WATER_BATTLESTAFF, water);
        ELEMENTAL_PROVIDERS.put(ItemID.MYSTIC_WATER_STAFF, water);

        // --- Earth ---
        Set<Rune> earth = EnumSet.of(Rune.EARTH);
        ELEMENTAL_PROVIDERS.put(ItemID.STAFF_OF_EARTH, earth);
        ELEMENTAL_PROVIDERS.put(ItemID.EARTH_BATTLESTAFF, earth);
        ELEMENTAL_PROVIDERS.put(ItemID.MYSTIC_EARTH_STAFF, earth);

        // --- Fire ---
        Set<Rune> fire = EnumSet.of(Rune.FIRE);
        ELEMENTAL_PROVIDERS.put(ItemID.STAFF_OF_FIRE, fire);
        ELEMENTAL_PROVIDERS.put(ItemID.FIRE_BATTLESTAFF, fire);
        ELEMENTAL_PROVIDERS.put(ItemID.MYSTIC_FIRE_STAFF, fire);

        // --- Combination staves ---
        Set<Rune> steamRunes = EnumSet.of(Rune.WATER, Rune.FIRE);
        ELEMENTAL_PROVIDERS.put(ItemID.STEAM_BATTLESTAFF, steamRunes);
        ELEMENTAL_PROVIDERS.put(ItemID.MYSTIC_STEAM_STAFF, steamRunes);

        Set<Rune> smokeRunes = EnumSet.of(Rune.AIR, Rune.FIRE);
        ELEMENTAL_PROVIDERS.put(ItemID.SMOKE_BATTLESTAFF, smokeRunes);
        ELEMENTAL_PROVIDERS.put(ItemID.MYSTIC_SMOKE_STAFF, smokeRunes);

        Set<Rune> dustRunes = EnumSet.of(Rune.AIR, Rune.EARTH);
        ELEMENTAL_PROVIDERS.put(ItemID.DUST_BATTLESTAFF, dustRunes);
        ELEMENTAL_PROVIDERS.put(ItemID.MYSTIC_DUST_STAFF, dustRunes);

        Set<Rune> mudRunes = EnumSet.of(Rune.WATER, Rune.EARTH);
        ELEMENTAL_PROVIDERS.put(ItemID.MUD_BATTLESTAFF, mudRunes);
        ELEMENTAL_PROVIDERS.put(ItemID.MYSTIC_MUD_STAFF, mudRunes);

        Set<Rune> lavaRunes = EnumSet.of(Rune.FIRE, Rune.EARTH);
        ELEMENTAL_PROVIDERS.put(ItemID.LAVA_BATTLESTAFF, lavaRunes);
        ELEMENTAL_PROVIDERS.put(ItemID.MYSTIC_LAVA_STAFF, lavaRunes);

        Set<Rune> mistRunes = EnumSet.of(Rune.WATER, Rune.AIR);
        ELEMENTAL_PROVIDERS.put(ItemID.MIST_BATTLESTAFF, mistRunes);
        ELEMENTAL_PROVIDERS.put(ItemID.MYSTIC_MIST_STAFF, mistRunes);

        // Kodai wand provides water runes (and has 15% rune save)
        Set<Rune> kodaiRunes = EnumSet.of(Rune.WATER);
        ELEMENTAL_PROVIDERS.put(ItemID.KODAI_WAND, kodaiRunes);
    }

    /**
     * Update the cached gear state. MUST be called from the client thread
     * (e.g., from an event subscriber like onItemContainerChanged).
     */
    public static void updateCache(Client client)
    {
        Set<Rune> provided = EnumSet.noneOf(Rune.class);
        List<String> effects = new ArrayList<>();
        int weaponId = -1;

        ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);

        if (equipment != null)
        {
            Item[] items = equipment.getItems();

            // Check weapon slot
            if (items.length > EquipmentInventorySlot.WEAPON.getSlotIdx())
            {
                weaponId = items[EquipmentInventorySlot.WEAPON.getSlotIdx()].getId();
                Set<Rune> weaponRunes = ELEMENTAL_PROVIDERS.get(weaponId);
                if (weaponRunes != null)
                {
                    provided.addAll(weaponRunes);
                }
            }

            // Check shield slot (Tome of Fire / Tome of Water)
            if (items.length > EquipmentInventorySlot.SHIELD.getSlotIdx())
            {
                int shieldId = items[EquipmentInventorySlot.SHIELD.getSlotIdx()].getId();
                if (shieldId == TOME_OF_FIRE)
                {
                    provided.add(Rune.FIRE);
                }
                else if (shieldId == TOME_OF_WATER)
                {
                    provided.add(Rune.WATER);
                }
            }
        }

        // Build effects list
        if (!provided.isEmpty())
        {
            StringBuilder sb = new StringBuilder("Free runes: ");
            boolean first = true;
            for (Rune r : provided)
            {
                if (!first) sb.append(", ");
                sb.append(r.getName());
                first = false;
            }
            effects.add(sb.toString());
        }

        if (weaponId == KODAI_WAND)
        {
            effects.add("Kodai wand: 15% rune save");
        }
        if (weaponId == BRYOPHYTAS_STAFF)
        {
            effects.add("Bryophyta's staff: 1/15 nature rune save");
        }

        // Store in cache (thread-safe enough for our purposes)
        cachedProvidedRunes = provided;
        cachedEffects = effects;
        cachedWeaponId = weaponId;
    }

    /**
     * Returns cached provided runes. Safe to call from any thread.
     */
    public static Set<Rune> getCachedProvidedRunes()
    {
        return cachedProvidedRunes;
    }

    /**
     * Returns cached active effects list. Safe to call from any thread.
     */
    public static List<String> getCachedEffects()
    {
        return cachedEffects;
    }

    /**
     * Returns the rune-saving probability from cached gear state.
     * Safe to call from any thread.
     */
    public static double getRuneSaveChance(Rune rune)
    {
        // Kodai wand: 15% chance to save all runes
        if (cachedWeaponId == KODAI_WAND)
        {
            return 0.15;
        }

        // Bryophyta's staff: 1/15 chance to save nature runes only
        if (cachedWeaponId == BRYOPHYTAS_STAFF && rune == Rune.NATURE)
        {
            return 1.0 / 15.0;
        }

        return 0.0;
    }
}

package org.dreambot.fractals.util;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.loadout.ItemVariant;

public class OwnedItems {
    public static int count(int itemId) {
        return Inventory.count(itemId) + Bank.count(itemId);
    }


    public static int count(int itemId, boolean includeNoted) {
        Item item = new Item(itemId, 1);
        if (item.getNotedItemID() != itemId && includeNoted) {
            return Inventory.count(itemId) + Bank.count(itemId) + Inventory.count(item.getNotedItemID());
        }
        return Inventory.count(itemId) + Bank.count(itemId);
    }

    public static boolean contains(int itemId) {
        return Inventory.contains(itemId) || Bank.contains(itemId) || Equipment.contains(itemId);
    }

    public static boolean containsUnworn(int itemId) {
        return Inventory.contains(itemId) || Bank.contains(itemId);
    }

    public static boolean contains(ItemVariant variant) {
        if (variant == null) return false;
        for (Integer id : variant.getIds()) {
            if (OwnedItems.contains(id)) return true;
        }
        return false;
    }

    public static boolean containsAny(boolean includeNoted, int... ids) {
        for (int id : ids) {
            if (count(id, includeNoted) >= 1) return true;
        }
        return false;
    }

    public static boolean containsAny(int... ids) {
        for (int id : ids) {
            if (contains(id)) return true;
        }
        return false;
    }

    public static boolean containsAnyUnworn(int... ids) {
        for (int id : ids) {
            if (containsUnworn(id, true)) return true;
        }
        return false;
    }

    public static boolean containsAll(int... ids) {
        for (int id : ids) {
            if (!contains(id)) return false;
        }
        return true;
    }
}

package org.dreambot;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.wrappers.items.Item;

public class OwnedItems {
    public static int count(int itemId) {
        return Inventory.count(itemId) + Bank.count(itemId);
    }

    public static boolean contains(int itemId) {
        return Inventory.contains(itemId) || Bank.contains(itemId);
    }

//    public static boolean contains(ItemVariant variant) {
//        if (variant == null) return false;
//        for (Integer id : variant.getIds()) {
//            if (OwnedItems.contains(id)) return true;
//        }
//        return false;
//    }

    public static boolean containsAny(int... ids) {
        for (int id : ids) {
            if (contains(id) || contains(new Item(id, 0).getNotedItemID())) return true;
        }
        return false;
    }
}

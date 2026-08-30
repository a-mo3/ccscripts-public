package org.dreambot.utility;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;

public class OwnedItems {
    public static int count(int itemId) {
        return Inventory.count(itemId) + Bank.count(itemId);
    }

    public static boolean contains(int itemId) {
        return Inventory.contains(itemId) || Bank.contains(itemId);
    }
}

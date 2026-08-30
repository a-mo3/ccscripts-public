package org.dreambot.behaviour.foundry.data;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.InventoryLoadoutItem;

import java.util.function.Supplier;

public class FoundryLoadouts {
    private static final Supplier<Integer> bucketSupplier = () -> {
        if (Bank.contains(ItemID.BUCKET_OF_WATER) || Inventory.contains(ItemID.BUCKET_OF_WATER)) {
            return ItemID.BUCKET_OF_WATER;
        }
        return ItemID.BUCKET;
    };

    public static final InventoryLoadout FOUNDRY_BARS = new InventoryLoadout()
            .addItem(ItemID.STEEL_BAR, 19).setRefill(500)
            .addItem(ItemID.IRON_BAR, 9).setRefill(500)
            .setStrict(true);

    public static final InventoryLoadout BUCKET = new InventoryLoadout()
            .addItem(new InventoryLoadoutItem(bucketSupplier, 1, 1));
}

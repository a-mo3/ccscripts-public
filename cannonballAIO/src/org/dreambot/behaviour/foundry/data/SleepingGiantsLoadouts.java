package org.dreambot.behaviour.foundry.data;


import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public class SleepingGiantsLoadouts {
    public static final InventoryLoadout SLEEPING_GIANTS_LOADOUT = new InventoryLoadout()
            .addItem(ItemID.OAK_LOGS, 3)
            .addItem(ItemID.WOOL, 1)
            .addItem(ItemID.STEEL_NAILS, 10)
            .addItem(ItemID.HAMMER, 1)
            .addItem(ItemID.CHISEL, 1)
            .addItem(ItemID.BUCKET_OF_WATER, 1)
            .addItem(ItemVariants.AMULET_OF_GLORY)
            .setStrict(false);
}

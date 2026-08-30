package org.dreambot.behaviour.method.pirates;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;

public enum PirateInventoryLoadout {
    WINES(new InventoryLoadout()

            .addItem(ItemVariants.BURNING_AMULET)
            .setRefill(10)
            .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.SALVE_AMULET))

            .addItem(ItemID.JUG_OF_WINE, 20)
            .setRefill(1500)
    ),


    MANTAS(new InventoryLoadout()
            .addItem(ItemVariants.BURNING_AMULET)
            .setRefill(10)
            .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.SALVE_AMULET))
            .addItem(ItemID.BLIGHTED_MANTA_RAY, 20)
            .setRefill(250)
    ),


    LOBSTERS(new InventoryLoadout()
            .addItem(ItemVariants.BURNING_AMULET)
            .setRefill(10)
            .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.SALVE_AMULET))
            .addItem(ItemID.LOBSTER, 20)
            .setRefill(250)
    ),

    ;

    public final InventoryLoadout loadout;

    PirateInventoryLoadout(InventoryLoadout loadout) {
        this.loadout = loadout;
    }
}

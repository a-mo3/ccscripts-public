package org.dreambot.behaviour.method.huey;

import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public class HueyConst {
    public static final InventoryLoadout MELEE_INV = new InventoryLoadout()
            .addItem(ItemID.PRAYER_POTION4, 6, 6)
            .setRefill(50)
            .addItem(ItemVariants.DIVINE_SUPER_COMBAT_POTION, 2, 2)
            .setRefill(50)
            .addItem(ItemID.SHARK, 18)
            .setRefill(500)
            .addItem(ItemID.BURNING_CLAWS)
            .setEnabledCondition(() -> HueyData.useBurningClaws)
            ;
}

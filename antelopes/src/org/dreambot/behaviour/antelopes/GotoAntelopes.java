package org.dreambot.behaviour.antelopes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

public class GotoAntelopes extends Fractal {
    public GotoAntelopes() {
        setInventoryLoadout(new InventoryLoadout()
                .addItem(ItemID.CHISEL, 1)
                .addItem(ItemID.KNIFE, 1)
                .addItem(ItemID.TEASING_STICK)
                .addItem(ItemID.JUG_OF_WINE, 4)
                .setRefill(200)
                .setEnabledCondition(() -> !Inventory.contains(ItemID.JUG_OF_WINE))
                .addItem(ItemVariants.FUR_POUCH)
                .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.FUR_POUCH))
                .addItem(ItemVariants.MEAT_POUCH)
                .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.MEAT_POUCH))
                .setRefill(500)
                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
        );

        setEquipmentLoadout(new EquipmentLoadout()
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
        );
    }

    public static final Area ANTELOPE_AREA = new Area(1567, 9426, 1552, 9415, 0);

    @Override
    public boolean isValid() {
        if (ANTELOPE_AREA.contains(Players.getLocal()) && Walking.getRunEnergy() > 10 && !Walking.isRunEnabled()) {
            Walking.toggleRun();
        }
        return !ANTELOPE_AREA.contains(Players.getLocal()) || !inventoryLoadout.isFulfilled();
    }

    @Override
    public int onLoop() {
        if (Walking.shouldWalk()) Walking.walk(ANTELOPE_AREA);
        return ReactionGenerator.getNormal();
    }
}

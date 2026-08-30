package org.dreambot.behaviour.method.scorpia;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public enum ScorpiaLoadout {
    TRIDENT(
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.TRIDENT)
                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE),
            new InventoryLoadout()
                    .addItem(ItemID.KNIFE)
                    .addItem(ItemID.ANTIPOISON4, 2)
                    .setRefill(20)
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 6)
                    .setRefill(30)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 15)
                    .setRefill(200)
    ),
    TRIDENT_WINES(
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE),
            new InventoryLoadout()
                    .addItem(ItemID.ANTIPOISON4, 1)
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 5)
                    .addItem(ItemID.JUG_OF_WINE, 22)
    );
    final EquipmentLoadout equipmentLoadout;
    final InventoryLoadout loadout;

    ScorpiaLoadout(EquipmentLoadout equipmentLoadout, InventoryLoadout loadout) {
        this.equipmentLoadout = equipmentLoadout;
        this.loadout = loadout;
    }
}

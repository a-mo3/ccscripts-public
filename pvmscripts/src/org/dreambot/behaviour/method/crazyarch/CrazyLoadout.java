package org.dreambot.behaviour.method.crazyarch;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public enum CrazyLoadout {
    MAGE_TRIDENT(
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 20).setRefill(200)
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 2).setRefill(50)
                    .addItem(ItemVariants.BURNING_AMULET).setRefill(20),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH).setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.TRIDENT),
            Skill.MAGIC
    ),
    MAGE_ACCURSED(
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 20).setRefill(200)
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 2).setRefill(50)
                    .addItem(ItemVariants.BURNING_AMULET).setRefill(20),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH).setRefill(20)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.TRIDENT),
            Skill.MAGIC
    ),
    ;
    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;
    public final Skill mode;

    CrazyLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout, Skill mode) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
        this.mode = mode;
    }
}

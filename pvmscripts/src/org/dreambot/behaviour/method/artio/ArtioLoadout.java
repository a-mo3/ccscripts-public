package org.dreambot.behaviour.method.artio;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public enum ArtioLoadout {
    ACCURSED(
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.ACCURSED)
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                    .addItem(EquipmentSlot.CHEST, ItemID.XERICIAN_TOP)
                    .setRefill(3)
                    .addItem(EquipmentSlot.LEGS, ItemID.XERICIAN_ROBE)
                    .setRefill(3)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(30),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 22)
                    .setRefill(500)
                    .addItem(ItemID.MAGIC_POTION4, 1)
                    .setRefill(50)
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 3)
                    .setRefill(150)
                    .addItem(ItemID.BLIGHTED_ENTANGLE_SACK, 75)
                    .setRefill(300)
    ),

    WEBWEAVER_DHIDE(
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.WEBWEAVER_BOW)
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setRefill(3)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setRefill(3)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(30),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 22)
                    .setRefill(500)
                    .addItem(ItemID.MAGIC_POTION4, 1)
                    .setRefill(50)
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 3)
                    .setRefill(150)
                    .addItem(ItemID.BLIGHTED_ENTANGLE_SACK, 75)
                    .setRefill(300)
    ),

    CRAWS_DHIDE(
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.CRAWS_BOW)
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setRefill(3)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setRefill(3)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                    .setRefill(30),
            new InventoryLoadout()
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 22)
                    .setRefill(500)
                    .addItem(ItemID.MAGIC_POTION4, 1)
                    .setRefill(50)
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 3)
                    .setRefill(150)
                    .addItem(ItemID.BLIGHTED_ENTANGLE_SACK, 75)
                    .setRefill(300)
    );

    final EquipmentLoadout equipmentLoadout;
    final InventoryLoadout loadout;

    ArtioLoadout(EquipmentLoadout equipmentLoadout, InventoryLoadout loadout) {
        this.equipmentLoadout = equipmentLoadout;
        this.loadout = loadout;
    }
}

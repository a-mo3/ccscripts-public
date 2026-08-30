package org.dreambot.behaviour.method.nightmare;

import lombok.Getter;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

@Getter
public enum PhosaniLoadout {
    BLOOD_MOON_KIT(
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.HAT, ItemVariants.BLOOD_MOON_HELM)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.BLOOD_MOON_CHESTPLATE)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.BLOOD_MOON_LEGS)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_BLUDGEON)
                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)
                    .addItem(EquipmentSlot.RING, ItemID.BRIMSTONE_RING)
                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE),

            new InventoryLoadout()
                    // mage switch
                    .addItem(ItemID.OCCULT_NECKLACE)
                    .addItem(ItemVariants.TRIDENT_OF_SWAMP)
                    .addItem(ItemVariants.AHRIMS_ROBEBOTTOM)
                    .addItem(ItemVariants.AHRIMS_ROBETOP)
                    // resources
                    .addItem(ItemVariants.SANFEW_SERUM, 3, 3) // 21 slots free
                    .setRefill(50)
                    .addItem(ItemID.PRAYER_POTION4, 2) // 19 slots free
                    .setRefill(50)
                    .addItem(ItemID.SUPER_COMBAT_POTION4, 1) // 18 sslots free
                    .setRefill(50)
                    .addItem(ItemID.STAMINA_POTION4, 1) // 18 sslots free
                    .setRefill(50)
                    .addItem(ItemID.MANTA_RAY, 15) // 1 slot free
                    .setRefill(500)
                    .addItem(ItemID.ARMADYL_GODSWORD)
                    .addItem(ItemID.FENKENSTRAINS_CASTLE_TELEPORT, 1, 2)
                    .setRefill(50),

            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.TRIDENT_OF_SWAMP)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.AHRIMS_ROBEBOTTOM)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.AHRIMS_ROBETOP)
    );
    final EquipmentLoadout equipmentLoadout;
    final InventoryLoadout loadout;
    final EquipmentLoadout mageLoadout;

    PhosaniLoadout(EquipmentLoadout equipmentLoadout, InventoryLoadout loadout, EquipmentLoadout mageLoadout) {
        this.equipmentLoadout = equipmentLoadout;
        this.loadout = loadout;
        this.mageLoadout = mageLoadout;
    }
}

package org.dreambot.behaviour.method.gwd.nex;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

/**
 * after getting nex KC you get to bank, so we pre buy supplies for main loadout
 * equip the kc loadout, kill blood reavers with sharks and blowpipe (or whatever)
 * and then switch back to KC loadout when in bank
 */
public enum NexKCLoadout {
    BLOWPIPE(
            new InventoryLoadout()
                    .addItem(ItemID.SHARK, 14).setRefill(50)
                    .addItem(ItemID.SUPER_RESTORE4, 10).setRefill(50)
                    .addItem(ItemID.STAMINA_POTION4, 2).setRefill(40)
                    .addItem(ItemID.RUNE_POUCH),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemVariants.ARCHERS_RING)

                    .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                    .addItem(EquipmentSlot.LEGS, ItemID.ZAMORAK_CHAPS)
                    .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
                    .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                    .addItem(EquipmentSlot.WEAPON, ItemVariants.BLOWPIPE)
                    .addItem(EquipmentSlot.CHEST, ItemID.ANCIENT_DHIDE_BODY)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
    );

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;

    NexKCLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }
}

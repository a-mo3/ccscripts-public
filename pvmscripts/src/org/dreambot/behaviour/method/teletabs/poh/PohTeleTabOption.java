package org.dreambot.behaviour.method.teletabs.poh;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;

public enum PohTeleTabOption {
    VARROCK("Varrock teleport", 30,
            new InventoryLoadout()
                    .addItem(ItemID.COINS_995, 5, 100_000)
                    .addItem(ItemID.LAW_RUNE, 1, 5000)
                    .addItem(ItemID.SOFT_CLAY + 1, 28, 2000)
                    .setEnabledCondition(() -> !Inventory.contains(ItemID.SOFT_CLAY)),
            new EquipmentLoadout(PohTabsBranch.teleportBase)
                    .addItem(EquipmentSlot.WEAPON, ItemID.SMOKE_BATTLESTAFF)
    ),
    LUMBRIDGE("Lumbridge teleport", 31,
            new InventoryLoadout()
                    .addItem(ItemID.COINS_995, 5, 100_000)
                    .addItem(ItemID.LAW_RUNE, 1, 5000)
                    .addItem(ItemID.SOFT_CLAY + 1, 28, 2000)
                    .setEnabledCondition(() -> !Inventory.contains(ItemID.SOFT_CLAY)),
            new EquipmentLoadout(PohTabsBranch.teleportBase)
                    .addItem(EquipmentSlot.WEAPON, ItemID.DUST_BATTLESTAFF)
    ),
    FALADOR("Falador teleport", 37,
            new InventoryLoadout()
                    .addItem(ItemID.COINS_995, 5, 100_000)
                    .addItem(ItemID.LAW_RUNE, 1, 5000)
                    .addItem(ItemID.SOFT_CLAY + 1, 28, 2000)
                    .setEnabledCondition(() -> !Inventory.contains(ItemID.SOFT_CLAY)),
            new EquipmentLoadout(PohTabsBranch.teleportBase)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MIST_BATTLESTAFF)
    ),
    HOUSE("Teleport to house", 40,
            new InventoryLoadout()
                    .addItem(ItemID.COINS_995, 5, 100_000)
                    .addItem(ItemID.LAW_RUNE, 1, 5000)
                    .addItem(ItemID.SOFT_CLAY + 1, 28, 2000)
                    .setEnabledCondition(() -> !Inventory.contains(ItemID.SOFT_CLAY)),
            new EquipmentLoadout(PohTabsBranch.teleportBase)
                    .addItem(EquipmentSlot.WEAPON, ItemID.DUST_BATTLESTAFF)
    ),
    CAMELOT("Camelot teleport", 45,
            new InventoryLoadout()
                    .addItem(ItemID.COINS_995, 5, 100_000)
                    .addItem(ItemID.LAW_RUNE, 1, 5000)
                    .addItem(ItemID.SOFT_CLAY + 1, 28, 2000)
                    .setEnabledCondition(() -> !Inventory.contains(ItemID.SOFT_CLAY)),
            new EquipmentLoadout(PohTabsBranch.teleportBase)
                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
    ),
    KOUREND("Kourend castle teleport", 48,
            new InventoryLoadout()
                    .addItem(ItemID.COINS_995, 5, 100_000)
                    .addItem(ItemID.LAW_RUNE, 1, 5000)
                    .addItem(ItemID.SOFT_CLAY + 1, 28, 2000)
                    .setEnabledCondition(() -> !Inventory.contains(ItemID.SOFT_CLAY)),
            new EquipmentLoadout(PohTabsBranch.teleportBase)
                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
    ),
    ARDOUGNE("Ardougne teleport", 51,
            new InventoryLoadout()
                    .addItem(ItemID.COINS_995, 5, 100_000)
                    .addItem(ItemID.LAW_RUNE, 1, 5000)
                    .addItem(ItemID.SOFT_CLAY + 1, 28, 2000)
                    .setEnabledCondition(() -> !Inventory.contains(ItemID.SOFT_CLAY)),
            new EquipmentLoadout(PohTabsBranch.teleportBase)
                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_WATER)
    ),
    CIV_ILLA_FORTIS("Civitas illa fortis teleport", 51,
            new InventoryLoadout()
                    .addItem(ItemID.COINS_995, 5, 100_000)
                    .addItem(ItemID.LAW_RUNE, 1, 5000)
                    .addItem(ItemID.SOFT_CLAY + 1, 28, 2000)
                    .setEnabledCondition(() -> !Inventory.contains(ItemID.SOFT_CLAY)),
            new EquipmentLoadout(PohTabsBranch.teleportBase)
                    .addItem(EquipmentSlot.WEAPON, ItemID.LAVA_BATTLESTAFF)
    ),
    // todo watchtower w/ its quest
    // todo staffless
    ;
    public final String title;
    public final int magicReq;
    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;

    PohTeleTabOption(String title, int magicReq, InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.title = title;
        this.magicReq = magicReq;
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }
}

package org.dreambot.scriptdata.loadouts;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;

public enum WyrmLoadouts {
    ;
    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;
    public final Skill mode;

    WyrmLoadouts(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout, Skill mode) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
        this.mode = mode;
    }
}

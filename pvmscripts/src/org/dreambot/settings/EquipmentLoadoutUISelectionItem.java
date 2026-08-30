package org.dreambot.settings;

import org.dreambot.fractals.loadout.EquipmentLoadout;

/**
 * used when constructing an equipment loadout ui, has a name and a loadout it offers
 */
public class EquipmentLoadoutUISelectionItem {
    public final String name;
    public final EquipmentLoadout equipmentLoadout;

    public EquipmentLoadoutUISelectionItem(String name, EquipmentLoadout equipmentLoadout) {
        this.name = name;
        this.equipmentLoadout = equipmentLoadout;
    }

    @Override
    public String toString() {
        return name.toUpperCase();
    }
}

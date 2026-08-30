package org.dreambot.behaviour.misc.tickcombat.decisions;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.fractals.TickDecision;

import java.util.HashMap;
import java.util.Map;

/**
 * some weapons need a specific index, because they have different styles for a skill, but require one for a boss
 * these are all determined by the category but im too lazy to grab all that data
 */
public class TickSetCombatIndex extends TickDecision {
    // id, index
    Map<Integer, Integer> weaponIndex = new HashMap<>();

    public TickSetCombatIndex addWeapon(int weaponId, int index) {
        weaponIndex.put(weaponId, index);
        return this;
    }

    @Override
    public boolean evaluate() {
        if (weaponIndex.containsKey(Equipment.getIdForSlot(EquipmentSlot.WEAPON))
                && weaponIndex.get(Equipment.getIdForSlot(EquipmentSlot.WEAPON)) != Combat.getCombatModeIndex())
            Combat.setCombatModeIndex(weaponIndex.get(Equipment.getIdForSlot(EquipmentSlot.WEAPON)));
        return false;
    }
}

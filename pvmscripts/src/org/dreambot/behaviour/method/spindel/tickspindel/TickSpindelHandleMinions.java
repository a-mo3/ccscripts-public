package org.dreambot.behaviour.method.spindel.tickspindel;

import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.spindel.SpindelData;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.List;

public class TickSpindelHandleMinions extends TickDecision {
    List<Integer> dartIds = Arrays.asList(
            ItemID.ADAMANT_DART,
            ItemID.RUNE_DART
    );

    @Override
    public boolean evaluate() {
        NPC spiderling = NPCs.closest(x -> x.getId() == SpindelData.SPIDERLING_ID && x.getHealthPercent() != 0);
        if (spiderling == null) {
            return false;
        }

        if (!Equipment.contains(x -> dartIds.contains(x.getId()))) {
            log("Switch to dart");
            Equipment.equip(EquipmentSlot.WEAPON, x -> dartIds.contains(x.getId()));
        }

        Character currentTgt = Players.getLocal().getInteractingCharacter();
        if (currentTgt == null || currentTgt.getId() != SpindelData.SPIDERLING_ID || currentTgt.getHealthPercent() == 0) {
            log("Attack spiderling curr " + currentTgt);
            spiderling.interact("Attack");
        }
        return true;
    }
}

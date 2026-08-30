package org.dreambot.behaviour.method.huey.mainfight;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.method.huey.HueyData;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

public class HueyEatDecision extends TickDecision {
    int lastEatTick = 0;

    @Override
    public boolean evaluate() {
        int missingHP = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
        if (missingHP < 20) {
            return false;
        }

        if (lastEatTick != 0 && Client.getGameTick() - lastEatTick < 4) {
            log("On eat cooldown");
            return false;
        }

        // eat sharks or leave
        if (Inventory.contains(ItemID.SHARK)) {
            log("Eating a shark");
            Inventory.interact(ItemID.SHARK);
            lastEatTick = Client.getGameTick();
        } else {
            log("No food left wait for <30 hp before leaving");
            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 30) {
                log("should leave, eat");
                return HueyData.leaveFight();
            } else {
                return false;
            }
        }
        return false;
    }
}

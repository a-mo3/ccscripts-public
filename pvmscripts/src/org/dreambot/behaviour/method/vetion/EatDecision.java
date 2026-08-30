package org.dreambot.behaviour.method.vetion;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

public class EatDecision extends TickDecision {
    @Override
    public boolean evaluate() {
        int missingHP = Skill.HITPOINTS.getLevel() - Skill.HITPOINTS.getBoostedLevel();
        if (missingHP > 30) {
            Logger.info("Eat " + Client.getGameTick());
            Inventory.interact(ItemID.BLIGHTED_MANTA_RAY);
        }

        return false;
    }
}

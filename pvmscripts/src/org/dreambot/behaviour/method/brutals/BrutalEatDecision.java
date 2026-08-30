package org.dreambot.behaviour.method.brutals;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

public class BrutalEatDecision extends TickDecision {
    int lastEatTick = 0;

    @Override
    public boolean evaluate() {
        int missingHP = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
        if (missingHP < 12) {
            return false;
        }

        if (lastEatTick != 0 && Client.getGameTick() - lastEatTick < 4) {
            log("On eat cooldown");
            return false;
        }

        // eat sharks or leave
        if (Inventory.contains(ItemID.JUG_OF_WINE)) {
            if (Widgets.isOpen()) Widgets.closeAll();
            log("Drink wine");
            Inventory.interact(ItemID.JUG_OF_WINE);
            lastEatTick = Client.getGameTick();
            return true;
        } else {
            log("should leave, eat");
        }
        return false;
    }
}

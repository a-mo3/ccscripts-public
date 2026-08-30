package org.dreambot.behaviour.method.barrows.killbrothers.decisions;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.List;

public class BarrowsEat extends TickDecision {
    int lastEatTick = 0;
    List<Integer> food = Arrays.asList(
            ItemID.SHARK
    );

    @Override
    public boolean evaluate() {
        if (lastEatTick > 0 && Client.getGameTick() - lastEatTick > 3) {
            log("On eat cooldown");
            return false;
        }

        int missingHP = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
        if (missingHP >= 20) {
            log("Missing enough hp to eat");
            Item foodItem = Inventory.get(x -> food.contains(x.getId()));
            if (foodItem != null) {
                log("Eat food " + foodItem);
                foodItem.interact();
            } else {
                // leave?
                log("No food!");
            }
        }
        return false;
    }
}

package org.dreambot.behaviour.method.scurrius;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;


public class ScurriusEat extends TickDecision {
    public static int lastAteTick = 0;

    public ScurriusEat() {
        setSimpleName("Eat");
    }

    @Override
    public boolean evaluate() {
        if (lastAteTick != 0 && Client.getGameTick() - lastAteTick < 3) {
            log("On eat delay");
            return false;
        }

        int missingHp = Skill.HITPOINTS.getLevel() - Skills.getBoostedLevel(Skill.HITPOINTS);
        if (missingHp > 20 && Skills.getBoostedLevel(Skill.HITPOINTS) < 40) {
            log("Should eat");
            if (!Inventory.contains(ItemID.SHARK)) {
                log("Gotta leave, no food.");
                // hopes for a teleprot here
                Walking.walk(BankLocation.GRAND_EXCHANGE);
                return false;
            }

            Inventory.interact(ItemID.SHARK);
            lastAteTick = Client.getGameTick();
        }

        return false;
    }
}

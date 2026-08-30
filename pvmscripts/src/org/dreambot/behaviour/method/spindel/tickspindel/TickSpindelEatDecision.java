package org.dreambot.behaviour.method.spindel.tickspindel;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.method.spindel.AntiCrashWildyBosses;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

public class TickSpindelEatDecision extends TickDecision {
    public static int lastAteTick = 0;
    @Override
    public boolean evaluate() {
        if (lastAteTick != 0 && Client.getGameTick() - lastAteTick < 3) {
            log("On eat delay");
            return false;
        }

        if (!Inventory.contains(ItemID.BLIGHTED_MANTA_RAY)) {
            log("Gotta leave, no food.");
            AntiCrashWildyBosses.hasToLeave = true;
            return false;
        }

        int missingHp = Skill.HITPOINTS.getLevel() - Skills.getBoostedLevel(Skill.HITPOINTS);
        if (missingHp > 22) {
            log("Should eat");
            Inventory.interact(ItemID.BLIGHTED_MANTA_RAY);
            lastAteTick = Client.getGameTick();
        }
        return false;
    }
}

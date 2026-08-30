package org.dreambot.behaviour.combattutorial;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.framework.Leaf;
import org.dreambot.util.MyVarps;

public class MeleeRatLeaf extends Leaf {
    Area ratPit = new Area(3097, 9522, 3109, 9514);
    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() < 470;
    }

    @Override
    public int onLoop() {
        if (!ratPit.contains(Players.getLocal()) && Walking.shouldWalk(2)) {
            Walking.walk(ratPit.getCenter());
        }

        NPC rat = NPCs.closest("Giant rat");
        if (rat != null && !Players.getLocal().isInCombat()) {
            rat.interact("Attack");
            Sleep.sleepUntil(Players.getLocal()::isInCombat, 5000);
        }
        return 1200;
    }
}

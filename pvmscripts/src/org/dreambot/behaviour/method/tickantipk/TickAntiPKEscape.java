package org.dreambot.behaviour.method.tickantipk;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.vetion.WildernessRunMode;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.CombatUtil;

import java.util.function.Supplier;

public class TickAntiPKEscape extends TickDecision {
    Area ESCAPE_CAVE = new Area(3325, 10301, 3392, 10242);
    final WildernessRunMode runMode;
    final Supplier<Player> topEnemy;

    public TickAntiPKEscape(WildernessRunMode runMode, Supplier<Player> topEnemy) {
        this.runMode = runMode;
        this.topEnemy = topEnemy;
    }

    @Override
    public boolean evaluate() {
        if (runMode != WildernessRunMode.RUN) {
            if (topEnemy.get() != null) return false;
        }
        log("Run away");
        if (Combat.getWildernessLevel() <= 30 && CombatUtil.get().isTeleblocked()) {
            log("Hop to clear tb");
            WorldHopper.hopWorld(Worlds.getRandomWorld(GetOff330.MEMBERS_WORLD_FILTER));
        }

        if (Walking.shouldWalk()) Walking.walk(BankLocation.EDGEVILLE);
        return true;
    }
}

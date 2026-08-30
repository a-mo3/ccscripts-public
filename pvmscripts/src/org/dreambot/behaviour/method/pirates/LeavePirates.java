package org.dreambot.behaviour.method.pirates;

import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class LeavePirates extends Fractal {
    public LeavePirates(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        // world hop to remove a tb
        if (!CombatUtil.get().isOnLogoutTimer() && CombatUtil.get().isTeleblocked()) {
            log("Hopping to remove TB");
            WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isMembers()
                    && x.isNormal() && x.getWorld() != 401
                    && x.getMinimumLevel() < Skills.getTotalLevel()
            ));
        }

        // teleports enabled on the walker should be handled by CombatUtil
        if (Walking.shouldWalk()) Walking.walk(BankLocation.FEROX_ENCLAVE);
        return ReactionGenerator.getNormal();
    }
}

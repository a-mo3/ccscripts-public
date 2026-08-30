package org.dreambot.behaviour.method.spindel;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import static org.dreambot.behaviour.method.antipk.AntiPkLeaveBosses.*;

public class AntiCrashWildyBosses extends Fractal {
    public static boolean hasToLeave = false;

    public AntiCrashWildyBosses() {
        this.acceptCondition = () -> hasToLeave;
        setSimpleName("Anti Crash");
    }

    @Override
    public int onLoop() {
        if (SPINDEL_CHASM.contains(Players.getLocal()) && Players.getLocal().isInCombat()) {
            // go to exit cave
            if (!SPINDEL_EXIT_CAVE.contains(Players.getLocal())) {
                Walking.walk(SpindelAntiPk.SPINDEL_EXIT_CAVE);
                return ReactionGenerator.getQuick();
            }

            // exit
            GameObject exit = GameObjects.closest(x -> x.hasAction("Exit"));
            if (exit != null) {
                Logger.info("Exit");
                exit.interact();
            }
            return ReactionGenerator.getQuick();
        }

        if (CALVARION_ARENA.contains(Players.getLocal()) && Players.getLocal().isInCombat()) {
            Logger.info("Exit");
            // go to exit cave
            GameObject exit = GameObjects.closest(x -> x.hasAction("Exit"));
            if (exit != null) {
                Logger.info("Exit");
                exit.interact();
            }

            if (!TOMB_EXIT.contains(Players.getLocal())) {
                Walking.walk(TOMB_EXIT);
                return ReactionGenerator.getQuick();
            }
            return ReactionGenerator.getQuick();
        }


        hasToLeave = false;
        return ReactionGenerator.getQuick();
    }
}

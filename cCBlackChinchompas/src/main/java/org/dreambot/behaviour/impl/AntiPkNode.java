package org.dreambot.behaviour.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.fractals.Fractal;

public class AntiPkNode extends Fractal {
    public static boolean shouldHop;

    @Override
    public boolean isValid() {
        return isBeingPked() || shouldHop;
    }

    @Override
    public int onLoop() {
        shouldHop = true;
        if (Combat.isInWild()) {
            Logger.info("Running away form pker");
            if (Combat.getWildernessLevel() > 30) {
                if (Walking.shouldWalk(6)) Walking.walk(Players.getLocal().getTile().translate(0, -10));
                return 200;
            }
            if (Walking.shouldWalk(6)) Walking.walk(EdgevilleBankLeaf.EDGEVILLE_BANK);
            return 200;
        }

        Logger.info("Hopping worlds");
        Calculations.setRandomSeed(System.currentTimeMillis());
        if (WorldHopper.hopWorld(Worlds.getRandomWorld(w -> w.isNormal() && w.isMembers() && w.getMinimumLevel() <= Skills.getTotalLevel())))
            shouldHop = false;
        return 250;
    }

    private boolean isBeingPked() {
        Character c = Players.getLocal().getCharacterInteractingWithMe();
        if (c == null) {
            return false;
        }
        // todo might be a better way to check this
        return Players.closest(c.getName()) != null;
    }
}

package org.dreambot.behaviour.method.nightmare.phosani;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

public class PhosaniBoostPot extends Fractal {
    static Timer lock = new Timer(800);

    public PhosaniBoostPot() {
    }

    @Override
    public boolean isValid() {
        return lock.finished()
                && ItemVariants.SUPER_COMBAT_POTION.getItem() != null
                && Skills.getBoostedLevel(Skill.STRENGTH) <= Skills.getRealLevel(Skill.STRENGTH);
    }

    @Override
    public int onLoop() {
        // todo maybe something for a super restore here

        Item boostPot = ItemVariants.SUPER_COMBAT_POTION.getItem();
        if (boostPot == null) {
            Logger.info("No boost pot found");
            return ReactionGenerator.getQuick();
        }

        if (boostPot.interact("Drink")) {
            lock.reset();
        }
        return ReactionGenerator.getQuick();
    }
}

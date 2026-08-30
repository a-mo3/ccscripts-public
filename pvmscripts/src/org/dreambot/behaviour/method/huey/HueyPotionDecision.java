package org.dreambot.behaviour.method.huey;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.loadout.ItemVariants;

public class HueyPotionDecision extends TickDecision {
    @Override
    public boolean evaluate() {
        int prayerRecover = 8 + (int) (Skills.getRealLevel(Skill.PRAYER) * 0.25);
        int missingPrayer = Skills.getRealLevel(Skill.PRAYER) - Skills.getBoostedLevel(Skill.PRAYER);
        if (prayerRecover <= missingPrayer) {
            log("Should recover prayer, drinking prayer pot");
            Item pot = ItemVariants.PRAYER_POTION.getItem(); if (pot != null) {
                log("Drink prayer");
                pot.interact();
                return false;
            } else {
                log("No prayer potion will leave when under 10 prayer currently " + Skills.getBoostedLevel(Skill.PRAYER));
                // i think theres a shortcut to leave to the bank next to huey idk when you cant take that or what its gasmeobj is
                if (Skills.getBoostedLevel(Skill.PRAYER) < 10) return HueyData.leaveFight();
            }
        }

        int missingStrengthBoost = Skills.getBoostedLevel(Skill.STRENGTH) - Skills.getRealLevel(Skill.STRENGTH);
        int maxStrBoost = (5 + (int) (Skills.getRealLevel(Skill.STRENGTH) * 0.15));
        if (missingStrengthBoost < (maxStrBoost / 2)) {
            Item pot = ItemVariants.DIVINE_SUPER_COMBAT_POTION.getItem();
            if (pot != null) {
                log("Drink super combat");
                pot.interact();
                return false;
            }
            // no super combat is benign
        }

        return false;
    }
}

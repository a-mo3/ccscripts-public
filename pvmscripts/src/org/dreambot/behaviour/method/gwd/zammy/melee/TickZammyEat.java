package org.dreambot.behaviour.method.gwd.zammy.melee;

import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

public class TickZammyEat extends TickDecision {
    public TickZammyEat() {
        setSimpleName("Zammy eat");
    }

    @Override
    public boolean evaluate() {
        int missingAtk = Skills.getRealLevel(Skill.ATTACK) - Skills.getBoostedLevel(Skill.ATTACK);
        int missingHP = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
        Item brew = ItemVariants.SARADOMIN_BREW.getItem();
        if (missingAtk > 0) {
            log("Need to restore");
            // check if we can brew again
            int possibleRestore = (int) (Skills.getRealLevel(Skill.ATTACK) * 0.25 + 8);
            int brewReduction = (int) (Skills.getBoostedLevel(Skill.ATTACK) * 0.1 + 2);
            if (brew != null && missingHP > -10 && possibleRestore >= (missingAtk + brewReduction)) {
                log("Can double brew");
                brew.interact();
                Sleep.sleep(30);
                return false;
            }

            Item restore = ItemVariants.SUPER_RESTORE.getItem();
            if (restore != null) {
                log("Restore");
                restore.interact();
                Sleep.sleep(30);
                return false;
            }
            return false;
        }

        if (missingHP > 30) {
            if (brew == null) {
                log("Out of brews");
                Walking.walk(BankLocation.EDGEVILLE);
                return true;
            }
            log("Low hp brew");
            brew.interact();
            Sleep.sleep(30);
        }
        return false;
    }
}

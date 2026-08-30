package org.dreambot.behaviour.method.gwd.zilyana.tickzilyanafight;

import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.loadout.ItemVariants;

public class KillZilyanaPotionDecision extends TickDecision {
    @Override
    public boolean evaluate() {
        // need to heal
        if (Skills.getBoostedLevel(Skill.HITPOINTS) < 45) {
            log("Under 45 needs to heal");
            Item brew = ItemVariants.SARADOMIN_BREW.getItem();
            if (brew != null) {
                log("Found brew, drinking brew");
                brew.interact();
            } else {
                log("No brew found, needs to leave");
                exitToGE();
                return true;
            }
            return false;
        }

        // needs stamina
        if (Walking.getRunEnergy() < 50) {
            log("Do stamina pot");
            Item stam = ItemVariants.STAMINA_POTION.getItem();
            if (stam != null) {
                log("Found stamina " + Walking.getRunEnergy());
                stam.interact();
                return false;
            } else  {
                log("No stamina found, should leave if <5 run!");
                if (Walking.getRunEnergy() < 5) exitToGE();
                return true;
            }

        }

        // needs prayer point
        if (Skills.getBoostedLevel(Skill.PRAYER) < 5) {
            log("Need prayer points");
            Item pot = ItemVariants.PRAYER_POTION.getItem();
            if (pot == null) pot = ItemVariants.SUPER_RESTORE.getItem();
            if (pot != null) {
                log("Found pot");
                pot.interact();
            } else {
                log("No prayer pot needs to leave");
                exitToGE();
                return true;
            }
            return false;
        }

        // needs to super restore after brew fucked up the boost
        int missingRange = Skills.getRealLevel(Skill.RANGED) - Skills.getBoostedLevel(Skill.RANGED);
        if (missingRange > 0) {
            int missingHP = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
            int possibleRestore = (int) (Skills.getRealLevel(Skill.RANGED) * 0.25 + 8);
            // the amount that another brew will reduce range
            int brewReduction = (int) (Skills.getBoostedLevel(Skill.RANGED) * 0.1 + 2);
            Item brew = ItemVariants.SARADOMIN_BREW.getItem();
            if (missingHP > 0 && possibleRestore - (missingRange + brewReduction) >= 0 && brew != null) {
                log("Should drink another brew to maximize boost");
                brew.interact();
                return false;
            }

            Item pot = ItemVariants.SUPER_RESTORE.getItem();
            if (pot != null) {
                log("Found restore");
                pot.interact();
            } else {
                log("No restore needs to leave");
                exitToGE();
                return true;
            }
            return false;
        }
        return false;
    }

    public static void exitToGE() {
        GroundItem expensiveLoot = GroundItems.closest(x -> x.getItem().getLivePrice() > 50_000);
        if (expensiveLoot != null) {
            Logger.info("We need to leave but theres something expensive im gonna try and get " + expensiveLoot);
            expensiveLoot.interact("Take");
            return;
        }

        Logger.info("Out of resource leaving zilyana");
        Walking.walk(BankLocation.GRAND_EXCHANGE);
    }
}

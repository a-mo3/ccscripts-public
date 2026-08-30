package org.dreambot.behaviour.method.vetion.tickvetion;

import org.dreambot.api.Client;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.event.impl.ExperienceEvent;
import org.dreambot.api.script.listener.ExperienceListener;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.spindel.AntiCrashWildyBosses;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.loadout.ItemVariants;

/**
 * handle drinking potions not on the tick we attack on
 * potions add no tick cooldown so we can drink whenever as long as its not on a combat tick
 */
public class VetionTickPotion extends TickDecision implements ExperienceListener {
    public VetionTickPotion() {
        Client.getInstance().addEventListener(this);
        setSimpleName("Potion");
    }

    int lastXpDrop = 0;

    @Override
    public boolean evaluate() {
        boolean isOnEatDelay = VetionTickEat.lastAteTick == 0 || Client.getGameTick() - VetionTickEat.lastAteTick < 3;
        boolean isOnCombatDelay =  lastXpDrop == 0 || Client.getGameTick() - lastXpDrop < 4; // chainmace is assumed, 4 spd weapon

        // emergency prayer pot
        if (Skills.getBoostedLevel(Skill.PRAYER) == 0) {
            log("Emergency prayer pot");
            Item prayPot = ItemVariants.BLIGHTED_SUPER_RESTORE.getItem();
            if (prayPot != null) {
                log("Prayer pot up");
                prayPot.interact();
            } else {
                log("No prayer pot!");
                AntiCrashWildyBosses.hasToLeave = true;
            }
            return false;
        }

        if (!isOnEatDelay && !isOnCombatDelay) {
            log("No delay active not an acceptable tick to pot up " + " Atk " + lastXpDrop + " Eat: " + VetionTickEat.lastAteTick);
            return false;
        }

        // prayer pot
        int missingPrayer = Skills.getRealLevel(Skill.PRAYER) - Skills.getBoostedLevel(Skill.PRAYER);
        if (missingPrayer > (7 + (Skills.getRealLevel(Skill.PRAYER) * 0.25))) {
            Item prayPot = ItemVariants.BLIGHTED_SUPER_RESTORE.getItem();
            if (prayPot != null) {
                log("Prayer pot up");
                prayPot.interact();
                return false;
            } else {
                log("No prayer pot!");
                AntiCrashWildyBosses.hasToLeave = true;
            }
        }

        // combat pot
        int missingStrengthBoost = Skills.getBoostedLevel(Skill.STRENGTH) - Skills.getRealLevel(Skill.STRENGTH);
        int maxStrBoost = (5 + (int) (Skills.getRealLevel(Skill.STRENGTH) * 0.15));
        if (missingStrengthBoost < (maxStrBoost/2)) {
            log("Less than half str bonus");
            Item combatPot = ItemVariants.SUPER_COMBAT_POTION.getItem();
            if (combatPot != null) {
                log("Combat pot up");
                combatPot.interact();
                return false;
            } else {
                log("No Combat pot!");
            }
        }
        return false;
    }

    @Override
    public void onGained(ExperienceEvent event) {
        if (event.getSkill() != Skill.HITPOINTS) {
            return;
        }
    }
}

package org.dreambot.behaviour.method.scurrius;

import org.dreambot.api.Client;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.event.impl.ExperienceEvent;
import org.dreambot.api.script.listener.ExperienceListener;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.loadout.ItemVariants;


public class ScurriusPotion extends TickDecision implements ExperienceListener {
    final ScurriusMode mode;
    public ScurriusPotion(ScurriusMode mode) {
        this.mode = mode;
        Client.getInstance().addEventListener(this);
        setSimpleName("Potion");
    }

    int lastXpDrop = 0;

    @Override
    public boolean evaluate() {
        boolean isOnEatDelay = ScurriusEat.lastAteTick == 0 || Client.getGameTick() - ScurriusEat.lastAteTick < 3;
        boolean isOnCombatDelay =  lastXpDrop == 0 || Client.getGameTick() - lastXpDrop < 4; // chainmace is assumed, 4 spd weapon

        // emergency prayer pot
        if (Skills.getBoostedLevel(Skill.PRAYER) == 0) {
            log("Emergency prayer pot");
            Item prayPot = ItemVariants.PRAYER_POTION.getItem();
            if (prayPot != null) {
                log("Prayer pot up");
                prayPot.interact();
            } else {
                log("No prayer pot!");
            }
            return false;
        }

        if (!isOnEatDelay && !isOnCombatDelay) {
            log("No delay active not an acceptable tick to pot up " + " Atk " + lastXpDrop + " Eat: " + ScurriusEat.lastAteTick);
            return false;
        }

        // prayer pot
        int missingPrayer = Skills.getRealLevel(Skill.PRAYER) - Skills.getBoostedLevel(Skill.PRAYER);
        if (missingPrayer > (7 + (Skills.getRealLevel(Skill.PRAYER) * 0.25))) {
            Item prayPot = ItemVariants.PRAYER_POTION.getItem();
            if (prayPot != null) {
                log("Prayer pot up");
                prayPot.interact();
                return false;
            } else {
                log("No prayer pot!");
            }
        }

        // combat pot
        if (mode.boostPotCondition.get()) {
            log("Less than half str bonus");
            Item combatPot = mode.boostPotion.getItem();
            if (combatPot != null) {
                log("boost pot up");
                combatPot.interact();
                return false;
            } else {
                log("No boost pot!");
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

package org.dreambot.behaviour.method.lms.deathdot;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;

public class LMSEat extends TickDecision {
    @Override
    public boolean evaluate() {
        // check the type of eat that is appropriate

        // triple heal (we always have 99 hp)
        // theal = 16 + 18 + 20 = 44
        // dheal = 18 + 20 = 38
        // shark heal = 20
        int missingHp = Skills.getBoostedLevel(Skill.HITPOINTS) - Skills.getRealLevel(Skill.HITPOINTS);
        if (missingHp < 50) return false;
        if (canTripleEat()) {
            log("Triple eat");
            tripleEat();
            LMSCounter.actionCounter += 3;
            return true;
        }

        if (canDoubleEat()) {
            log("Double eat");
            doubleEat();
            LMSCounter.actionCounter += 3;
            return true;
        }

        log("Normal eat shark" );
        // todo consider having only potion or karambwan left over
        LMSCounter.actionCounter += 3;
        Inventory.interact(ItemID.SHARK);
        return true;
    }

    private boolean canTripleEat() {
        Item i = ItemVariants.SARADOMIN_BREW.getItem();
        if (i == null) return false;
        return Inventory.containsAll(ItemID.SHARK, ItemID.COOKED_KARAMBWAN);
    }

    private boolean canDoubleEat() {
        return Inventory.containsAll(ItemID.SHARK, ItemID.COOKED_KARAMBWAN);
    }

    private void tripleEat() {
        Inventory.interact(ItemID.SHARK); // always shark in lms
        Sleep.sleep(10, 50);
        Inventory.interact(ItemVariants.SARADOMIN_BREW.getItem());
        Sleep.sleep(10, 50);
        Inventory.interact(ItemID.COOKED_KARAMBWAN); // always shark in lms
    }

    private void doubleEat() {
        Inventory.interact(ItemID.SHARK); // always shark in lms
        Sleep.sleep(10, 50);
        Inventory.interact(ItemID.COOKED_KARAMBWAN); // always shark in lms
    }
}

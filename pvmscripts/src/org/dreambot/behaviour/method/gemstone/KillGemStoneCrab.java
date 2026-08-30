package org.dreambot.behaviour.method.gemstone;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;

public class KillGemStoneCrab extends TickDecision {

    @Override
    public boolean evaluate() {
        // prayer flick aggressive prayers
//        if (Skill.PRAYER.getBoostedLevel() > 0 && Skill.PRAYER.getLevel() > 30) {
//            Prayers.toggleQuickPrayer(false);
//            Sleep.sleep(50);
//            Prayers.toggleQuickPrayer(true);
//        }

        // drink combat potion
        int strBoost = Skills.getBoostedLevel(Skill.STRENGTH) - Skills.getRealLevel(Skill.STRENGTH);
        Item boost = ItemVariants.DIVINE_SUPER_COMBAT_POTION.getItem();
        if (Skill.HITPOINTS.getBoostedLevel() > 15 && strBoost < 1 && boost != null) {
            log("Needs to get boost");
            boost.interact();
        }

        int magicBoost = Skills.getBoostedLevel(Skill.MAGIC) - Skills.getRealLevel(Skill.MAGIC);
        Item maigcPot = ItemVariants.DIVINE_MAGIC_POTION.getItem();
        if (Skill.HITPOINTS.getBoostedLevel() > 15 && magicBoost < 1 && maigcPot != null) {
            log("Needs to get boost");
            maigcPot.interact();
        }

        int rangeBoost = Skills.getBoostedLevel(Skill.RANGED) - Skills.getRealLevel(Skill.RANGED);
        Item rangePot = ItemVariants.DIVINE_RANGING_POTION.getItem();
        if (Skill.HITPOINTS.getBoostedLevel() > 15 && rangeBoost < 1 && rangePot != null) {
            log("Needs to get boost");
            rangePot.interact();
        }

        if (Skill.HITPOINTS.getBoostedLevel() < 3) {
            log("Eat a shark under 3 hp");
            Inventory.interact(ItemID.SHARK);
            return true;
        }

        // change combat style
        // todo lots of considerations here for leveling, weapons combat style, range, magic, all dat

        // attack crab
        Character tgt = Players.getLocal().getInteractingCharacter();
        NPC crab = NPCs.closest("Gemstone Crab");
        if (tgt != null && tgt.equals(crab)) {
            log("Attacking crab");
            return true;
        }
        log("Attack crab");
        if (crab != null) crab.interact();
        return false;
    }
}

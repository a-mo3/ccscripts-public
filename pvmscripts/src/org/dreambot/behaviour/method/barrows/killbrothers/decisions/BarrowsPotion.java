package org.dreambot.behaviour.method.barrows.killbrothers.decisions;

import org.dreambot.api.methods.hint.HintArrow;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.barrows.handlecrypt.HandleCryptBranch;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.loadout.ItemVariants;

public class BarrowsPotion extends TickDecision {
    @Override
    public boolean evaluate() {
        if (Skills.getBoostedLevel(Skill.PRAYER) < 5) {
            if (HandleCryptBranch.BARROWS_CRYPT.contains(Players.getLocal()) && !HintArrow.exists()) {
                log("Skipping prayer in crypt with no brother present");
                return false;
            }
            log("Needs prayer");
            Item prayerPot = ItemVariants.PRAYER_POTION.getItem();
            if (prayerPot != null) {
                prayerPot.interact();
            } else {
                log("No prayer pot");
            }
        }

        return false;
    }
}

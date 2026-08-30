package org.dreambot.behaviour.method.brutals;

import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.loadout.ItemVariants;

public class BrutalPotionDecision extends TickDecision {
    @Override
    public boolean evaluate() {
        if (shouldDrinkAntiFire()) {
            Item antiFire = ItemVariants.ANTI_FIRE_POTION.getItem();
            if (antiFire != null) {
                if (Widgets.isOpen()) Widgets.closeAll();
                log("Sip anti-fire");
                antiFire.interact();
                return false;
            }
        }

        int prayerRecover = 8 + (int) (Skills.getRealLevel(Skill.PRAYER) * 0.25);
        int missingPrayer = Skills.getRealLevel(Skill.PRAYER) - Skills.getBoostedLevel(Skill.PRAYER);
        if (prayerRecover <= missingPrayer) {
            log("Should recover prayer, drinking prayer pot");
            Item pot = ItemVariants.PRAYER_POTION.getItem();
            if (pot != null) {
                if (Widgets.isOpen()) Widgets.closeAll();
                log("Drink prayer");
                pot.interact();
            } else {
                log("No prayer potion should probably leave");
                // i think theres a shortcut to leave to the bank next to huey idk when you cant take that or what its gasmeobj is
            }
        }
        return false;
    }

    private boolean shouldDrinkAntiFire() {
        return PlayerSettings.getBitValue(3981) < 3;
    }
}

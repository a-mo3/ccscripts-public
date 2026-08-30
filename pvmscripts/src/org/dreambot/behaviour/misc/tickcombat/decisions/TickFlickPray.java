package org.dreambot.behaviour.misc.tickcombat.decisions;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.TickDecision;

@Accessors(chain = true)
public class TickFlickPray extends TickDecision {
    boolean flickPrayers;
    @Setter
    boolean disable;

    public TickFlickPray(boolean flickPrayers) {
        this.flickPrayers = flickPrayers;
        setSimpleName("Flick prayer");
    }

    @Override
    public boolean evaluate() {
        if (disable) return false;
        if (Skill.PRAYER.getBoostedLevel() == 0) {
            return false;
        }

        if (flickPrayers) {
            Prayers.toggleQuickPrayer(false);
            Sleep.sleep(50);
            Prayers.toggleQuickPrayer(true);
            Sleep.sleep(10);
        } else {
            Prayers.toggleQuickPrayer(true);
        }
        return false;
    }
}

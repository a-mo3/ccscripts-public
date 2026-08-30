package org.dreambot.behaviour.method.brutals;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PrayerUtils;

public class BrutalPrayerFlick extends TickDecision {
    final boolean disablePrayFlicking;

    public BrutalPrayerFlick(boolean disablePrayFlicking) {
        this.disablePrayFlicking = disablePrayFlicking;
    }

    public static final Area CATACOMBS_OF_KOUREND = new Area(1605, 10105, 1638, 10066);
    @Override
    public boolean evaluate() {
        if (!CATACOMBS_OF_KOUREND.contains(Players.getLocal())) {
            log("Not in catacombs disable prayer");
            PrayerUtils.disableAll();
            return false;
        }

        if (Prayers.getQuickPrayers().isEmpty()) {
            log("Empty quick prayers");
            if (!disablePrayFlicking) {
                PrayerUtils.toggle(false, Prayer.PROTECT_FROM_MAGIC);
                Sleep.sleep(50);
            }
            PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MAGIC);
            return false;
        }

        if (!disablePrayFlicking) {
            Prayers.toggleQuickPrayer(false);
            Sleep.sleep(50);
        }
        Prayers.toggleQuickPrayer(true);
        return false;
    }
}

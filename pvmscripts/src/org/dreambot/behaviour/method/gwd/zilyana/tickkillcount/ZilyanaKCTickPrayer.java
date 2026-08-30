package org.dreambot.behaviour.method.gwd.zilyana.tickkillcount;

import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.TickDecision;

public class ZilyanaKCTickPrayer extends TickDecision {
    @Override
    public boolean evaluate() {
        log("KC Tick prayer " + System.currentTimeMillis());
        Prayers.toggleQuickPrayer(false);
        Sleep.sleep(50);
        Prayers.toggleQuickPrayer(true);
        return false;
    }
}

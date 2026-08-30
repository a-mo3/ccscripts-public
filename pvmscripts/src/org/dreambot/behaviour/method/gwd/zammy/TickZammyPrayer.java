package org.dreambot.behaviour.method.gwd.zammy;

import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PVMUtil;

public class TickZammyPrayer extends TickDecision {
    @Override
    public boolean evaluate() {
        Prayers.toggle(true, PVMUtil.getBestRangePray());

        // todo check all of these are alive
//        if (ZammyCounters.zamCounter == 0) {
//            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
//            return false;
//        }

        if (ZammyCounters.rangeCounter == 0) {
            Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
            return false;
        }

        if (ZammyCounters.magicCounter == 0) {
            Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
            return false;
        }

        if (ZammyCounters.meleeCounter == 0) {
            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
            return false;
        }


        Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
        return false;
    }
}

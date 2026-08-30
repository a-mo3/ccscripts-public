package org.dreambot.behaviour.method.calvarion.tickcalv;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.fractals.TickDecision;

import java.util.Arrays;

public class CalvarionTickPrayer extends TickDecision {
    final boolean tickFlick;

    public CalvarionTickPrayer(boolean tickFlick) {
        this.tickFlick = tickFlick;
        setSimpleName("Prayer");
    }

    @Override
    public boolean evaluate() {
        NPC calv = NPCs.closest("Calvar'ion");
        if (calv == null) {
            log("No calvarion, disable prayers");
            Prayers.toggleQuickPrayer(false);
            Arrays.stream(Prayer.values()).forEach(x -> {
                Prayers.toggle(false, x);
            });
            return false;
        }

        if (tickFlick && Menu.isMenuManipulationActive()) {
            log("Flick prayers");
            Prayers.toggleQuickPrayer(false);
            Sleep.sleep(50);
            Prayers.toggleQuickPrayer(true);
        } else {
            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
        }

        return false;
    }
}

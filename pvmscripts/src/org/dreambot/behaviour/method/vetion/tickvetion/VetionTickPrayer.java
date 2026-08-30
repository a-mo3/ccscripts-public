package org.dreambot.behaviour.method.vetion.tickvetion;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.behaviour.method.vetion.VetionData;
import org.dreambot.fractals.TickDecision;

import java.util.Arrays;

public class VetionTickPrayer extends TickDecision {
    final boolean tickFlick;

    public VetionTickPrayer(boolean tickFlick) {
        this.tickFlick = tickFlick;
        setSimpleName("Prayer");
    }

    @Override
    public boolean evaluate() {
        NPC vetion = NPCs.closest(VetionData.VETION_NAME);
        if (vetion == null) {
            log("No vetion, disable prayers");
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
package org.dreambot.behaviour.method.barrows.killbrothers.decisions;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.script.listener.PaintListener;
import org.dreambot.behaviour.method.barrows.BarrowsBrother;
import org.dreambot.fractals.TickDecision;

import java.util.Arrays;

public class KillBrotherSetPrayers extends TickDecision {

    @Override
    public boolean evaluate() {
        BarrowsBrother currentlyIn = Arrays.stream(BarrowsBrother.values())
                .filter(x -> x.tombArea.contains(Players.getLocal()))
                .findAny()
                .orElse(null);
        if (currentlyIn == null) {
            log("Failed to find the brother we are inside... what?");
            return false;
        }

        Prayers.toggle(true, currentlyIn.prayerStyle);
        return false;
    }
}

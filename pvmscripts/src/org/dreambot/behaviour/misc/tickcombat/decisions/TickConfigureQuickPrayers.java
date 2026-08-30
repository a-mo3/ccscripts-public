package org.dreambot.behaviour.misc.tickcombat.decisions;

import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.fractals.TickDecision;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Supplier;

public class TickConfigureQuickPrayers extends TickDecision {
    Supplier<Prayer[]> prayers;

    public TickConfigureQuickPrayers(Supplier<Prayer[]> prayers) {
        this.prayers = prayers;
    }

    @Override
    public boolean evaluate() {
        if (prayers == null) return false;
        if (prayers.get() == null) return false;
        log(Prayers.getQuickPrayers() + " ");
        boolean should = Prayers.getQuickPrayers().isEmpty() || !new HashSet<>(Prayers.getQuickPrayers()).containsAll(Arrays.asList(prayers.get()));
        if (!should) return false;
        if (Widgets.isOpen()) {
            Widgets.closeAll();
            log("Close all widgets");
        }

        log("Configure prayers " + Arrays.toString(prayers.get()));
        Prayers.setupQuickPrayers(prayers.get());
        return true;
    }
}

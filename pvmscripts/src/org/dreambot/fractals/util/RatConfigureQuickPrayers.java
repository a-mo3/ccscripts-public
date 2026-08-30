package org.dreambot.fractals.util;

import org.dreambot.api.Client;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Supplier;

public class RatConfigureQuickPrayers extends Fractal {
    Supplier<Prayer[]> prayers;

    public RatConfigureQuickPrayers(Supplier<Prayer[]> prayers) {
        super(() -> !Client.isDynamicRegion()
                && (Prayers.getQuickPrayers().isEmpty() || !new HashSet<>(Prayers.getQuickPrayers()).containsAll(Arrays.asList(prayers.get()))));
        this.prayers = prayers;
    }

    public RatConfigureQuickPrayers(Supplier<Boolean> acceptCondition, Supplier<Prayer[]> prayers) {
        super(() -> acceptCondition.get()
                && (Prayers.getQuickPrayers().isEmpty() || !new HashSet<>(Prayers.getQuickPrayers()).containsAll(Arrays.asList(prayers.get()))));
        this.prayers = prayers;
    }

    @Override
    public int onLoop() {
        if (Widgets.isOpen()) {
            Widgets.closeAll();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("");

        }

        log("Configure prayers " + Arrays.toString(prayers.get()));
        Prayers.setupQuickPrayers(prayers.get());
        return ReactionGenerator.getNormal();
    }
}

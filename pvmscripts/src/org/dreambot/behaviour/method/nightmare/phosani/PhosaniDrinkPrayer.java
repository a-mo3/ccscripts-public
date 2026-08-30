package org.dreambot.behaviour.method.nightmare.phosani;

import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.nightmare.PhosaniBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class PhosaniDrinkPrayer extends Fractal {
    static Timer t = new Timer(800);

    public PhosaniDrinkPrayer(Supplier<Boolean> acceptCondition) {
        super(() -> t.finished() & acceptCondition.get());
    }


    @Override
    public int onLoop() {
        Item prayerPot = ItemVariants.PRAYER_POTION.getItem();
        if (prayerPot == null) {
            Logger.info("No prayer pot - leaving phosani");
            PhosaniBranch.exitPhosani();
            return ReactionGenerator.getQuick();
        }

        prayerPot.interact("Drink");
        t.reset();
        return ReactionGenerator.getQuick();
    }
}

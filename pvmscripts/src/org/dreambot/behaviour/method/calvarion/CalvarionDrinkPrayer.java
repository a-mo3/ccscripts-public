package org.dreambot.behaviour.method.calvarion;

import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class CalvarionDrinkPrayer extends Fractal {
    private static final Timer lock = new Timer(1200);

    public CalvarionDrinkPrayer(Supplier<Boolean> acceptCondition) {
        super(() -> lock.finished() && acceptCondition.get());
    }

    @Override
    public int onLoop() {
        Item prayerPot = ItemVariants.BLIGHTED_SUPER_RESTORE.getItem();
        if (prayerPot == null) {
            Logger.warn("No prayer pot (blighted super restore)");
            return LeaveCalvarion.leaveCalvarion();
        }

        lock.reset();
        prayerPot.interact("Drink");
        return ReactionGenerator.getQuick();
    }
}

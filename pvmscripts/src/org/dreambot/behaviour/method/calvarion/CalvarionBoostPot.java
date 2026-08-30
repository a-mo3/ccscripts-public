package org.dreambot.behaviour.method.calvarion;

import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.CalvarionSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class CalvarionBoostPot extends Fractal {
    private static final Timer lock = new Timer(1200);

    public CalvarionBoostPot(Supplier<Boolean> acceptCondition) {
        super(() -> lock.finished() && acceptCondition.get()
                && (SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat ? ItemVariants.SUPER_COMBAT_POTION.getItem() : ItemVariants.STRENGTH_POTION.getItem()) != null
        );
    }

    @Override
    public int onLoop() {
        Item prayerPot = SettingsRepository.findInstanceOf(new CalvarionSettings()).useSuperCombat ? ItemVariants.SUPER_COMBAT_POTION.getItem() : ItemVariants.STRENGTH_POTION.getItem();
        if (prayerPot == null) {
            Logger.warn("No boost pot");
            return LeaveCalvarion.leaveCalvarion();
        }

        lock.reset();
        prayerPot.interact("Drink");
        return ReactionGenerator.getQuick();
    }
}

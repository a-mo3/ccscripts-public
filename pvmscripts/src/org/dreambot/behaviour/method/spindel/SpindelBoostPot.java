package org.dreambot.behaviour.method.spindel;

import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.SpindelSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class SpindelBoostPot extends Fractal {
    private static Timer lock = new Timer(1200);

    public SpindelBoostPot(Supplier<Boolean> acceptCondition) {
        super(() -> lock.finished() && acceptCondition.get());
    }

    @Override
    public int onLoop() {
        // todo add str potions for melee
        Item prayerPot = SettingsRepository.findInstanceOf(new SpindelSettings()).loadout.isRange ? ItemVariants.RANGING_POTION.getItem() : ItemVariants.STRENGTH_POTION.getItem();
        if (prayerPot == null) {
            Logger.warn("No boost pot");
            return SpindelAntiPk.leaveSpindel();
        }

        prayerPot.interact("Drink");
        return ReactionGenerator.getQuick();
    }
}

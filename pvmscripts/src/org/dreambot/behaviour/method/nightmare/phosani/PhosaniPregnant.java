package org.dreambot.behaviour.method.nightmare.phosani;

import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

public class PhosaniPregnant extends Fractal {
    public static boolean isPreggers() {
        return PlayerSettings.getBitValue(10151) == 1;
    }

    static Timer t = new Timer(800);

    public PhosaniPregnant() {
        super(() -> t.finished() && isPreggers());
        setSimpleName("Pregnant");
    }

    @Override
    public int onLoop() {
        Item sanfew = ItemVariants.SANFEW_SERUM.getItem();
        if (sanfew == null) {
            Logger.info("No sanfew serum, exit");
            // todo exit
            return ReactionGenerator.getQuick();
        }

        sanfew.interact("Drink");
        t.reset();
        return ReactionGenerator.getQuick();
    }
}

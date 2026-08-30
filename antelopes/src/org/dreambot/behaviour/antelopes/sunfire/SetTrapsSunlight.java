package org.dreambot.behaviour.antelopes.sunfire;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

public class SetTrapsSunlight extends Fractal {
    @Override
    public boolean isValid() {
        return true;
    }

    boolean logLock = false;

    @Override
    public int onLoop() {
        if (Inventory.contains(ItemID.JUG)) Inventory.dropAll(ItemID.JUG);

        if (!Inventory.contains("Logs") || logLock) {
            logLock = Inventory.count("Logs") < 3;

            if (Inventory.isFull()) {
                Inventory.dropAll(ItemID.SUNLIGHT_ANTELOPE_FUR, ItemID.SUNLIGHT_ANTELOPE_ANTLER, ItemID.RAW_SUNLIGHT_ANTELOPE, ItemID.BIG_BONES);
                return ReactionGenerator.getNormal();
            }

            GameObject roots = GameObjects.closest(x -> x.getName().equals("Tree") && x.hasAction("Chop down"));
            Logger.info("Take logs " + roots);
            if (roots != null) {
                roots.interact("Chop down");
                Sleep.sleepUntil(() -> Inventory.count(ItemID.LOGS) == 3, 2600, 100);
            }
            return ReactionGenerator.getNormal();
        }

        GameObject northPit = GameObjects.closest(51676); // todo shuffle fractal this out to like 3-4 spots
        Logger.info("North pit " + northPit);
        if (northPit != null && northPit.interact()) {
            Sleep.sleepUntil(() -> !northPit.exists() || northPit.getName().toLowerCase().contains("spiked"), 2400);
        }

        return ReactionGenerator.getNormal();
    }
}

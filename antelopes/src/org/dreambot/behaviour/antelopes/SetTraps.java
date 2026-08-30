package org.dreambot.behaviour.antelopes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

public class SetTraps extends Fractal {
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
                Inventory.dropAll(ItemID.MOONLIGHT_ANTELOPE_FUR, ItemID.BIG_BONES);
                return ReactionGenerator.getNormal();
            }

            GameObject roots = GameObjects.closest(x -> x.getName().equals("Roots") && x.hasAction("Take-log"));
            Logger.info("Take logs " + roots);
            if (roots != null) {
                roots.interact("Take-log");
            }
            return ReactionGenerator.getNormal();
        }

        GameObject northPit = GameObjects.closest(51678);
        Logger.info("North pit " + northPit);
        if (northPit != null && northPit.interact()) {
            Sleep.sleepUntil(() -> northPit.getName().toLowerCase().contains("spiked"), 2400);
        }

        return ReactionGenerator.getNormal();
    }
}

package org.dreambot.behaviour.antelopes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

public class LootAntelopes extends Fractal {
    @Override
    public boolean isValid() {
        return GameObjects.closest("Collapsed trap") != null;
    }

    @Override
    public int onLoop() {
        if (Inventory.emptySlotCount() < 3) {
            Inventory.dropAll(ItemID.MOONLIGHT_ANTELOPE_FUR, ItemID.BIG_BONES, ItemID.SUNLIGHT_ANTELOPE_ANTLER, ItemID.SUNLIGHT_ANTELOPE_FUR, ItemID.RAW_SUNLIGHT_ANTELOPE);
            return ReactionGenerator.getNormal();
        }

        GameObject collapsedTrap = GameObjects.closest(x -> x.hasAction("Dismantle")
                && x.getName().equals("Collapsed trap"));
        if (collapsedTrap != null && collapsedTrap.interact("Dismantle")) {
            Sleep.sleepUntil(() -> !collapsedTrap.exists(), 2400);
        }

        return ReactionGenerator.getNormal();
    }
}

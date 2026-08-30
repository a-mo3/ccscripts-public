package org.dreambot.behaviour.antelopes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

public class ScavangeAntelopes extends Fractal {
    public ScavangeAntelopes() {
    }

    @Override
    public boolean isValid() {
        GroundItem loot = GroundItems.closest(ItemID.RAW_MOONLIGHT_ANTELOPE, ItemID.MOONLIGHT_ANTELOPE_ANTLER);
        return loot != null && ScriptSettings.getSettingsData().looting && Players.getLocal().getCharacterInteractingWithMe() == null;
    }

    @Override
    public int onLoop() {
        if (Inventory.isFull()) {
            Inventory.dropAll(ItemID.MOONLIGHT_ANTELOPE_FUR, ItemID.BIG_BONES);
            return ReactionGenerator.getNormal();
        }

        GroundItem loot = GroundItems.closest(ItemID.RAW_MOONLIGHT_ANTELOPE, ItemID.MOONLIGHT_ANTELOPE_ANTLER);
        if (loot != null && loot.interact("Take")) {
            Sleep.sleepUntil(() -> !loot.exists(), 1200);
        }

        return ReactionGenerator.getNormal();
    }
}

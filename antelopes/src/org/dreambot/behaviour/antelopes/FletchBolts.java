package org.dreambot.behaviour.antelopes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

public class FletchBolts extends Fractal {
    @Override
    public boolean isValid() {
        return ScriptSettings.getSettingsData().fletch && Inventory.contains(ItemID.SUNLIGHT_ANTELOPE_ANTLER, ItemID.MOONLIGHT_ANTELOPE_ANTLER)
                && Inventory.contains(ItemID.CHISEL) && !Players.getLocal().isHealthBarVisible();
    }

    @Override
    public int onLoop() {
        if (Inventory.contains(ItemID.MOONLIGHT_ANTELOPE_ANTLER)) {
            Inventory.combine(ItemID.CHISEL, ItemID.MOONLIGHT_ANTELOPE_ANTLER);
            Sleep.sleepUntil(() -> ItemProcessing.isOpen() || !Inventory.contains(ItemID.MOONLIGHT_ANTELOPE_ANTLER), 2400);
            if (ItemProcessing.isOpen()) {
                ItemProcessing.makeAll(ItemID.MOONLIGHT_ANTLER_BOLTS);
                Sleep.sleepUntil(() -> !Inventory.contains(ItemID.MOONLIGHT_ANTELOPE_ANTLER), 3500);
            }
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.SUNLIGHT_ANTELOPE_ANTLER)) {
            Inventory.combine(ItemID.CHISEL, ItemID.SUNLIGHT_ANTELOPE_ANTLER);
            Sleep.sleepUntil(() -> ItemProcessing.isOpen() || !Inventory.contains(ItemID.SUNLIGHT_ANTELOPE_ANTLER), 2400);
            if (ItemProcessing.isOpen()) {
                ItemProcessing.makeAll(ItemID.SUNLIGHT_ANTLER_BOLTS);
                Sleep.sleepUntil(() -> !Inventory.contains(ItemID.SUNLIGHT_ANTELOPE_ANTLER), 3500);
            }
            return ReactionGenerator.getNormal();
        }

        return ReactionGenerator.getNormal();
    }
}

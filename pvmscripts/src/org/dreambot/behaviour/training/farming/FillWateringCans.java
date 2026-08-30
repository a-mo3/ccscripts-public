package org.dreambot.behaviour.training.farming;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

public class FillWateringCans extends Fractal {
    public FillWateringCans() {
        super(() -> !Inventory.contains(x -> x.getName().contains("Watering can (")));
    }

    @Override
    public int onLoop() {
        // get water cans
        if (!Inventory.contains(ItemID.WATERING_CAN)) {
            return ReactionGenerator.getNormal();
        }

        if (Client.isDynamicRegion()) {
            // exit house, assume rimmington and go to fountain outside
            return ReactionGenerator.getNormal();
        }

        // use on fountain
        return ReactionGenerator.getNormal();
    }
}

package org.dreambot.behaviour.training.agility;

import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

public class PickupMOG extends Fractal {
    public PickupMOG() {
        setSimpleName("Pickup MOG");
    }

    @Override
    public boolean isValid() {
        return getMOG() != null;
    }

    @Override
    public int onLoop() {
        GroundItem i = getMOG();
        if (i != null && i.interact("Take")) Sleep.sleepUntil(() -> !i.exists(), 4400);
        return ReactionGenerator.getNormal();
    }

    private GroundItem getMOG() {
        return GroundItems.closest(x -> x.canReach() && x.getItem().getId() == ItemID.MARK_OF_GRACE);
    }
}

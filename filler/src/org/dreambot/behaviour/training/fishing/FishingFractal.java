package org.dreambot.behaviour.training.fishing;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

@Accessors(chain = true)
public class FishingFractal extends Fractal {
    // even if some of these go unused they will likely be needed later

    Area targetArea;
    Supplier<NPC> spotSupplier;
    @Setter
    boolean shouldBank;
    @Setter
    String interaction = "Bait";

    public FishingFractal(Supplier<Boolean> acceptCondition, Area targetArea, Supplier<NPC> spotSupplier) {
        super(acceptCondition);
        this.targetArea = targetArea;
        this.spotSupplier = spotSupplier;
    }

    @Override
    public int onLoop() {
        if (Inventory.isFull()) {
            Inventory.dropAll(ItemID.RAW_ANCHOVIES, ItemID.RAW_SHRIMPS, ItemID.RAW_SALMON, ItemID.RAW_TROUT);
            return ReactionGenerator.getNormal();
        }

        if (!targetArea.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(targetArea);
            return ReactionGenerator.getNormal();
        }

        NPC fishy = spotSupplier.get();
        if (fishy != null && targetArea.contains(fishy)) {
            fishy.interact(interaction);
            Sleep.sleepUntil(Inventory::isFull, () -> Players.getLocal().isAnimating(), 1600, 100);
        }
        return ReactionGenerator.getNormal();
    }
}

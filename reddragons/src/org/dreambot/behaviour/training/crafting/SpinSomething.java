package org.dreambot.behaviour.training.crafting;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class SpinSomething extends Fractal {
    public static final Tile LOOM_AREA = new Tile(3209, 3213, 1);
    final int supplyId;
    final int restockQuantity;
    final int productId;

    public SpinSomething(Supplier<Boolean> acceptCondition, int supplyId, int restockQuantity, int productId) {
        super(acceptCondition);
        this.supplyId = supplyId;
        this.restockQuantity = restockQuantity;
        this.productId = productId;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(supplyId, 1, 28).setRefill(restockQuantity)
        ;
    }

    @Override
    public int onLoop() {
        if (!LOOM_AREA.equals(Players.getLocal().getTile())) {
            if (Walking.shouldWalk()) Walking.walkExact(LOOM_AREA);
            return ReactionGenerator.getQuick();
        }

        if (ItemProcessing.isOpen()) {
            ItemProcessing.makeAll(productId);
            Sleep.sleepUntil(() -> !Inventory.contains(supplyId), () -> Players.getLocal().isAnimating(), 1200, 100);
            return ReactionGenerator.getNormal();
        }

        GameObject loom = GameObjects.closest("Spinning wheel");
        if (loom != null && loom.interact("Spin")) {
            Sleep.sleepUntil(ItemProcessing::isOpen, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}

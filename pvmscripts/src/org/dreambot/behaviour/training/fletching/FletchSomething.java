package org.dreambot.behaviour.training.fletching;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class FletchSomething extends Fractal {
    final int logId;
    final int product;

    public FletchSomething(Supplier<Boolean> acceptCondition, int logId, int product, int restock) {
        super(acceptCondition);
        this.logId = logId;
        this.product = product;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.KNIFE)
                .addItem(logId, 1, 27)
                .setRefill(restock)
                .setStrict(true)
        ;
    }

    @Override
    public int onLoop() {
        if (Widgets.isOpen()) Widgets.closeAll();
        if (ItemProcessing.isOpen()) {
            log("Making bows");
            ItemProcessing.makeAll(product);
            Sleep.sleepUntil(() -> !Inventory.contains(logId), () -> Players.getLocal().isAnimating(), 4000, 100);
            return ReactionGenerator.getNormal();
        }

        log("Cutting log");
        Inventory.combine(ItemID.KNIFE, logId);
        Sleep.sleepUntil(ItemProcessing::isOpen, 3200);
        return ReactionGenerator.getNormal();
    }
}

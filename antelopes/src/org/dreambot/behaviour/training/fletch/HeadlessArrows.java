package org.dreambot.behaviour.training.fletch;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class HeadlessArrows extends Fractal {
    public HeadlessArrows(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.ARROW_SHAFT, 1, 7000)
                .addItem(ItemID.FEATHER, 1, 8000).setBuyPrice(3)
        ;
    }

    @Override
    public int onLoop() {
        if (Bank.isOpen() || GrandExchange.isOpen()) {
            Widgets.closeAll();
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.canContinue()) {
            Logger.info("Dialogue solve");
            Dialog.solve();
        }

        if (ItemProcessing.isOpen()) {
            if (ItemProcessing.getSelectedQuantity() != 10) ItemProcessing.setSelectedQuantity(10);
            ItemProcessing.makeAll("Headless arrow");
            Sleep.sleepUntil(
                    () -> !Inventory.containsAll(ItemID.HEADLESS_ARROW, ItemID.FEATHER) || Dialogues.canContinue(),
                    () -> Players.getLocal().isAnimating(),
                    2400,
                    100
            );
            return ReactionGenerator.getNormal();
        }


        Item feather = Inventory.get(ItemID.FEATHER);
        Item shaft = Inventory.get(ItemID.ARROW_SHAFT);
        if (feather != null && shaft != null) {
            feather.useOn(shaft);
            Sleep.sleepUntil(ItemProcessing::isOpen, 2400);
        }
        return ReactionGenerator.getQuick();
    }

}

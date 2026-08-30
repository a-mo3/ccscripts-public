package org.dreambot.behaviour;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
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

import java.text.DecimalFormat;
import java.util.function.Supplier;

public class StringBows extends Fractal {
    final int targetID;
    final int unstrungBowID;
    final int restockQuantity;

    public StringBows(Supplier<Boolean> acceptCondition, int unstrungBowID, int restockQuantity, int strungBowID) {
        super(acceptCondition);
        this.targetID = strungBowID;
        this.unstrungBowID = unstrungBowID;
        this.restockQuantity = restockQuantity;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.BOW_STRING, 14).setRefill(restockQuantity)
                .addItem(unstrungBowID, 14).setRefill(restockQuantity)
        ;
    }

    DecimalFormat df = new DecimalFormat("###,###,###");

    @Override
    public int onLoop() {
        // combine bow and such
        if (Dialogues.canContinue()) {
            Logger.info("Dialogue solve");
            Dialog.solve();
        }

        if (ItemProcessing.isOpen()) {
            Logger.info(targetID);
            ItemProcessing.makeAll(targetID);
            Sleep.sleepUntil(() -> !Inventory.contains(unstrungBowID) || Dialogues.canContinue(),
                    () -> Players.getLocal().isAnimating(),
                    2200, 100);
            return ReactionGenerator.getNormal();
        }

        if (Widgets.isOpen()) Widgets.closeAll();
        Item unstrung = Inventory.get(unstrungBowID);
        Item string = Inventory.get(ItemID.BOW_STRING);
        unstrung.useOn(string);
        Sleep.sleepUntil(Dialogues::inDialogue, 2400);
        return ReactionGenerator.getNormal();
    }
}

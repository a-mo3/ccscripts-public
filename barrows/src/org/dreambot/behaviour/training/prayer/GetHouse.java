package org.dreambot.behaviour.training.prayer;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class GetHouse extends Fractal {
    final Area VARROACK_ESTATE = new Area(2981, 3370, 2984, 3368);

    final InventoryLoadout coins = new InventoryLoadout()
            .addItem(ItemID.COINS_995, 6000, 12000);

    // iirc 0 = no house > 0 != 1 = house but not at rimmington
    private int getHouseState() {
        return PlayerSettings.getBitValue(2187);
    }

    @Override
    public boolean isValid() {
        return getHouseState() != 1;
    }

    @Override
    public int onLoop() {
        // im only assuming u just have no house not have a different house
        if (!coins.isFulfilled()) {
            new WithdrawLoadoutEvent(coins, null)
                    .executed();
            return ReactionGenerator.getNormal();
        }

        if (Bank.isOpen() || GrandExchange.isOpen()) {
            Widgets.closeAll();
        }

        // you have a house, but its not at rimmington or else this fractal wouldnt accept
        if (getHouseState() > 0 && Widgets.isOpen()) {
            WidgetChild rimmingtonButton = Widgets.get(x -> x.getText().contains("Rimmington (5,000)"));
            if (rimmingtonButton == null) {
                Logger.info("Cant find the rimmington button");
                return ReactionGenerator.getNormal();
            }

            rimmingtonButton.interact();
            Sleep.sleepUntil(() -> getHouseState() == 1, 4500);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve(
                    "How can I get a house?",
                    "Yes please!"
            );
            return ReactionGenerator.getNormal();
        }

        if (!VARROACK_ESTATE.contains(Players.getLocal())) {
            Walking.walk(VARROACK_ESTATE.getCenter());
            return ReactionGenerator.getNormal();
        }

        NPC estate = NPCs.closest("Estate agent");
        if (estate != null) {
            if (!estate.canReach()) {
                Walking.walk(estate.getTile());
                return ReactionGenerator.getNormal();
            }

            if (getHouseState() > 0) {
                Logger.info("Move house, relocate");
                estate.interact("Relocate");
                return ReactionGenerator.getNormal();
            }

            if (estate.interact("Talk-to")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            }
        }
        return ReactionGenerator.getNormal();
    }

}

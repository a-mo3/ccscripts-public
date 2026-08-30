package org.dreambot.behaviour.training.slayer;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

public class GetIceCoolers extends AbstractResponseEvent<GetIceCoolers.Response> {
    enum Response {
        NO_MORE_COINS,
        SUCCESS,
        TIMEOUT,
    }

    Timer timeout = new Timer(60 * 1000 * 8);
    private static final Area TURAEL = new Area(2929, 3538, 2933, 3535);

    @Override
    public int onLoop() {
        if (Inventory.count(ItemID.COINS_995) < 100) {
            Logger.info("Ice coolers get coins");
            new WithdrawLoadoutEvent(
                    new InventoryLoadout().addItem(ItemID.COINS_995, 4000)
                            .addItem(ItemVariants.GAMES_NECKLACE)
                            .setStrict(true),
                    null).executed();
            return ReactionGenerator.getNormal();
        }

        if (timeout.finished()) {
            Logger.info("Ice coolers timeout");
            setResponse(Response.TIMEOUT);
            return ReactionGenerator.getNormal();
        }

        if (!TURAEL.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(TURAEL);
            return ReactionGenerator.getNormal();
        }

        if (OwnedItems.count(ItemID.ICE_COOLER) > 500) {
            Logger.info("Got ice coolers");
            setResponse(Response.SUCCESS);
            return ReactionGenerator.getNormal();
        }

        if (Shop.isOpen()) {
            Logger.info("Buying ice coolers");
            Shop.purchase(ItemID.ICE_COOLER, 50);
            return ReactionGenerator.getNormal();
        }

        NPC turael = NPCs.closest("Turael");
        if (turael != null && turael.interact("Trade")) {
            Logger.info("Trade turael");
            Sleep.sleepUntil(Shop::isOpen, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}

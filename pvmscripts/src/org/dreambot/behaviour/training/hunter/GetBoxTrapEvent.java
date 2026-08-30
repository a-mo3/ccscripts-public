package org.dreambot.behaviour.training.hunter;

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
import org.dreambot.settings.timing.ReactionGenerator;

public class GetBoxTrapEvent extends AbstractResponseEvent<GetBoxTrapEvent.Response> {
    Area HUNTING_STORE = new Area(2564, 3085, 2570, 3079);
    Timer timeout = new Timer(3 * 60 * 1000);

    enum Response {
        GOT_TRAPS,
        TIMEOUT
    }

    @Override
    public int onLoop() {
        if (timeout.finished()) {
            setResponse(Response.TIMEOUT);
            return ReactionGenerator.getNormal();
        }

        if (!HUNTING_STORE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(HUNTING_STORE);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.count(ItemID.COINS_995) < 5_000) {
            if (Shop.isOpen()) Shop.close();

            if (Inventory.contains(ItemID.BOX_TRAP_PACK)) {
                Inventory.interact(ItemID.BOX_TRAP_PACK, "Open");
                return ReactionGenerator.getNormal();
            } else {
                setResponse(Response.GOT_TRAPS);
            }
            return ReactionGenerator.getNormal();
        }

        if (Shop.isOpen()) {
            if (Shop.contains(ItemID.BOX_TRAP_PACK)) {
                Logger.info("buying box traps");
                Shop.purchase(ItemID.BOX_TRAP_PACK, 3);
                return ReactionGenerator.getNormal();
            }
        }


        NPC aleck = NPCs.closest("Aleck");
        if (aleck != null) {
            aleck.interact("Trade");
            Sleep.sleepUntil(Shop::isOpen, 2400);
            return ReactionGenerator.getNormal();
        }


        return ReactionGenerator.getNormal();
    }
}

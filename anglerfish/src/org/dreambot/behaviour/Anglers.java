package org.dreambot.behaviour;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class Anglers extends Fractal {
    public Anglers(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.FISHING_ROD)
                .addItem(ItemID.SANDWORMS, 1, 1600)
        ;
    }

    public static final Area ANGLER_AREA = new Area(1825, 3780, 1844, 3765);

    @Override
    public int onLoop() {
        if (Inventory.isFull()) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open(BankLocation.PORT_PISCARILIUS);
                return ReactionGenerator.getNormal();
            }

            Bank.depositAll(ItemID.RAW_ANGLERFISH);
            return ReactionGenerator.getNormal();
        }

        if (!ANGLER_AREA.contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(ANGLER_AREA);
            return ReactionGenerator.getQuick();
        }

        NPC spot = NPCs.closest(x -> x.getName().equals("Rod Fishing spot") && ANGLER_AREA.contains(x));
        Logger.info("Spot: " + spot);
        if (spot != null && spot.interact("Bait")) {
            Sleep.sleepUntil(() -> Inventory.isFull() || !spot.exists(),
                    () -> Players.getLocal().isAnimating(),
                    2100,
                    100);
        }

        return ReactionGenerator.getNormal();
    }
}

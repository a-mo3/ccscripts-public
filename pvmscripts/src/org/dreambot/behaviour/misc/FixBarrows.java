package org.dreambot.behaviour.misc;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.timing.ReactionGenerator;

public class FixBarrows extends Fractal {
    Area BOBS_AXES = new Area(3228, 3205, 3233, 3201);
    String isBarrowRegex = ".*(Guthan's|Karil's|Ahrim's|Dharok's|Torag's|Verac's).*[0-9]+";

    public FixBarrows() {
        setSimpleName("Barrows");
    }

    @Override
    public boolean isValid() {
        // if any piece of barrows equipment is broken we will repair all the damaged
        return OwnedItems.contains(x -> x.getName().endsWith(" 0") && x.getName().matches(isBarrowRegex));
    }

    @Override
    public int onLoop() {
        if (Inventory.isFull()) {
            log("Bank all");
            new BankAllInventoryEvent().execute();
        }

        if (Bank.isCached() && OwnedItems.count(ItemID.COINS_995) < 100_000) {
            log("Need 100k for barrows recovery");

            if (OwnedItems.containsAnyUnworn(MuleOff.LOOT)) {
                log("Selling loot for money");
                new SellAllEvent(MuleOff.LOOT)
                        .execute();
                return ReactionGenerator.getNormal();
            }

            log("Mule request event");
            new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                    .addRequiredItem(ItemID.COINS_995, 105_000)
                    .execute();
            return ReactionGenerator.getNormal();
        }

        if (Bank.contains(x -> x.getName().matches(isBarrowRegex)) || Inventory.count(ItemID.COINS_995) < 100_000 || Bank.contains(ItemID.COINS_995)) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open(BankLocation.GRAND_EXCHANGE);
                return ReactionGenerator.getNormal();
            }

            if (Bank.contains(ItemID.COINS_995)) {
                log("Get all coins");
                Bank.withdrawAll(ItemID.COINS_995);
                return ReactionGenerator.getNormal();
            }

            log("Get all barrows");
            Bank.withdrawAll(x -> x.getName().matches(isBarrowRegex));
            return ReactionGenerator.getNormal();
        }

        if (Bank.isOpen()) Bank.close();

        if (Equipment.contains(x -> x.getName().matches(isBarrowRegex))) {
            log("Unequip barrows");
            Equipment.unequip(x -> x.getName().matches(isBarrowRegex));
            return ReactionGenerator.getNormal();
        }

        if (!BOBS_AXES.contains(Players.getLocal())) {
            log("Go to bob");
            Walking.walk(BOBS_AXES.getCenter());
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            log("Repair dialogue");
            Dialog.solve("Repair all", "Repair");
            return ReactionGenerator.getNormal();
        }

        NPC bob = NPCs.closest("Bob");
        if (bob != null) {
            bob.interact("Repair");
            Antiban.sleepUntil(Dialogues::inDialogue, 5000);
            return ReactionGenerator.getNormal();
        }

        return ReactionGenerator.getNormal();
    }
}
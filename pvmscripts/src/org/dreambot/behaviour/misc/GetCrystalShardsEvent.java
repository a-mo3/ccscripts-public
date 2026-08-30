package org.dreambot.behaviour.misc;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * buy enhanced crystal teleport seed, go to prif, recharge teleport crystal, trade in for shards
 */
public class GetCrystalShardsEvent extends AbstractResponseEvent<GetCrystalShardsEvent.Response> {
    enum Response {
        GOT_SHARDS,
        NO_TELEPORT,
    }

    final Area PRIFDDINAS = new Area(3199, 6149, 3328, 6011);
    final Area ELUNED = new Area(3223, 6067, 3236, 6059); // Elund charges the teleport sseeds, "Enchant"
    final Area AMROD = new Area(3234, 6126, 3240, 6123); // Guy in pub that trades enchanted jaunt for, interact with "Trade" then select item w/ ItemProcessing

    List<Integer> unchargedIds = Arrays.asList(
            ItemID.BOW_OF_FAERDHINEN_INACTIVE,
            ItemID.CRYSTAL_HELM_INACTIVE,
            ItemID.CRYSTAL_BODY_INACTIVE,
            ItemID.CRYSTAL_LEGS_INACTIVE
    );


    @Override
    public int onLoop() {
        if (!OwnedItems.contains(x -> x.getName().contains("Teleport crystal ("))) {
            Logger.info("We require a charged teleport crystal to recharge crystal items");
            setResponse(Response.NO_TELEPORT);
            return ReactionGenerator.getNormal();
        }

        int requiredShards = OwnedItems.count(x -> unchargedIds.contains(x.getId())) * 100;

        if (Equipment.contains(x -> unchargedIds.contains(x.getId()))) {
            Equipment.unequip(x -> unchargedIds.contains(x.getId()));
            return ReactionGenerator.getNormal();
        }

        if (OwnedItems.count(ItemID.CRYSTAL_SHARD) <= requiredShards) {
            setResponse(Response.GOT_SHARDS); // returns to fractal to charge items
            return ReactionGenerator.getNormal();
        }

        if (!OwnedItems.contains(ItemID.ENHANCED_CRYSTAL_TELEPORT_SEED)) {
            new WithdrawLoadoutEvent(new InventoryLoadout()
                    .addItem(ItemID.ENHANCED_CRYSTAL_TELEPORT_SEED, (int) Math.ceil((double) requiredShards / 150.00)),
                    null).executed();
            return ReactionGenerator.getNormal();
        }

        if (!Inventory.contains(x -> x.getName().contains("Teleport crystal"))) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            Bank.withdraw(x -> x.getName().contains("Teleport crystal ("));
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(x -> x.getName().contains("Teleport crystal") && !x.getName().contains("(")) && PRIFDDINAS.contains(Players.getLocal())) {
            if (!ELUNED.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(ELUNED);
                return ReactionGenerator.getNormal();
            }

            Logger.info("enchant seeds");
            NPC eluned = NPCs.closest("Eluned");
            if (eluned != null && eluned.interact("Enchant")) {
                Antiban.sleepUntil(() -> Inventory.contains(x -> x.getName().contains("Teleport crystal (")), 6400);
            }
            return ReactionGenerator.getNormal();
        }

        if (!AMROD.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(AMROD);

            if (ItemProcessing.isOpen()) {
                ItemProcessing.makeAll(ItemID.ENHANCED_CRYSTAL_TELEPORT_SEED);
                return ReactionGenerator.getNormal();
            }

            NPC amrod = NPCs.closest("Amrod");
            if (amrod != null && amrod.interact("Trade")) {
                Antiban.sleepUntil(ItemProcessing::isOpen, 4400);
            }
        }
        return ReactionGenerator.getNormal();
    }

}

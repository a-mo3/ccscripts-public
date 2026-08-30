package org.dreambot.behaviour.method.mixology;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.mixology.data.MixologyHerbs;
import org.dreambot.behaviour.method.mixology.data.PotionReagent;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.InventoryLoadoutItem;
import org.dreambot.fractals.loadout.LoadoutItem;
import org.dreambot.fractals.loadout.events.BuyItemsEvent;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * when you have a low amount of any paste, fill your inventory with enough paste to max each slot
 * then deposit
 */
public class GetMoreReagents extends Fractal {
    public GetMoreReagents(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    final Tile PROCESSOR = new Tile(1398, 9312, 0);

    @Override
    public int onLoop() {
        // make sure bank is cached
        if (!Bank.isCached()) {
            Bank.open();
            if (Bank.isOpen()) Bank.updateCache();
            return ReactionGenerator.getNormal();
        }

        // get the best gp/paste jaunts
        // check for 500 here and 0 in accept so we restock on low ones
        PotionReagent[] requiredReagents = Arrays.stream(PotionReagent.values())
                .filter(x -> x.getReagentCount() < 500)
                .filter(x -> MixologyHerbs.getOwnedPotential(x) < 500)
                .toArray(PotionReagent[]::new);

        if (requiredReagents.length > 0) {
            log("Buying more supplies to create reagent " + Arrays.toString(requiredReagents));
            InventoryLoadout loadout = new InventoryLoadout();
            List<LoadoutItem> buyables = new ArrayList<>();
            for (PotionReagent c : requiredReagents) {
                // todo consider different quant to max out the paste count
                // todo consider buying more than the max depositable paste so you can stay there longer
//                loadout.addItem(MixologyHerbs.getCheapest(c, false, false), 26).setRefill(500);
                buyables.add(new InventoryLoadoutItem(MixologyHerbs.getCheapest(c, false, false), 26).setRefill(500));
            }
            BuyItemsEvent.Response res = new BuyItemsEvent(buyables).executed();
            log("buying reagents " + res);
            if (res == BuyItemsEvent.Response.NO_GP) {
                log("Get gp for more herbs");

                new MuleRequestEvent("cCMixologyFarm")
                        .addRequiredItem(ItemID.COINS_995, (int) (loadout.getMissingItems().stream().mapToInt(x -> LivePrices.get(x.getItemId())).sum() * 1.1))
                        .execute();
            }
//            log("Getting new reagents - " + new WithdrawLoadoutEvent(loadout, null).executed());
            return ReactionGenerator.getNormal();
        }

        // go to processor
        if (PROCESSOR.distance() > 5) {
            log("Go to the processor");
            // todo consider the teleport situation here. need coins to take ship
            if (Inventory.count(ItemID.COINS_995) < 5000 && Players.getLocal().getY() < 5000) {
                log("Getting coins for boat - " + new WithdrawLoadoutEvent(new InventoryLoadout()
                        .addItem(ItemID.COINS_995, 10_000)
                        , null).executed());
            }
            if (Walking.shouldWalk()) Walking.walk(PROCESSOR);
            return ReactionGenerator.getNormal();
        }

        // Fill inventory with whatever we've got to convert to paste
        PotionReagent[] ownedPrecursors = Arrays.stream(PotionReagent.values())
                .filter(x -> x.pasteCountInInv() < 2500)
                .filter(x -> x.getReagentCount() < 2000)
                .filter(x -> MixologyHerbs.getOwnedPotential(x) > 5)
                .toArray(PotionReagent[]::new);
        log(Arrays.toString(ownedPrecursors));

        int owned = ownedPrecursors.length == 0 ? -1 : MixologyHerbs.getOwnedIdForType(ownedPrecursors[0]);
        // once you are have processed all your herbs it will be -1, deposit and then the reagent will be full and removed from arr
        if (owned > 0) {
            log("Processing " + owned);
            if (!Inventory.contains(owned)) {
                log("Take from bank");

                if (Bank.open()) {
                    Bank.depositAll(x -> !x.getName().contains("paste") && x.getId() != owned);
                    Bank.setWithdrawMode(BankMode.ITEM);
                    Bank.withdrawAll(ItemID.AGA_PASTE);
                    Bank.withdrawAll(ItemID.MOX_PASTE);
                    Bank.withdrawAll(ItemID.LYE_PASTE);
                    Bank.withdrawAll(owned);
                }
                return ReactionGenerator.getNormal();
            }

            if (Widgets.isOpen()) Widgets.closeAll();

            GameObject refiner = GameObjects.closest("Refiner");
            if (refiner == null) {
                log("Failed to find Refiner");
                return ReactionGenerator.getNormal();
            }
            refiner.interact();
            return Calculations.random(400, 700);
        }

        if (Inventory.contains(Item::isNoted)){
            log("Bank noted items");
            new BankAllInventoryEvent(Item::isNoted).execute();
            return ReactionGenerator.getNormal() + 3000;
        }
        // todo possibly withdraw all of the 3 pastes in bank
        // deposit
        log("Deposit");
        ObjectUtil.interact("Hopper");
        return ReactionGenerator.getNormal() + 3000;
    }
}

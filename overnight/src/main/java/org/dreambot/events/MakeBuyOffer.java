package org.dreambot.events;

import org.dreambot.OwnedItems;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.muling.Log;

import java.util.Arrays;

public class MakeBuyOffer extends AbstractResponseEvent<MakeBuyOffer.Response> {
    final int itemId;
    final int quantity;
    final int buyPrice;
    final int cost;

    public MakeBuyOffer(int itemId, int quantity, int buyPrice) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        cost = buyPrice * quantity;
    }

    enum Response {
        NOT_ENOUGH_GP,
        GE_FULL,
        ALREADY_MADE, // offer with this item was already made
    }

    @Override
    public int onLoop() {
        if (Bank.getLastBankHistoryCacheTime() <= 1) {
            if (Bank.isOpen()) Bank.updateCache();
            if (Walking.shouldWalk()) Bank.open();
            return sleep();
        }

        if (cost > OwnedItems.count(ItemID.COINS_995)) {
            setResponse(Response.NOT_ENOUGH_GP);
            return sleep();
        }

        if (Bank.isOpen()) {
            Log.info("Make Buy Offer - closing bank");
            Bank.close();
            return sleep();
        }

        if (!GrandExchange.isOpen()) {
            Log.info("Make Buy Offer - open GE");
            if (Walking.shouldWalk()) GrandExchange.open();
            Sleep.sleepUntil(GrandExchange::isOpen, 2400);
            return sleep();
        }

        if (GrandExchange.isReadyToCollect()) {
            GrandExchange.collect();
            Sleep.sleepUntil(() -> !GrandExchange.isReadyToCollect(), 2400);
            return sleep();
        }

        boolean alreadyMade = Arrays.stream(GrandExchange.getItems()).anyMatch(x -> x.getID() == itemId);
        if (alreadyMade) {
            setResponse(Response.ALREADY_MADE);
            return sleep();
        }

        Log.info("Make Buy Offer - Make buy offer");
        GrandExchange.buyItem(itemId, quantity, buyPrice);
        return sleep();
    }
}

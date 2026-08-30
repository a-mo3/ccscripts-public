package org.dreambot.events;

import org.dreambot.OwnedItems;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.muling.Log;

import java.util.Arrays;

public class MakeSellOffer extends AbstractResponseEvent<MakeSellOffer.Response> {
    final int itemId;
    final int quantity;
    final int sellPrice;

    public MakeSellOffer(int itemId, int quantity, int sellPrice) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.sellPrice = sellPrice;
    }

    enum Response {
        NO_ITEMS_OWNED,
        ALREADY_MADE,
        GE_FULL,
    }

    @Override
    public int onLoop() {
        if (Bank.getLastBankHistoryCacheTime() <= 1) {
            if (Bank.isOpen()) Bank.updateCache();
            if (Walking.shouldWalk()) Bank.open();
            return sleep();
        }

        if (!OwnedItems.contains(itemId) && !OwnedItems.contains(getNotedId(itemId))) {
            setResponse(Response.NO_ITEMS_OWNED);
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
        GrandExchange.sellItem(new Item(itemId, 0).getName(), quantity, sellPrice);
        return sleep();
    }

    private int getNotedId(int id) {
        return new Item(id, 0).getNotedItemID();
    }
}

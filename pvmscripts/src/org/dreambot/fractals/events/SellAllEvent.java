package org.dreambot.fractals.events;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.GrandExchangeItem;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.misc.MuleOffItem;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SellAllEvent extends AbstractEvent {

    public SellAllEvent(int... items) {
        this.sellItems = MuleOffItem.makeMuleItems(items);
    }

    public SellAllEvent(List<MuleOffItem> items) {
        this.sellItems = items;
    }

    List<MuleOffItem> sellItems;
    Timer timeout = new Timer(60 * 1000 * 3);

    @Override
    public int onLoop() {
        if (timeout.finished()) {
            Logger.info("3 minute sell all timeout");
            GrandExchange.close();
            setFailed(true);
            return ReactionGenerator.getQuick();
        }

        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (Bank.isOpen()) {
                Bank.close();
                return ReactionGenerator.getNormal();
            }
            Logger.info("Sell all event getting bank cache");
            if (Walking.shouldWalk()) BankUtil.openClosest();
            return ReactionGenerator.getNormal();
        }

        List<Item> sellItemsInBank = Bank.all(x -> sellItems.stream().anyMatch(i -> x.getId() == i.getItemID()));
        sellItems = sellItems.stream()
                .filter(MuleOffItem::shouldSell)
                .collect(Collectors.toList())
        ;
        Item sellItem = Inventory.get(x -> sellItems.stream().anyMatch(i -> i.getItemID() == x.getUnnotedItemId()));
        MuleOffItem muleOffItem = null;
        if (sellItem != null) {
            Logger.info("Get mule off item");
            muleOffItem = sellItems.stream()
                    .filter(x -> x.getItemID() == sellItem.getUnnotedItemId())
                    .findFirst()
                    .orElse(null);
            Logger.info("" + muleOffItem);
        }

        if (GrandExchange.isReadyToCollect() && GrandExchange.isOpen()) {
            if (Inventory.isFull() && sellItem == null) new BankAllInventoryEvent().execute();
            GrandExchange.collect();
        }

        if (GrandExchange.isOpen()) {
            if (GrandExchange.getFirstOpenSlot() < 0) {
                Logger.info("GE is full");
                GrandExchange.cancelAll();
                return ReactionGenerator.getNormal();
            }
            // close if you have no more items to sell and ge is empty or you have more items to withdraw
            // sell items
            if (sellItem == null) {
                Logger.info("No sellable item in inventory");

                if (sellItemsInBank.isEmpty()) {
                    if (Arrays.stream(GrandExchange.getItems()).filter(Objects::nonNull).noneMatch(GrandExchangeItem::isSellOffer)) {
                        Logger.info("Selling all is complete");
                        setComplete(true);
                        return ReactionGenerator.getNormal();
                    }
                    Logger.info("No more items to withdraw, waiting for offers to complete");
//                    if (timeout.elapsed() > (1000 * 60)) setComplete(true);
                    return ReactionGenerator.getNormal();
                }
                // close ge to go get more
                if (Inventory.isFull()) new BankAllInventoryEvent().execute();
                GrandExchange.close();
                return ReactionGenerator.getNormal();
            }

            Logger.info("Sell off mule off item " + muleOffItem);
            GrandExchange.sellItem(sellItem.getId(),
                    Inventory.count(sellItem.getName()) - (muleOffItem == null ? 0 : muleOffItem.getRemainingCount()),
                    (int) (sellItem.getLivePrice() * 0.5));
            return ReactionGenerator.getNormal();
        }

        if (!Inventory.isFull()) {
            // deposit everything that isnt bankable
            if (!sellItemsInBank.isEmpty()) {
                Logger.info("withdraw");
                if (!Bank.isOpen()) {
                    if (Walking.shouldWalk()) Bank.open();
                    return ReactionGenerator.getNormal();
                }

                // deposit non sellable items
                if (Inventory.contains(x -> x.getId() != ItemID.COINS_995
                        && sellItems.stream().noneMatch(i -> i.getItemID() == x.getUnnotedItemId())
                )) {
                    Logger.info("Deposit all non sellable items");
                    Bank.depositAllItems();
                    return ReactionGenerator.getNormal();
                }

                if (Bank.getWithdrawMode() != BankMode.NOTE) Bank.setWithdrawMode(BankMode.NOTE);
                Bank.withdrawAll(x -> sellItems.stream().anyMatch(i -> i.getItemID() == x.getId()));
                return ReactionGenerator.getQuick();
            }
        }

        if (BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) > 15) {
            Logger.info("Walk to GE");
            if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getNormal();
        }
        Logger.info("Open GE");
        if (!GrandExchange.isOpen()) GrandExchange.open();
        return ReactionGenerator.getNormal();
    }
}
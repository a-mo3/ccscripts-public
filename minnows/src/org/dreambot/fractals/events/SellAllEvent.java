package org.dreambot.fractals.events;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SellAllEvent extends AbstractEvent {

    public SellAllEvent(int... items) {
        this.items = items;
    }

    final int[] items;
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
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getNormal();
        }

        Set<Integer> sellItems = Arrays.stream(items).boxed().collect(Collectors.toSet());
        List<Item> sellItemsInBank = Bank.all(x -> sellItems.contains(x.getID()));
        Item sellItem = Inventory.get(x -> sellItems.contains(x.getUnnotedItemID()));
        if (GrandExchange.isReadyToCollect() && GrandExchange.isOpen()) {
            if (Inventory.isFull() && sellItem == null) new BankAllInventoryEvent().execute();
            GrandExchange.collect();
        }

        if (GrandExchange.isOpen()) {
            if (GrandExchange.getFirstOpenSlot() < 0) {
                Logger.info("GE is full");
                return ReactionGenerator.getNormal();
            }
            // close if you have no more items to sell and ge is empty or you have more items to withdraw
            // sell items
            if (sellItem == null) {
                Logger.info("No sellable item in inventory");

                if (sellItemsInBank.isEmpty()) {
                    if (GrandExchange.getFirstOpenSlot() <= 0) {
                        Logger.info("Selling all is complete");
                        setComplete(true);
                        return ReactionGenerator.getNormal();
                    }
                    Logger.info("No more items to withdraw, waiting for offers to complete");
                    if (timeout.elapsed() > (1000 * 60)) setComplete(true);
                    return ReactionGenerator.getNormal();
                }
                // close ge to go get more
                if (Inventory.isFull()) new BankAllInventoryEvent().execute();
                GrandExchange.close();
                return ReactionGenerator.getNormal();
            }

            GrandExchange.sellItem(sellItem.getID(), Inventory.count(sellItem.getName()), (int) (sellItem.getLivePrice() * 0.5));
            return ReactionGenerator.getNormal();
        }

        if (!Inventory.isFull()) {
            // deposit everything that isnt bankable
            if (!sellItemsInBank.isEmpty()) {
                if (!Bank.isOpen()) {
                    if (Walking.shouldWalk()) Bank.open();
                    return ReactionGenerator.getNormal();
                }

                // deposit non sellable items
                if (Inventory.contains(x -> x.getID() != ItemID.COINS_995 && !sellItems.contains(x.getUnnotedItemID()))) {
                    Logger.info("Deposit all non sellable items");
                    Bank.depositAllItems();
                    return ReactionGenerator.getNormal();
                }

                if (Bank.getWithdrawMode() != BankMode.NOTE) Bank.setWithdrawMode(BankMode.NOTE);
                Bank.withdrawAll(x -> sellItems.contains(x.getID()));
                return ReactionGenerator.getQuick();
            }

        }

        if (!GrandExchange.isOpen()) GrandExchange.open();
        return ReactionGenerator.getNormal();
    }
}

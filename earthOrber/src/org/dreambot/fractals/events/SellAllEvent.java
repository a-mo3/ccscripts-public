package org.dreambot.fractals.events;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

public class SellAllEvent extends AbstractEvent {

    public SellAllEvent(int... items) {
        this.items = items;
    }

    final int[] items;
    Timer timeout = new Timer(60 * 1000 * 6);

    @Override
    public int onLoop() {
        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (Bank.isOpen()) {
                Bank.close();
                return ReactionGenerator.getNormal();
            }
            Logger.info("Sell all event getting bank cache");
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getNormal();
        }

        if (GrandExchange.isReadyToCollect() && GrandExchange.isOpen()) {
            if (Inventory.isFull()) new BankAllInventoryEvent().execute();
            GrandExchange.collect();
            return ReactionGenerator.getNormal();
        }

        for (Integer id : items) {
            if (OwnedItems.contains(id) || OwnedItems.contains(id + 1)) {
                String name = new Item(id, 0).getName();
                Logger.format("Selling item: %s", name);
                if (Bank.contains(id) || Bank.getLastBankHistoryCacheTime() < 1) {
                    if (!Bank.isOpen() && Walking.shouldWalk()) Bank.open();

                    if (Inventory.isFull()) {
                        Bank.depositAllItems();
                        return ReactionGenerator.getNormal();
                    }

                    Bank.setWithdrawMode(BankMode.NOTE);

                    Bank.withdrawAll(id);
                    return ReactionGenerator.getNormal();
                }

                if (Bank.isOpen()) {
                    Bank.close();
                    return ReactionGenerator.getNormal();
                }

                if (!GrandExchange.isOpen()) {
                    if (!BankLocation.GRAND_EXCHANGE.getArea(15).contains(Players.getLocal())) {
                        Logger.info("Walking to ge");
                        if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
                        return ReactionGenerator.getNormal();
                    }

                    GrandExchange.open();
                    return ReactionGenerator.getNormal();
                }


                GrandExchange.sellItem(new Item(id, 0).getName(), Inventory.count(name), (int) (LivePrices.get(id) * 0.8));
                Sleep.sleepUntil(GrandExchange::isReadyToCollect, 2400);
                return ReactionGenerator.getNormal();
            }
        }

        setComplete(true);
        return ReactionGenerator.getNormal();
    }
}

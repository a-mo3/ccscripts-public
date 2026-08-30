package org.dreambot.behaviour.bankdump;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.List;
import java.util.function.Supplier;

public class DumpBank extends Fractal {
    final int minPrice;
    final Filter<Item> sellableFilter;

    public DumpBank(Supplier<Boolean> acceptCondition, int minPrice) {
        super(acceptCondition);
        setSimpleName("Dump bank");
        this.minPrice = Math.max(5, minPrice);
        this.equipmentLoadout = new EquipmentLoadout().setStrict(true);
        this.sellableFilter = x -> x.isTradable() && x.getLivePrice() * (x.isStackable() ? x.getAmount() : 1) > minPrice;
    }

    @Override
    public int onLoop() {
        if (!Bank.isCached()) {
            log("Cache bank");
            if (Walking.shouldWalk()) Bank.open();
            Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (Bank.isOpen() && Bank.placeHoldersEnabled()) {
            Bank.togglePlaceholders(false);
            return ReactionGenerator.getNormal();
        }

        Item nonSellable = Inventory.get(x -> !sellableFilter.match(x));
        if (nonSellable != null && !Bank.isFull()) {
            log("Bank Non sell item " + nonSellable.getId() + " " + nonSellable.getName());
            if (!Bank.isOpen()) {
                log("Open bank");
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            log("Deposit non sellable");
            Bank.depositAll(nonSellable);
            return ReactionGenerator.getNormal();
        }

        if (GrandExchange.isOpen() && GrandExchange.isReadyToCollect()) {
            log("Collect");
            GrandExchange.collectToBank();
            return ReactionGenerator.getNormal();
        }

        if (!Bank.contains(sellableFilter) && !Inventory.contains(sellableFilter)) {
            if (OwnedItems.count(ItemID.COINS_995) == 0) {
                log("No coins no sellables, stop rescript");
                return -1;
            }

            log("Out of tradable items, mule off everything");
            new MuleRequestEvent("bankDump")
                    .addOfferedItem(ItemID.COINS_995, OwnedItems.count(ItemID.COINS_995))
                    .execute();
            Bank.resetCache();
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(sellableFilter)) {
            log("Sell inventory on ge");
            if (!GrandExchange.isOpen()) {
                GrandExchange.open();
                return ReactionGenerator.getNormal();
            }

            if (GrandExchange.getFirstOpenSlot() < 0) {
                // todo maybe do some thing about that
                log("No slot empty");
                return ReactionGenerator.getNormal();
            }

            Item first = Inventory.get(sellableFilter);
            log("Sell item " + (first == null ? " - " : first.getName()));
            if (first != null) GrandExchange.sellItem(first.getId(), Inventory.count(first.getId()), 1);
            return ReactionGenerator.getNormal();
        }

        if (!Bank.isOpen()) {
            log("Open bank");
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getNormal();
        }

        Bank.setWithdrawMode(BankMode.NOTE);
        List<Item> bankedSellables = Bank.all(sellableFilter);
        for (Item i : bankedSellables) {
            if (Inventory.isFull()) return ReactionGenerator.getNormal();
            log("Withdraw all " + i.getId());
            Bank.withdrawAll(i.getId());
        }
        return ReactionGenerator.getNormal();
    }
}

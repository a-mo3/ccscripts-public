package org.dreambot.fractals.events;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.BankUtil;
import org.dreambot.settings.timing.ReactionGenerator;

public class BankAllInventoryEvent extends AbstractEvent implements ChatListener {
    public BankAllInventoryEvent() {
        Client.getInstance().addEventListener(this);
        this.itemFilter = null;
    }

    Timer timeout = new Timer(60_000 * 10);

    BankLocation bankLocation = null;
    final Filter<Item> itemFilter;

    public BankAllInventoryEvent(Filter<Item> itemFilter) {
        Client.getInstance().addEventListener(this);
        this.itemFilter = itemFilter;
    }

    public BankAllInventoryEvent(BankLocation bankLocation) {
        Client.getInstance().addEventListener(this);
        this.bankLocation = bankLocation;
        this.itemFilter = null;
    }

    @Override
    public int onLoop() {
        if (timeout.finished()) {
            Logger.info("Bank all event timed out");
            setComplete(true);
            return ReactionGenerator.getQuick();
        }

        if (itemFilter != null && !Inventory.contains(itemFilter)) {
            Logger.info("No items matching filter, complete");
            setComplete(true);
            return ReactionGenerator.getQuick();
        }

        if (Inventory.isEmpty()) {
            Logger.info("Empty inv");
            setComplete(true);
            return ReactionGenerator.getQuick();
        }

        if (!Bank.isOpen()) {
            Logger.info("Bank open");
            if (bankLocation != null) {
                if (Walking.shouldWalk()) Bank.open(bankLocation);
                return ReactionGenerator.getNormal();
            }
            if (Walking.shouldWalk()) BankUtil.openClosest();
            return ReactionGenerator.getQuick();
        }

        if (itemFilter != null) {
            Logger.info("Deposit filter style");
            Bank.depositAll(itemFilter);
            return ReactionGenerator.getQuick();
        }
        Logger.info("Deposit all items");
        Bank.depositAllItems();
        return ReactionGenerator.getQuick();
    }

    @Override
    public void onExit() {
        Client.getInstance().removeEventListener(this);
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message.getMessage().contains("Your items cannot be stored in the bank")) {
            setComplete(true);
        }
    }
}

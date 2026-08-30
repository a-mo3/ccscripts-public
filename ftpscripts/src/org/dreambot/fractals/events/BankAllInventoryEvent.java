package org.dreambot.fractals.events;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.BankUtil;
import org.dreambot.settings.timing.ReactionGenerator;

public class BankAllInventoryEvent extends AbstractEvent implements ChatListener {
    public BankAllInventoryEvent() {
        Client.getInstance().addEventListener(this);
    }

    BankLocation bankLocation = null;

    public BankAllInventoryEvent(BankLocation bankLocation) {
        Client.getInstance().addEventListener(this);
        this.bankLocation = bankLocation;
    }

    @Override
    public int onLoop() {
        if (Inventory.isEmpty()) {
            setComplete(true);
            return ReactionGenerator.getQuick();
        }

        if (!Bank.isOpen()) {
            if (bankLocation != null) {
                if (Walking.shouldWalk()) Bank.open(bankLocation);
                return ReactionGenerator.getNormal();
            }
            if (Walking.shouldWalk()) BankUtil.openClosest();
            return ReactionGenerator.getQuick();
        }

        Bank.depositAllItems();
        return ReactionGenerator.getQuick();
    }

    @Override
    public void onExit() {
        Client.getInstance().removeEventListener(this);
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().contains("Your items cannot be stored in the bank")) {
            setComplete(true);
        }
    }
}

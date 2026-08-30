package org.dreambot.fractals.events;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.settings.timing.ReactionGenerator;

public class BankAllInventoryEvent extends AbstractEvent implements ChatListener {
    public BankAllInventoryEvent() {
        Client.getInstance().addEventListener(this);
    }

    @Override
    public int onLoop() {
        if (Inventory.isEmpty()) {
            setComplete(true);
            return ReactionGenerator.getQuick();
        }

        if (!Bank.isOpen()) {
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getQuick();
        }

        Bank.depositAllItems();
        return ReactionGenerator.getQuick();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().contains("cannot be stored in the bank")) {
            Logger.info("Cant be stored in bank message - cancel event");
            // will this cause a thread issue? we will find out in production #YOLOSWAG
            setComplete(true);
        }
    }

    @Override
    public void onExit() {
        Client.getInstance().removeEventListener(this);
    }
}

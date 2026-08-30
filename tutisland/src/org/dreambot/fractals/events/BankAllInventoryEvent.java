package org.dreambot.fractals.events;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.settings.timing.ReactionGenerator;

public class BankAllInventoryEvent extends AbstractEvent {
    @Override
    public int onLoop() {
        if (Inventory.isEmpty()) {
            setComplete(true);
            return ReactionGenerator.getNormal();
        }

        if (!Bank.isOpen()) {
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getNormal();
        }

        Bank.depositAllItems();
        return ReactionGenerator.getNormal();
    }
}

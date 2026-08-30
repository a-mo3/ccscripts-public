package org.dreambot.fractals.events;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.settings.timing.ReactionGenerator;

public class BankAllEquipmentEvent extends AbstractEvent {
    @Override
    public int onLoop() {
        if (Equipment.isEmpty()) {
            setComplete(true);
            return ReactionGenerator.getQuick();
        }

        if (!Bank.isOpen()) {
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getQuick();
        }

        Bank.depositAllEquipment();
        return ReactionGenerator.getQuick();
    }
}

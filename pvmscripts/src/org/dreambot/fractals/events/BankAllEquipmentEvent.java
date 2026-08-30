package org.dreambot.fractals.events;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.BankUtil;
import org.dreambot.settings.timing.ReactionGenerator;

public class BankAllEquipmentEvent extends AbstractEvent {
    @Override
    public int onLoop() {
        if (Equipment.isEmpty()) {
            setComplete(true);
            return ReactionGenerator.getQuick();
        }

        if (!Bank.isOpen()) {
            Logger.info("Open bank dei " + Walking.shouldWalk());
            if (Widgets.isOpen()) Widgets.closeAll();

            WidgetChild puzzleClose = Widgets.get(480, x -> x.hasAction("Close"));
            if (puzzleClose != null) {
                Logger.info("Puzzle close");
                puzzleClose.interact();
//                return ReactionGenerator.getNormal();
            }

//            if (Walking.shouldWalk()) {
//                boolean b = Bank.open();
//                if (!b) {
//                    Logger.info("Failed to open bank safeing to GE");
//                    Bank.open(BankLocation.GRAND_EXCHANGE);
//                }
//            }
            if (Walking.shouldWalk()) BankUtil.openClosest();
            return ReactionGenerator.getQuick();
        }

        Bank.depositAllEquipment();
        return ReactionGenerator.getQuick();
    }
}

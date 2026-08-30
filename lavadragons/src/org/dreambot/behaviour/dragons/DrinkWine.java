package org.dreambot.behaviour.dragons;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class DrinkWine extends Fractal {
    public DrinkWine(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        if (Bank.isOpen() || GrandExchange.isOpen()) {
            log("Close bank/ge");
            Widgets.closeAll();
        }

        Inventory.interact(ItemID.JUG_OF_WINE, "Drink");
        return ReactionGenerator.getNormal();
    }
}

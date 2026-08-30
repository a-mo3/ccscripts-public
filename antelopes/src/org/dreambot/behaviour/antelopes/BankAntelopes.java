package org.dreambot.behaviour.antelopes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

public class BankAntelopes extends Fractal {
    private final Area HUNTER_BANK = new Tile(1544, 3041).getArea(4);

    public BankAntelopes() {
    }

    @Override
    public boolean isValid() {
        return (Inventory.getEmptySlots() + Inventory.count(ItemID.MOONLIGHT_ANTELOPE_FUR) + Inventory.count(ItemID.BIG_BONES)) < 3;
    }

    @Override
    public int onLoop() {
//        // deposit moonlight moths
//        if (!HUNTER_BANK.contains(Players.getLocal())) {
//            Logger.info("Going to hunter bank");
//            if (Walking.shouldWalk()) Walking.walk(HUNTER_BANK);
//            return ReactionGenerator.getNormal();
//        }

        if (!Bank.isOpen()) {
            if (Walking.shouldWalk()) Bank.open(BankLocation.HUNTER_GUILD);
            return ReactionGenerator.getNormal();
        }

        Logger.info("deposit all");
        Bank.depositAllExcept(x -> x.getID() == ItemID.TEASING_STICK || x.getID() == ItemID.KNIFE);
        return ReactionGenerator.getNormal();
    }
}

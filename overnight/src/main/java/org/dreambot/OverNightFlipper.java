package org.dreambot;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCOvernightFlipper", author = "camalCase", version = 0.0)
public class OverNightFlipper extends AbstractScript {
    @Override
    public void onStart(String... params) {
        init();
    }

    @Override
    public void onStart() {
        init();
    }

    private void init() {

    }

    @Override
    public int onLoop() {
        // sleep for alloted time

        // make sure you're at the ge
        if (!BankLocation.GRAND_EXCHANGE.getArea(10).contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
            return sleep();
        }

        // set all sell offers

        // set all buy offers

        // log off and sleep
        return sleep();
    }

    private int sleep() {
        return Calculations.random(600, 2000);
    }
}

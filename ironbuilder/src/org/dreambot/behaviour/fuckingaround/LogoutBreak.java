package org.dreambot.behaviour.fuckingaround;

import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.IronFractal;

import java.util.function.BooleanSupplier;

public class LogoutBreak extends IronFractal {
    public LogoutBreak(BooleanSupplier acceptCondition) {
        super(acceptCondition);
        setSimpleName("Logout");
    }

    @Override
    protected int onLoop() {
        Client.getInstance().getRandomManager().disableSolver(RandomEvent.LOGIN);
        Tabs.logout();
        int i = Calculations.random(200_000, 800_000);
        log("Logging out for " + i);
        Sleep.sleep(i);
        // todo this will fuck up analytics timing
        Client.getInstance().getRandomManager().enableSolver(RandomEvent.LOGIN);
        return sleep();
    }
}

package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import task.AbstractTask;
import util.EatUtil;

public class BankNode extends AbstractTask {
    @Override
    public boolean accept() {
        return config.isShouldBank() && config.getActivityTile() != null;
    }

    @Override
    public int priority() {
        return 999;
    }

    @Override
    public int execute() {
        if (!config.isReturning()) {
            if (Bank.open()) {
                if (Bank.depositAllItems()) {
                    if (config.isEatFood() && EatUtil.bestFoodInBank() != -1) {
                        Bank.withdraw(EatUtil.bestFoodInBank(), config.getFoodAmount());
                    } else {
                        Logger.log("no food found in bank or eating is disabled.");
                    }
                    Sleep.sleep(Calculations.random(700, 1300));
                    if (config.isUseNecklace()) {
                        if (Bank.contains("Dodgy necklace")) {
                            Bank.withdraw("Dodgy necklace", config.getNecklaceAmount());
                        } else {
                            Logger.log("no necklaces found - turning off necklaces");
                            config.setUseNecklace(false);
                        }
                    }
                    config.setReturning(true);
                }
            }
        } else {
            if (config.getActivityTile().equals(Players.getLocal().getTile())) {
                config.setReturning(false);
                config.setShouldBank(false);
                return 20;
            }
            // walk back to where you were before banking
            if (Walking.shouldWalk() && (Walking.getDestination() == null || !config.getActivityTile().equals(Walking.getDestination()))) {
                Walking.walkExact(config.getActivityTile());
            }
        }
        return Calculations.random(100, 200);
    }
}

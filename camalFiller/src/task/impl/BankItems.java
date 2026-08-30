package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import task.AbstractTask;


/**
 * @author camalCase
 * @version 1
 * 25th aug refactor
 * desc: main, adds nodes & starts gui.
 */
public class BankItems extends AbstractTask {
    private final Area FALLY_BANK = new Area(2949, 3368, 2944, 3368); // added this because BankLocations.FALADOR_WEST was causing problems

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public boolean accept() {
        return config.isScriptRunning() && !Inventory.contains(config.getContainer());
    }

    @Override
    public int execute() {
        config.setStatus("Banking full " + config.getContainer());
        if (FALLY_BANK.contains(Players.getLocal())) {
            if (Bank.open()) {
                config.setAmountFilled(config.getAmountFilled() + Inventory.fullSlotCount()); // full slot count rather than just 28 for when you have less than 28 on the last run
                Bank.depositAllItems();
                Sleep.sleep(Calculations.random(100, 300));
                if (Bank.contains(config.getContainer())) {
                    Bank.withdrawAll(config.getContainer());
                    Sleep.sleep(Calculations.random(100, 300));
                    Bank.close();
                } else {
                    Logger.log("Seems you're out of " + config.getContainer() + "s stopping script...");
                    config.setScriptRunning(false);
                    config.setStatus("Of out empty " + config.getContainer());
                    manager.stop();
                }
            }
        } else { // if not @ bank, walk to bank
            if (Walking.shouldWalk()) {
                Walking.walk(FALLY_BANK.getRandomTile());
            }
        }
        Sleep.sleep(500);
        return 0;
    }
}

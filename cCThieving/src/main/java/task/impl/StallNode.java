package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import task.AbstractTask;

public class StallNode extends AbstractTask {
    @Override
    public boolean accept() {
        return config.isStallMode() && config.getStallTarget() != null;
    }

    @Override
    public int execute() {
        if (Inventory.isFull()) {
            if (config.isBankingMode()) {
                config.setActivityTile(Players.getLocal().getTile());
                config.setShouldBank(true);
                return 20;
            } else {
                Inventory.dropAllExcept(x -> x.getName().equals("Coins"));
            }
        }
        GameObject stall = GameObjects.closest(config.getStallTarget().NAME);
        if (stall != null) {
            if (stall.interact("Steal-from")) {
                Sleep.sleepUntil(() -> !stall.exists(), 4000);
            }
        }
        return Calculations.random(100, 300);
    }
}

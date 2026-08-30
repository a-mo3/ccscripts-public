package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import task.AbstractTask;

public class ChestNode extends AbstractTask {
    @Override
    public boolean accept() {
        return config.isChestMode();
    }

    @Override
    public int execute() {
        GameObject chest = GameObjects.closest("Chest");
        if (chest != null) {
            if (chest.interact("Search for traps")) {
                Sleep.sleep(config.getChestTimerRespawn());
            }
        }
        return Calculations.random(100, 300);
    }
}

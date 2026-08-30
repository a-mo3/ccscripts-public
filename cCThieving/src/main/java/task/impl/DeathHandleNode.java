package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import task.AbstractTask;

public class DeathHandleNode extends AbstractTask {
    @Override
    public boolean accept() {
        return config.isHandleDeath();
    }

    @Override
    public int priority() {
        return 1000;
    }

    @Override
    public int execute() {
        if (config.getActivityTile() != null) {
            if (config.getActivityTile().equals(Players.getLocal().getTile())) {
                config.setHandleDeath(false);
                return 20;
            }
            // walk back to where you were before banking
            if (Walking.shouldWalk() && (Walking.getDestination() == null || !config.getActivityTile().equals(Walking.getDestination()))) {
                Walking.walkExact(config.getActivityTile());
            }
        }
        return Calculations.random(70, 200);
    }
}

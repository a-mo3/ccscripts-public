package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import task.AbstractTask;

public class MineNode extends AbstractTask {
    @Override
    public boolean accept() {
        return config.isRunning() && config.shouldBank() && !Inventory.isFull()
                || config.isRunning() && !config.shouldBank();
    }

    @Override
    public int execute() {
        if (Inventory.contains(x -> x.getName().contains("pickaxe"))) {
//           Logger.log("contains");
            if (canEquip(Inventory.get(x -> x.getName().contains("pickaxe")))) {
//               Logger.log("can equip");
                Inventory.get(x -> x.getName().contains("pickaxe")).interact();
            }
        }

        config.setStatus("Mining...");
        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > Calculations.random(13, 30)) {
            Walking.toggleRun();
        }
        if (config.getMineLocation().LOCATION.contains(Players.getLocal())) {
            if (Inventory.isFull()) {
                if (!config.shouldBank()) {
                    Inventory.dropAllExcept(pickaxe -> pickaxe.getName().contains("pickaxe"));
                }
            }
            GameObject rock = config.getRockType().getRockWithOres(config.getRockType());
            if (rock != null) {
                rock.interact("Mine");
                Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
                Sleep.sleepUntil(() -> !Players.getLocal().isAnimating(), 45000);
            }
        } else {
            if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 13) {
                Walking.toggleRun();
            }
            if (Walking.shouldWalk()) {
                Walking.walk(config.getMineLocation().LOCATION.getNearestTile(Players.getLocal()));
            }
        }
        return config.getSleep();
    }
}

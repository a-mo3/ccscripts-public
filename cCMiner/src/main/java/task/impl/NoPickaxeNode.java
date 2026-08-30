package task.impl;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import task.AbstractTask;

public class NoPickaxeNode extends AbstractTask {
    @Override
    public boolean accept() {

        return !Equipment.contains(item -> item.getName().contains("pickaxe"))
                && !Inventory.contains(i -> i.getName().contains("pickaxe"));
    }

    @Override
    public int priority() {
        return 999;
    }

    @Override
    public int execute() {
        config.setStatus("NO PICKAXE!!!!");
        return 300;
    }
}

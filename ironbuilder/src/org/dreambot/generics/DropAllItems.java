package org.dreambot.generics;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.IronFractal;

import java.util.Arrays;
import java.util.function.BooleanSupplier;

public class DropAllItems extends IronFractal {
    final Filter<Item> dropItem;

    public DropAllItems(BooleanSupplier acceptCondition, Filter<Item> exceptThese) {
        super(acceptCondition);
        this.dropItem = exceptThese;
        setSimpleName("Drop all");
    }

    public DropAllItems(BooleanSupplier acceptCondition, int... dropIds) {
        super(acceptCondition);
        this.dropItem = x -> Arrays.stream(dropIds).anyMatch(i -> i == x.getId());
        setSimpleName("Drop all");
    }

    public DropAllItems(BooleanSupplier acceptCondition) {
        super(acceptCondition);
        this.dropItem = null;
        setSimpleName("Drop all");
    }

    @Override
    protected int onLoop() {
        Widgets.closeAll();
        // todo some guard to not like drop 1 quintillion coins or something

        if (dropItem == null) {
            log("Drop all inventory");
            Inventory.dropAll();
            return sleep();
        }

        log("Bank all except");
        Inventory.dropAll(dropItem);
        return sleep();
    }
}

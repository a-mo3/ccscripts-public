package org.dreambot.generics;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.IronFractal;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class GenericInventoryCombination extends IronFractal {
    final Supplier<Item> itemASupplier;
    final Supplier<Item> itemBSupplier;

    public GenericInventoryCombination(BooleanSupplier acceptCondition, String itemAName, String itemBName) {
        super(acceptCondition);
        setSimpleName("Combination");
        this.itemASupplier = () -> Inventory.get(itemAName);
        this.itemBSupplier = () -> Inventory.get(itemBName);
    }

    @Override
    protected int onLoop() {
        Item a = itemASupplier.get();
        Item b = itemBSupplier.get();
        log("Item a " + a);
        log("Item a " + b);
        if (a != null && b != null) Inventory.combine(a, b);
        return sleep();
    }
}

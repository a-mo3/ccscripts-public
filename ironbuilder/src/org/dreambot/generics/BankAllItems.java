package org.dreambot.generics;

import lombok.extern.slf4j.Slf4j;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.IronFractal;

import java.util.Arrays;
import java.util.function.BooleanSupplier;

@Slf4j
public class BankAllItems extends IronFractal {
    final Filter<Item> exceptThese;

    public BankAllItems(BooleanSupplier acceptCondition, Filter<Item> exceptThese) {
        super(acceptCondition);
        this.exceptThese = exceptThese;
        setSimpleName("Bank");
    }

    public BankAllItems(BooleanSupplier acceptCondition) {
        super(acceptCondition);
        this.exceptThese = null;
        setSimpleName("Bank");
    }

    static int inventoryTolerance = Calculations.random(2, 11);
    public BankAllItems(int... allowedIds) {
        super(() -> Inventory.count(x -> Arrays.stream(allowedIds).noneMatch(i -> i == x.getId())) > inventoryTolerance);
        exceptThese = null;
        setSimpleName("allow list bank");
    }

    @Override
    protected int onLoop() {
        if (!Bank.open()) {
            log("Open bank");
            return sleep();
        }

        if (exceptThese == null) {
            log("Bank all");
            Bank.depositAllItems();
            return sleep();
        }
        log("Bank all except");
        Bank.depositAllExcept(exceptThese);
        return sleep();
    }
}

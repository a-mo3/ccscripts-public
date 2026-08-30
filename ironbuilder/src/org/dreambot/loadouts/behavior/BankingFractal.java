package org.dreambot.loadouts.behavior;

import lombok.Setter;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.IronFractal;
import org.dreambot.loadouts.InventoryLoadout;
import org.dreambot.loadouts.InventoryLoadoutItem;
import org.dreambot.utility.OwnedItems;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Banking fractal will exist on the top level of all trees
 * it should enforce setting up an inventory and equipment loadout
 * <p>
 * when there are items we dont own we add their restock method to the restock stack
 * the restock stack will then add their requirements methods until we have the full restock poth
 */
public class BankingFractal extends IronFractal {
    @Setter
    private static InventoryLoadout pubInventoryLoadout;

    public BankingFractal() {
        super(() -> pubInventoryLoadout != null);
        setSimpleName("Banking fractal");
    }

    @Override
    protected int onLoop() {
        if (pubInventoryLoadout.isFulfilled()) {
            log("Inventory loadout fulfilled");
            pubInventoryLoadout = null;
            return sleep();
        }

        if (!Bank.isCached()) {
            log("Need to get bank cache");
            if (Walking.shouldWalk()) Bank.open();
            return sleep();
        }

        // find items we dont have at least the min quantity of, add them to restock stack
        // todo needs variant handling
        List<InventoryLoadoutItem> restockRequiredItems = pubInventoryLoadout.getInventoryItems().stream()
                .filter(x -> x.getInventoryMin() > OwnedItems.count(x.getItemId()))
                .collect(Collectors.toList());
        for (InventoryLoadoutItem restockRequiredItem : restockRequiredItems) {
            log("Restock required on item " + restockRequiredItem);
            if (Inventory.emptySlotCount() < (new Item(restockRequiredItem.getItemId(), 0).isStackable() ? 1 : restockRequiredItem.getInventoryMin())) {
                log("Dont have enough space to get that item, depositing");
                if (!Bank.isOpen()) {
                    Bank.open();
                    return sleep();
                }

                Bank.depositAllItems();
                return sleep();
            }

            RestockStackFractal.addTask(restockRequiredItem.getRestockMethod());
            pubInventoryLoadout = null;
        }
        if (!restockRequiredItems.isEmpty()) return sleep();

        if (!Bank.isOpen()) {
            log("Opening bank");
            if (Walking.shouldWalk()) Bank.open();
            return sleep();
        }
        // deposit any items that arent in our inventory loadout
        if (pubInventoryLoadout.isStrict()) {
            log("Checking strictness");
            Item i = pubInventoryLoadout.getStrictItem();
            if (i != null) {
                log("Enforcing strictness deposit " + i.getName());
                Bank.depositAll(i);
                return sleep();
            }
        }

        // deposit items we have over the max of
        for (InventoryLoadoutItem loadoutItem : pubInventoryLoadout.getInventoryItems()) {
            // todo variant requirement
            int max = loadoutItem.getInventoryMax();
            int invCount = Inventory.count(loadoutItem.getItemId());
            if (max < invCount) {
                log("Item over max " + loadoutItem + " " + invCount);
                Bank.deposit(loadoutItem.getItemId(), invCount - max);
                return sleep();
            }
        }

        // withdraw up to the maximum of the required items
        for (InventoryLoadoutItem loadoutItem : pubInventoryLoadout.getInventoryItems()) {
            int invCount = Inventory.count(loadoutItem.getItemId());
            if (invCount < loadoutItem.getInventoryMin()) {
                if (OwnedItems.count(loadoutItem.getItemId()) < loadoutItem.getInventoryMin()) {
                    log("Don't have enough of item, restocking " + loadoutItem);
                    RestockStackFractal.addTask(loadoutItem.getRestockMethod());
                    return sleep();
                }
                log("Withdrawing " + loadoutItem);
                Bank.withdraw(loadoutItem.getItemId(), loadoutItem.getInventoryMax());
                return sleep();
            }
        }
        log("End");
        return sleep();
    }

}

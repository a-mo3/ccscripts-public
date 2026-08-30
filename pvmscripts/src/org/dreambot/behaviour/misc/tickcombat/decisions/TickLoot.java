package org.dreambot.behaviour.misc.tickcombat.decisions;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PVMUtil;

import java.util.Comparator;
import java.util.function.Supplier;

public class TickLoot extends TickDecision {
    final Supplier<GroundItem> lootSupplier;
    final Filter<GroundItem> lootFilter;
    final Supplier<Item> dropSupplier;

    public TickLoot(Supplier<GroundItem> lootSupplier, Filter<GroundItem> lootFilter, Supplier<Item> dropSupplier) {
        super();
        this.lootSupplier = lootSupplier;
        this.lootFilter = lootFilter;
        this.dropSupplier = dropSupplier;
    }

    public TickLoot(Supplier<GroundItem> lootSupplier) {
        super();
        this.lootSupplier = lootSupplier;
        this.lootFilter = null;
        this.dropSupplier = null;
    }

    @Override
    public boolean evaluate() {
        if (lootSupplier == null && lootFilter == null) return false;
        GroundItem loot;
        if (lootSupplier != null) {
            loot = lootSupplier.get();
        } else {
            // get most expensive
            loot = GroundItems.all(lootFilter)
                    .stream()
                    .max(Comparator.comparingInt(x -> x.getItem().isStackable() ? (x.getItem().getLivePrice() + 1) * x.getAmount()
                            : x.getItem().getLivePrice()))
                    .orElse(null);
        }
        if (loot == null) return false;

        // if inventory is full, find something cheaper to drop, otherwise continue
        if (Inventory.isFull()) {
            if (loot.getItem().isStackable() && Inventory.contains(loot.getId())) {
                log("Stackable loot we already own");
                loot.interact();
                return true;
            }

            Item cheapest = PVMUtil.getCheapest();
            if (dropSupplier != null) cheapest = dropSupplier.get();
            int lootValue = loot.getItem().isStackable() ? (loot.getItem().getLivePrice() + 1) * loot.getAmount() : loot.getItem().getLivePrice();
            if (cheapest != null && cheapest.getLivePrice() > lootValue) {
                log("Cheapest in inventory is more expensive than the loot " + cheapest.getName() + " " + loot.getItem().getName());
                return false;
            }
            if (cheapest != null)Inventory.drop(cheapest.getId());
        }

        loot.interact();
        return true;
    }
}

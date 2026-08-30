package org.dreambot.loadouts;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.IronFractal;

import java.util.function.BooleanSupplier;

@Accessors(chain = true)
@Getter
@Setter
public class InventoryLoadoutItem {
    public InventoryLoadoutItem() {
    }

    public InventoryLoadoutItem(int itemId) {
        this.itemId = itemId;
    }

    private int itemId;
    // inventory min and max are the min and max one can have in an inventory
    private int inventoryMin = 1;
    private int inventoryMax = 1;
    // refill is the amount we have to fetch before we are done with restocking.
    private int refill = 1;

    // todo variant handling

    // for setting levels when to use an item
    BooleanSupplier enabledCondition = () -> true;

    // items should have a restock method that has recursive requirements, ie need logs so axe restock method requires coins to buy from shop
    IronFractal restockMethod = null;

    @Override
    public String toString() {
        return "InvItem " + itemId + " " + new Item(itemId, 0).getName()
                + " Min " + inventoryMin
                + " Max " + inventoryMax
                + " Refill " + refill;
    }

    public InventoryLoadoutItem setRestockMethod(IronFractal restockMethod) {
        this.restockMethod = restockMethod;
        return this;
    }

    public InventoryLoadoutItem setRestockMethod(BooleanSupplier acceptCond, IronFractal... restockMethods) {
        setRestockMethod(new IronFractal(acceptCond).addChildren(restockMethods));
        return this;
    }

    /**
     * establish a bunch of methods for getting something, eg coins, at compile time, but at run time only keep 1
     *
     * @param possibleMethods collection of possible restocks
     * @return this, fluent.
     */
    public InventoryLoadoutItem setRandomRestockMethod(IronFractal... possibleMethods) {
        restockMethod = possibleMethods[Calculations.random(possibleMethods.length)];
        return this;
    }
}

package org.dreambot.fractals.loadout;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.util.OwnedItems;

import java.util.Arrays;
import java.util.function.Supplier;

@Accessors(chain = true)
@Setter
@Getter
public abstract class LoadoutItem {
    protected int maxPrice = 2000000000;
    ItemVariant variant = null;
    protected int itemId;
    protected int min;
    protected int max;
    protected int refill;
    protected int buyPrice;
    protected float priceIncrease = 1.05f;
    protected Supplier<Boolean> enabledCondition = null;
    protected Supplier<Integer> idSupplier = null;
    protected int muleRequestAmount = 10_000; // the amount to mule if you cant afford the restock

    public int getBuyPrice() {
        if (buyPrice < 0 && Client.isLoggedIn() && Client.getGameStateID() >= 30) {
            Logger.info("looking up price");
            buyPrice = LivePrices.getHigh(variant == null ? getItemId() : variant.getBaseId());
        }
        return buyPrice;
    }

    public int getItemId() {
        if (idSupplier != null) {
            return idSupplier.get();
        }
        if (variant != null) {
            return variant.getOwnedId();
        }
        return itemId;
    }

    // checks if id matches this item or any variant of this item
    public boolean idMatches(int id) {
        if (variant != null) {
            return Arrays.stream(variant.getIds()).anyMatch(x -> x == id);
        }
        return getItemId() == id;
    }

    // returns the count of this item and any of its variants in the inventory
    public int inventoryCount() {
        if (variant != null) {
            return Arrays.stream(variant.getIds())
                    .mapToInt(Inventory::count)
                    .sum();
        }
        return Inventory.count(getItemId());
    }


    public int ownedCount() {
        if (variant != null) {
            return Arrays.stream(variant.getIds())
                    .mapToInt(OwnedItems::count)
                    .sum();
        }
        return OwnedItems.count(getItemId());
    }


    public int ownedCount(boolean includeNoted) {
        if (variant != null) {
            return Arrays.stream(variant.getIds())
                    .mapToInt(x -> OwnedItems.count(x, includeNoted))
                    .sum();
        }
        return OwnedItems.count(getItemId(), includeNoted);
    }

    public int getBaseID() {
        if (variant != null) return variant.getBaseId();
        return getItemId();
    }

    public String getItemName() {
        return new Item(getBaseID(), 1).getName();
    }
}

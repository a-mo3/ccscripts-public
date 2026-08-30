package org.dreambot.fractals.loadout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.util.OwnedItems;

import java.util.Arrays;
import java.util.function.Supplier;

@Accessors(chain = true)
@Setter
@Getter
@Unobfuscated
@ToString
public abstract class LoadoutItem {
    protected int maxPrice = 2000000000;
    @Unobfuscated
    ItemVariant variant = null;
    @Unobfuscated
    protected int itemId;
    @Unobfuscated
    protected transient Supplier<Integer> minSupplier;
    @Unobfuscated
    protected transient Supplier<Integer> maxSupplier;
    @Unobfuscated
    protected int min;
    @Unobfuscated
    protected int max;
    @Unobfuscated
    protected int refill;
    @Unobfuscated
    protected Supplier<Integer> refillSupplier;
    @Unobfuscated
    protected int buyPrice;
    @Unobfuscated
    protected float priceIncrease = 1.05f;
    @Unobfuscated
    protected transient Supplier<Boolean> enabledCondition = null;
    @Unobfuscated
    protected transient Supplier<Integer> idSupplier = null;
    @Unobfuscated
    protected int muleRequestAmount = 10_000; // the amount to mule if you cant afford the restock

    public int getBuyPrice() {
        if (idSupplier != null) {
            return (int) (LivePrices.get(idSupplier.get()) * 1.5);
        }

        if (buyPrice < 0 && Client.isLoggedIn() && Client.getGameStateID() >= 30) {
            Logger.info("looking up price");
            buyPrice = LivePrices.getHigh(variant == null ? getItemId() : variant.getBaseId());
        }

        if (refill < 10 && buyPrice < 1000) return 1000;
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
        return OwnedItems.count(getUnnotedBaseID(), includeNoted);
    }

    public int getBaseID() {
        if (variant != null) return variant.getBaseId();
        return getItemId();
    }


    public int getUnnotedBaseID() {
        if (variant != null) return new Item(variant.getBaseId(), 1).getUnnotedItemID();
        return new Item(getItemId(), 1).getUnnotedItemID();
    }

    public int getMin() {
        if (minSupplier != null) return minSupplier.get();
        return min;
    }

    public int getMax() {
        if (maxSupplier != null) return maxSupplier.get();
        return max;
    }

    public int getRefill() {
        if (maxSupplier != null && refill < maxSupplier.get()) return maxSupplier.get();
        if (refillSupplier != null) return refillSupplier.get();
        return refill;
    }

    public String getItemName() {
        return new Item(getBaseID(), 1).getName();
    }
}

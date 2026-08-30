package org.dreambot.fractals.loadout;

import lombok.experimental.Accessors;
import org.dreambot.api.methods.grandexchange.LivePrices;

import java.util.function.Supplier;

@Accessors(chain = true)
public class InventoryLoadoutItem extends LoadoutItem {
    public InventoryLoadoutItem(ItemVariant variant) {
        this.variant = variant;
        this.itemId = variant.getBaseId();
        this.min = 1;
        this.max = 1;
        this.refill = max;
        this.buyPrice = LivePrices.get(itemId);
    }

    public InventoryLoadoutItem(ItemVariant variant, int min, int max) {
        this.variant = variant;
        this.itemId = variant.getBaseId();
        this.min = min;
        this.max = max;
        this.refill = max;
        this.buyPrice = LivePrices.get(itemId);
    }

    public InventoryLoadoutItem(Supplier<Integer> idSupplier, int min, int max) {
        this.idSupplier = idSupplier;
        this.min = min;
        this.max = min;
        this.refill = max;
        this.buyPrice = LivePrices.get(itemId);
    }

    public InventoryLoadoutItem(int itemId, int min) {
        this.itemId = itemId;
        this.min = min;
        this.max = min;
        this.refill = max;
        this.buyPrice = LivePrices.get(itemId);
    }

    public InventoryLoadoutItem(int itemId, Supplier<Integer> minSupplier) {
        this.itemId = itemId;
        this.minSupplier = minSupplier;
        this.maxSupplier = minSupplier;
        this.refill = max;
        this.buyPrice = LivePrices.get(itemId);
    }

    public InventoryLoadoutItem(int itemId, int min, int max) {
        this.itemId = itemId;
        this.min = min;
        this.max = max;
        this.refill = max;
        this.buyPrice = LivePrices.get(itemId);
    }
}

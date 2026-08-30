package org.dreambot.fractals.loadout;

import org.dreambot.api.methods.grandexchange.LivePrices;

public class EquipmentLoadoutItem extends LoadoutItem {
    public EquipmentLoadoutItem(int itemId) {
        this.itemId = itemId;
        this.min = 1;
        this.max = 1;
        this.refill = 1;
        this.buyPrice = LivePrices.get(itemId);
    }

    public EquipmentLoadoutItem(int itemId, int min, int max) {
        this.itemId = itemId;
        this.min = min;
        this.max = max;
        this.refill = max;
        this.buyPrice = LivePrices.get(itemId);
    }

    public EquipmentLoadoutItem(ItemVariant variant) {
        this.variant = variant;
        this.itemId = variant.getBaseId();
        this.min = 1;
        this.max = 1;
        this.refill = max;
        this.buyPrice = LivePrices.get(itemId);
    }
}

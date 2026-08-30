package org.dreambot.fractals.loadout;

import lombok.Getter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.wrappers.items.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipmentLoadout {
    @Getter
    Map<EquipmentSlot, EquipmentLoadoutItem> loadoutMap = new HashMap<>();
    EquipmentLoadoutItem lastAddedItem = null;

    public EquipmentLoadout setRefill(int refill) {
        lastAddedItem.setRefill(refill);
        return this;
    }

    public EquipmentLoadout addItem(EquipmentSlot slot, int itemId) {
        EquipmentLoadoutItem item = new EquipmentLoadoutItem(itemId);
        lastAddedItem = item;
        loadoutMap.put(slot, item);
        return this;
    }

    public EquipmentLoadout addItem(EquipmentSlot slot, ItemVariant variant) {
        EquipmentLoadoutItem i = new EquipmentLoadoutItem(variant);
        lastAddedItem = i;
        loadoutMap.put(slot, i);
        return this;
    }

    public EquipmentLoadout setBuyPrice(int buyPrice) {
        lastAddedItem.setBuyPrice(buyPrice);
        return this;
    }

    public EquipmentLoadout setPriceIncrease(int priceIncrease) {
        lastAddedItem.setPriceIncrease(priceIncrease);
        return this;
    }

    public boolean isFulfilled() {
        for (Map.Entry<EquipmentSlot, EquipmentLoadoutItem> loadoutEntry : loadoutMap.entrySet()) {
            EquipmentLoadoutItem loadoutItem = loadoutEntry.getValue();
            EquipmentSlot slot = loadoutEntry.getKey();
            if (slot == null || loadoutItem == null) {
                continue;
            }

            Item itemInSlot = Equipment.getItemInSlot(slot);
            if (itemInSlot == null || itemInSlot.getID() != loadoutItem.getItemId()) return false;
        }
        return true;
    }

    public EquipmentLoadoutItem getMissingItem() {
        for (Map.Entry<EquipmentSlot, EquipmentLoadoutItem> loadoutEntry : loadoutMap.entrySet()) {
            EquipmentLoadoutItem loadoutItem = loadoutEntry.getValue();
            EquipmentSlot slot = loadoutEntry.getKey();
            if (Client.isLoggedIn() && loadoutItem.getBuyPrice() < 0) {
                loadoutItem.setBuyPrice(LivePrices.get(loadoutItem.getItemId()));
            }
            if (slot == null || loadoutItem == null) {
                continue;
            }

            Item itemInSlot = Equipment.getItemInSlot(slot);
            if (itemInSlot == null || itemInSlot.getID() != loadoutItem.getItemId()) return loadoutItem;
        }
        return null;
    }


    public List<EquipmentLoadoutItem> getMissingItems() {
        List<EquipmentLoadoutItem> missingItems = new ArrayList<>();
        for (Map.Entry<EquipmentSlot, EquipmentLoadoutItem> loadoutEntry : loadoutMap.entrySet()) {
            EquipmentLoadoutItem loadoutItem = loadoutEntry.getValue();
            EquipmentSlot slot = loadoutEntry.getKey();
            if (Client.isLoggedIn() && loadoutItem.getBuyPrice() < 0) {
                loadoutItem.setBuyPrice(LivePrices.get(loadoutItem.getItemId()));
            }
            if (slot == null || loadoutItem == null) {
                continue;
            }

            if (loadoutItem.getEnabledCondition() != null && !loadoutItem.getEnabledCondition().get()) {
                continue;
            }

            Item itemInSlot = Equipment.getItemInSlot(slot);
            if (itemInSlot == null || itemInSlot.getID() != loadoutItem.getItemId()) missingItems.add(loadoutItem);
        }
        return missingItems;
    }
}

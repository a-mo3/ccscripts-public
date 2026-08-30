package org.dreambot.fractals.loadout;

import lombok.Getter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EquipmentLoadout {
    @Getter
    List<LoadoutMapEntry> loadoutList = new ArrayList<>();

    EquipmentLoadoutItem lastAddedItem = null;


    public EquipmentLoadout setEnabledCondition(Supplier<Boolean> enabledCondition) {
        if (lastAddedItem == null) Logger.info("LAST ADDED ITEM NULL - PROBLEM");
        lastAddedItem.setEnabledCondition(enabledCondition);
        return this;
    }

    public EquipmentLoadout setRefill(int refill) {
        lastAddedItem.setRefill(refill);
        return this;
    }

    public EquipmentLoadout addItem(EquipmentSlot slot, int itemId) {
        EquipmentLoadoutItem item = new EquipmentLoadoutItem(itemId);
        lastAddedItem = item;
        loadoutList.add(new LoadoutMapEntry(slot, item));
        return this;
    }

    public EquipmentLoadout addItem(EquipmentSlot slot, ItemVariant variant) {
        EquipmentLoadoutItem i = new EquipmentLoadoutItem(variant);
        lastAddedItem = i;
        loadoutList.add(new LoadoutMapEntry(slot, i));
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
        return getMissingItems().isEmpty();
    }

    public EquipmentLoadoutItem getMissingItem() {
        for (LoadoutMapEntry loadoutEntry : loadoutList) {
            EquipmentLoadoutItem loadoutItem = loadoutEntry.item;
            EquipmentSlot slot = loadoutEntry.slot;
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
//        loadoutList.forEach((x, y) -> Logger.info(x + " " + y.getItemId()));
        for (LoadoutMapEntry loadoutEntry : loadoutList) {
            EquipmentLoadoutItem loadoutItem = loadoutEntry.getItem();
            EquipmentSlot slot = loadoutEntry.getSlot();
            if (slot == null || loadoutItem == null) {
                Logger.info("null!");
                continue;
            }
//            if (loadoutItem.getItemId() < 0) {
//                continue;
//            }

            Supplier<Boolean> cond = loadoutItem.getEnabledCondition();
            if (cond != null && !cond.get()) {
                Logger.info("cond false " + loadoutItem.getItemName() + " " + loadoutItem.itemId);
                continue;
            }

            if (Client.isLoggedIn() && loadoutItem.getBuyPrice() < 0) {
                loadoutItem.setBuyPrice(LivePrices.get(loadoutItem.getItemId()));
            }

            if (slot == EquipmentSlot.WEAPON) {
                Logger.info("Weapon " + loadoutItem.getItemId());
            }

            Item itemInSlot = Equipment.getItemInSlot(slot);
            if (itemInSlot == null || itemInSlot.getID() != loadoutItem.getItemId()) missingItems.add(loadoutItem);
        }
        return missingItems;
    }
}

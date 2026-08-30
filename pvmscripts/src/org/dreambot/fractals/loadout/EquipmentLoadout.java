package org.dreambot.fractals.loadout;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.Log;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Accessors(chain = true)
public class EquipmentLoadout {
    @Setter
    @Getter
    boolean strict = false;
    @Unobfuscated
    @Getter
    List<LoadoutMapEntry> loadoutList = new ArrayList<>();

    @Unobfuscated
    EquipmentLoadoutItem lastAddedItem = null;

    public EquipmentLoadout() {
    }

    public EquipmentLoadout(EquipmentLoadout loadout) {
        this.loadoutList = new ArrayList<LoadoutMapEntry>();
        this.loadoutList.addAll(loadout.loadoutList);
    }

    public EquipmentLoadout remove(Predicate<LoadoutMapEntry> predicate) {
        loadoutList.removeIf(predicate);
        return this;
    }

    public EquipmentLoadout enabledIfOwned() {
        if (lastAddedItem == null) Logger.info("LAST ADDED ITEM NULL - PROBLEM");
        int id = lastAddedItem.itemId; // important because lastAddedItem will change
        if (lastAddedItem.variant == null)  {
            lastAddedItem.setEnabledCondition(() -> OwnedItems.contains(id));
        } else  {
            Integer[] arr = lastAddedItem.variant.getIds();
            lastAddedItem.setEnabledCondition(() -> OwnedItems.containsAny(arr));
        }

        return this;
    }

    public EquipmentLoadout enabledIfOwned(Supplier<Boolean> enabledCondition) {
        if (lastAddedItem == null) Logger.info("LAST ADDED ITEM NULL - PROBLEM");
        int id = lastAddedItem.itemId; // important because lastAddedItem will change
        lastAddedItem.setEnabledCondition(() -> enabledCondition.get() && OwnedItems.contains(id));
        return this;
    }

    public EquipmentLoadout setEnabledCondition(Supplier<Boolean> enabledCondition) {
        if (lastAddedItem == null) Logger.info("LAST ADDED ITEM NULL - PROBLEM");
        lastAddedItem.setEnabledCondition(enabledCondition);
        return this;
    }

    public EquipmentLoadout addEnabledCondition(Supplier<Boolean> tenabledCondition) {
        if (lastAddedItem == null) Logger.info("LAST ADDED ITEM NULL - PROBLEM");
        Supplier<Boolean> prevCond = lastAddedItem.enabledCondition;
        if (prevCond == null) {
            lastAddedItem.setEnabledCondition(tenabledCondition);
            return this;
        }
        lastAddedItem.setEnabledCondition(() -> prevCond.get() && tenabledCondition.get());
        return this;
    }

    public EquipmentLoadout setRefill(int refill) {
        lastAddedItem.setRefill(refill);
        return this;
    }

    public EquipmentLoadout addItem(EquipmentSlot slot, Supplier<Integer> variant) {
        EquipmentLoadoutItem i = new EquipmentLoadoutItem(variant);
        lastAddedItem = i;
        loadoutList.add(new LoadoutMapEntry(slot, i));
        return this;
    }

    public EquipmentLoadout addItem(EquipmentSlot slot, EquipmentLoadoutItem item) {
        lastAddedItem = item;
        loadoutList.add(new LoadoutMapEntry(slot, item));
        return this;
    }

    public EquipmentLoadout addItem(EquipmentSlot slot, int itemId) {
        EquipmentLoadoutItem item = new EquipmentLoadoutItem(itemId);
        lastAddedItem = item;
        loadoutList.add(new LoadoutMapEntry(slot, item));
        return this;
    }

    public EquipmentLoadout addItem(EquipmentSlot slot, int itemId, int min, int max) {
        EquipmentLoadoutItem item = new EquipmentLoadoutItem(itemId, min, max);
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
        if (isStrict() && !getStrictItems().isEmpty()) {
            Logger.info("Equipment unfulfilled due to strictness");
            return false;
        }

        Map<EquipmentSlot, EquipmentLoadoutItem> map = new HashMap<>();
        for (LoadoutMapEntry loadoutEntry : loadoutList) {
            EquipmentLoadoutItem loadoutItem = loadoutEntry.getItem();
            EquipmentSlot slot = loadoutEntry.getSlot();
            if (slot == null || loadoutItem == null) {
                Logger.info("null!");
                continue;
            }

            Supplier<Boolean> cond = loadoutItem.getEnabledCondition();
            if (cond != null && !cond.get()) {
                continue;
            }

            map.put(slot, loadoutItem);
        }

        // check ammo and weapon slot for above or below count, (darts and arrows)
        EquipmentLoadoutItem arrowReq = map.get(EquipmentSlot.ARROWS);
        if (arrowReq != null) {
            Item inSlot = Equipment.getItemInSlot(EquipmentSlot.ARROWS);
            if (inSlot == null) return false;
            int arrowCount = inSlot.getAmount();
            if (arrowReq.getMin() > arrowCount || arrowReq.getMax() < arrowCount) {
                Logger.info("Loadouts arrow count was too high or low");
                return false;
            }
        }

        EquipmentLoadoutItem weaponReq = map.get(EquipmentSlot.WEAPON);
        if (weaponReq != null) {
            Item inSlot = Equipment.getItemInSlot(EquipmentSlot.WEAPON);
            if (inSlot == null) return false;
            int weaponCount = inSlot.getAmount();
            if (weaponReq.getMin() > weaponCount || weaponReq.getMax() < weaponCount) {
                Logger.info("Loadouts weapon count was too high or low");
                return false;
            }
        }


        return getMissingItems().isEmpty();
    }

    public List<EquipmentLoadoutItem> getMissingItems() {
        Map<EquipmentSlot, EquipmentLoadoutItem> map = new HashMap<>();
        /*
         we use a map here to not allow 2 conflicting items with both true conditions
         eg.
         rune boots >= 40 def
         dragon boots >= 70 def
         70 def both are true, but only dragon boots should be used, this makes creating complex progressive loadouts much simpler
         we do this first to not fuck up the slot checking in the 2nd iteration
         */
        for (LoadoutMapEntry loadoutEntry : loadoutList) {
            EquipmentLoadoutItem loadoutItem = loadoutEntry.getItem();
            EquipmentSlot slot = loadoutEntry.getSlot();
            if (slot == null || loadoutItem == null) {
                Logger.info("null!");
                continue;
            }

            if (!Client.isMembers() && new Item(loadoutEntry.getItem().getItemId(), 1).isMembersOnly()) {
                Logger.info("skip item thats mems only " + loadoutEntry.getItem().itemId);
                continue;
            }

            Supplier<Boolean> cond = loadoutItem.getEnabledCondition();
            if (cond != null && !cond.get()) {
                continue;
            }

            map.put(slot, loadoutItem);
        }

        List<EquipmentLoadoutItem> penis = new ArrayList<>();
        for (LoadoutMapEntry loadoutEntry : map.entrySet()
                .stream()
                .map(x -> new LoadoutMapEntry(x.getKey(), x.getValue()))
                .collect(Collectors.toList())) {
            EquipmentLoadoutItem loadoutItem = loadoutEntry.getItem();
            EquipmentSlot slot = loadoutEntry.getSlot();
//            Logger.info("Checking item " + loadoutItem.getItemName());
            if (slot == null) {
                Logger.info("Null slot");
                continue;
            }

            Supplier<Boolean> cond = loadoutItem.getEnabledCondition();
            if (cond != null && !cond.get()) {
                Logger.info("Equipment condition false");
                continue;
            }

            if (Client.isLoggedIn() && loadoutItem.getBuyPrice() < 0) {
                loadoutItem.setBuyPrice(LivePrices.get(loadoutItem.getItemId()));
            }

            Item itemInSlot = Equipment.getItemInSlot(slot);
            if (itemInSlot == null || itemInSlot.getId() != loadoutItem.getItemId()) {
//                Logger.info("Item slot check - in slot: "  + (itemInSlot == null ? 0 : itemInSlot.getId() + " Required: " + loadoutItem.getItemId()));
                penis.add(loadoutItem);
            } else {

            }
        }
//        Logger.info("Equipment loadout missing items.");
        return penis;
    }

    public List<Item> getStrictItems() {
        List<Item> strictItems = new ArrayList<>();
        for (Item item : Equipment.all()) {
            if (item == null) {
                continue;
            }
//            if (strictIgnoredItemIDs.contains(item.getId())) {
//                continue;
//            }

            if (loadoutList.stream().noneMatch(loadoutItem -> {
                if (loadoutItem.item.variant != null) {
                    return Arrays.stream(loadoutItem.item.variant.getIds()).anyMatch(x -> x == item.getId());
                }
                if (loadoutItem.item.idSupplier != null) {
                    return loadoutItem.item.idSupplier.get() == item.getId();
                }
                return loadoutItem.item.getItemId() == item.getId();
            })) {
                strictItems.add(item);
            }
        }
        return strictItems;
    }

    public EquipmentLoadout clone() {
        return new EquipmentLoadout(this);
    }
}

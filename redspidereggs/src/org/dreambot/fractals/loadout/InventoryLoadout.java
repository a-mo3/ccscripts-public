package org.dreambot.fractals.loadout;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.OwnedItems;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;


@Accessors(chain = true)
public class InventoryLoadout {
    @Setter
    @Getter
    private boolean strict = false;
    @Setter
    private BooleanSupplier strictSupplier = null;
    public List<InventoryLoadoutItem> loadoutItems = new ArrayList<>();
    private InventoryLoadoutItem lastAddedItem = new InventoryLoadoutItem(1, 1);
    // botched fix for limited strictness
    private final Set<Integer> strictIgnoredItemIDs = new HashSet<>();
    @Getter
    private int[] sellItems = new int[]{};

    public InventoryLoadout setSellItems(int... sellItems) {
        this.sellItems = sellItems;
        return this;
    }

    public InventoryLoadout() {}
    public InventoryLoadout(InventoryLoadout inventoryLoadout) {
        this.loadoutItems = new ArrayList<>();
        this.loadoutItems.addAll(inventoryLoadout.loadoutItems);
//        this.strictIgnoredItemIDs = inventoryLoadout.strictIgnoredItemIDs;
        this.strict = inventoryLoadout.strict;
        this.strictSupplier = inventoryLoadout.strictSupplier;
        this.sellItems = inventoryLoadout.sellItems;
    }

    /**
     * @param ids ignore these items when checking strict requirements
     * @return
     */
    public InventoryLoadout strictIgnore(int... ids) {
        for (int id : ids) {
            strictIgnoredItemIDs.add(id);
        }
        return this;
    }

    public InventoryLoadout setPriceIncrease(float increase) {
        if (increase == 0) {
            Logger.info("price increase was set to 0, assume 1 for no increase");
            increase = 1;
        }
        lastAddedItem.setPriceIncrease(increase);
        return this;
    }

    public InventoryLoadout setBuyPrice(int buyPrice) {
        lastAddedItem.setBuyPrice(buyPrice);
        return this;
    }

    public InventoryLoadout setEnabledCondition(Supplier<Boolean> enabledCondition) {
        lastAddedItem.setEnabledCondition(enabledCondition);
        return this;
    }

    public InventoryLoadout setRefill(int refill) {
        lastAddedItem.setRefill(refill);
        return this;
    }

    public InventoryLoadout addItem(InventoryLoadoutItem item) {
        loadoutItems.add(item);
        lastAddedItem = item;
        return this;
    }

    public InventoryLoadout addItem(Supplier<Integer> idSupplier, int min) {
        InventoryLoadoutItem item = new InventoryLoadoutItem(idSupplier, min, min);
        lastAddedItem = item;
        loadoutItems.add(item);
        return this;
    }

    public InventoryLoadout addItem(int itemid, int min) {
        InventoryLoadoutItem item = new InventoryLoadoutItem(itemid, min);
        lastAddedItem = item;
        loadoutItems.add(item);
        return this;
    }

    public InventoryLoadout addItem(int itemid) {
        InventoryLoadoutItem item = new InventoryLoadoutItem(itemid, 1);
        lastAddedItem = item;
        loadoutItems.add(item);
        return this;
    }

    public InventoryLoadout addItem(int itemid, int min, int max) {
        if (min > max) max = min;
        InventoryLoadoutItem item = new InventoryLoadoutItem(itemid, min, max);
        lastAddedItem = item;
        loadoutItems.add(item);
        return this;
    }

    public InventoryLoadout addItem(ItemVariant variant) {
        InventoryLoadoutItem item = new InventoryLoadoutItem(variant);
        lastAddedItem = item;
        loadoutItems.add(item);
        return this;
    }

    public InventoryLoadout addItem(ItemVariant variant, int min, int max) {
        InventoryLoadoutItem item = new InventoryLoadoutItem(variant, min, max);
        lastAddedItem = item;
        loadoutItems.add(item);
        return this;
    }

    public boolean isFulfilled() {
        for (InventoryLoadoutItem item : loadoutItems) {
            if (item.getEnabledCondition() != null && !item.getEnabledCondition().get()) {
                continue;
            }
            // clear example as to doga being out of his faggot mind
            int count = item.inventoryCount();
            if (count < item.getMin() || count > item.getMax()) {
                Logger.info(String.format("Item above or below requirements id: %d - %s min: %d, max: %d Owned: %d",
                        item.getItemId(), new Item(item.getItemId(), 0).getName(), item.getMin(), item.getMax(), OwnedItems.count(item.getItemId())));
                return false;
            }
        }

        if (strictSupplier != null) {
            strict = strictSupplier.getAsBoolean();
        }

        if (strict) {
            Item strictItem = getStrictItem();
            if (strictItem != null) {
                Logger.info("found strict item " + strictItem.getName());
                Logger.info("Inv loadout strictness not fulfilled");
                return false;
            }
        }
        return true;
    }

    // returns item that is in ur inv and not in loadout
    public Item getStrictItem() {
        for (Item item : Inventory.all()) {
            if (item == null) {
                continue;
            }

            if (strictIgnoredItemIDs.contains(item.getID())) {
                continue;
            }

            if (loadoutItems.stream().noneMatch(loadoutItem -> {
                if (loadoutItem.variant != null) {
                    return Arrays.stream(loadoutItem.variant.getIds()).anyMatch(x -> x == item.getID());
                }
                if (loadoutItem.idSupplier != null) {
                    return loadoutItem.idSupplier.get() == item.getID();
                }
                return loadoutItem.getItemId() == item.getID();
            })) {
                return item;
            }
        }
        return null;
    }


    public List<Item> getStrictItems() {
        List<Item> strictItems = new ArrayList<>();
        for (Item item : Inventory.all()) {
            if (item == null) {
                continue;
            }

            if (strictIgnoredItemIDs.contains(item.getID())) {
                continue;
            }

            if (loadoutItems.stream().noneMatch(loadoutItem -> {
                if (loadoutItem.variant != null) {
                    return Arrays.stream(loadoutItem.variant.getIds()).anyMatch(x -> x == item.getID());
                }
                if (loadoutItem.idSupplier != null) {
                    return loadoutItem.idSupplier.get() == item.getID();
                }
                return loadoutItem.getItemId() == item.getID();
            })) {
                strictItems.add(item);
            }
        }
        return strictItems;
    }


    public Item getStrictItem(boolean ignoreCoins) {
        for (Item item : Inventory.all()) {
            if (item == null) {
                continue;
            }

            if (strictIgnoredItemIDs.contains(item.getID())) {
                continue;
            }

            if (ignoreCoins && item.getID() == ItemID.COINS_995) continue;
            if (loadoutItems.stream().noneMatch(loadoutItem -> {
                if (loadoutItem.variant != null) {
                    return Arrays.stream(loadoutItem.variant.getIds()).anyMatch(x -> x == item.getID());
                }
                return loadoutItem.getItemId() == item.getID();
            })) return item;
        }
        return null;
    }




    public InventoryLoadoutItem getMissingItem() {
        for (InventoryLoadoutItem item : loadoutItems) {
            if (item.getEnabledCondition() != null && !item.getEnabledCondition().get()) {
                continue;
            }
            if (Client.isLoggedIn() && item.getBuyPrice() < 0) {
                item.setBuyPrice(LivePrices.get(item.getItemId()));
            }
            // clear example as to doga being out of his faggot mind
            int count = Inventory.count(item.getItemId());
            if (count < item.getMin() || count > item.getMax()) {
                return item;
            }
        }
        return null;
    }


    public List<InventoryLoadoutItem> getMissingItems() {
        List<InventoryLoadoutItem> missingItems = new ArrayList<>();
        for (InventoryLoadoutItem item : loadoutItems) {
            if (item.getEnabledCondition() != null && !item.getEnabledCondition().get()) {
                continue;
            }

            if (Client.isLoggedIn() && item.getBuyPrice() < 0) {
                item.setBuyPrice(LivePrices.get(item.getItemId()));
            }
            // clear example as to doga being out of his faggot mind
            int count = Inventory.count(item.getItemId());
            if (count < item.getMin() || count > item.getMax()) {
                missingItems.add(item);
            }
        }
        return missingItems;
    }

    /**
     * sets the mule request amount of all the items, should be done last when making a loadout
     * @return
     */
    public InventoryLoadout setMuleRequestAmount(int amount) {
        for (LoadoutItem i : loadoutItems) {
            i.setMuleRequestAmount(amount);
        }
        return this;
    }
}

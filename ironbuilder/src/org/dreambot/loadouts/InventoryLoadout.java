package org.dreambot.loadouts;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * attached to a fractal, a model that represents the desired inventory domain
 * when a fractal accepts it will check if its inventory is allowed by this model,
 * if not it will set it in the LoadoutFractal
 * LoadoutFractal would set the restock stack if items are not owned
 */
@Accessors(chain = true)
@Getter
@Setter
public class InventoryLoadout {
    private final List<InventoryLoadoutItem> inventoryItems = new ArrayList<>();
    // ids that are in the inventory items
    private final Set<Integer> allowedIds = new HashSet<>();
    /**
     * if a loadout is stick that means it cant have any items other than ones in the inventory loadout
     */
    BooleanSupplier strict = () -> false;

    /**
     * predicate for inventory items are exempt from strict requirements, eg in woodcutting you'd want
     * strict axe only, ignoring any of the logs
     */
    Predicate<Item> strictIgnore = i -> false;

    public InventoryLoadout addItem(InventoryLoadoutItem item) {
        inventoryItems.add(item);
        allowedIds.add(item.getItemId()); // todo update for variants
        return this;
    }

    public boolean isFulfilled() {
        // todo will need handling for variants
        // this probably will be running every search of the script tree, dont log too much
        for (InventoryLoadoutItem inventoryItem : inventoryItems) {
            int inventoryCount = Inventory.count(inventoryItem.getItemId());
            if (inventoryCount < inventoryItem.getInventoryMin()) {
                log("Item below min " + inventoryCount + " of " + inventoryItem);
                return false;
            }

            if (inventoryCount > inventoryItem.getInventoryMax()) {
                log("Item above max " + inventoryCount + " of " + inventoryItem);
                return false;
            }
        }

        // handle strictness
        if (isStrict()) {
            if (getStrictItem() != null) return false;
        }
        return true;
    }

    private void log(String string) {
        Logger.info("[Loadout] " + string);
    }

    public boolean isStrict() {
        return strict.getAsBoolean();
    }

    /**
     * @return null if strict compliant, otherwise the Item in your inventory that needs to be put away
     */
    public Item getStrictItem() {
        for (Item i : Inventory.all()) {
            if (i == null) continue;
            if (!allowedIds.contains(i.getId())) {
                if (strictIgnore.test(i)) {
                    continue;
                }
                log("Strict item found " + i.getName() + " " + i.getId());
                return i;
            }
        }
        return null;
    }
}

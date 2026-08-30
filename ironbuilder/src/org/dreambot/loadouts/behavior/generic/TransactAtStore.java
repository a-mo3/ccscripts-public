package org.dreambot.loadouts.behavior.generic;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.IronFractal;
import org.dreambot.loadouts.data.ShopLocation;
import org.dreambot.utility.OwnedItems;

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * buys or sell an item at the store
 *
 */
@Accessors(chain = true)
@Setter
public class TransactAtStore extends IronFractal {
    boolean buyMode = true;
    final Area storeLocation;
    final Supplier<NPC> shopKeepSupplier;
    final Filter<Item> itemFilter;

    int transactionQuantity = 1;
    // dont buy or hop if below this amount
    // if buymode = false, and we're selling, this would be treated as maxStock
    int stockLimit = 1;
    String tradeAction = "Trade";
    // types of world we hop to if stock
    Filter<World> hopToWorldFilter = x -> x.isNormal() && x.isMembers() == Client.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel();


    public TransactAtStore(BooleanSupplier acceptCondition, Area storeLocation, Supplier<NPC> shopSupplier, Filter<Item> itemFilter) {
        super(acceptCondition);
        this.storeLocation = storeLocation;
        this.shopKeepSupplier = shopSupplier;
        this.itemFilter = itemFilter;
        setSimpleName("Buy at store");
    }

    public TransactAtStore(BooleanSupplier acceptCondition, ShopLocation shop, Filter<Item> itemFilter) {
        super(acceptCondition);
        this.storeLocation = shop.getStoreLocation();
        this.shopKeepSupplier = () -> NPCs.closest(shop.getNpcFilter());
        this.itemFilter = itemFilter;
        setSimpleName("Buy at store");
    }

    public TransactAtStore(ShopLocation shop, int itemId) {
        super(() -> !OwnedItems.contains(itemId));
        this.storeLocation = shop.getStoreLocation();
        this.shopKeepSupplier = () -> NPCs.closest(shop.getNpcFilter());
        this.itemFilter = i -> i.getId() == itemId;
        setSimpleName("Buy at store " + shop.name() + " " + new Item(itemId, 0).getName());
        // todo warn if theres no gold fetch condition attached to this or make a default one.
    }

    public TransactAtStore(BooleanSupplier acceptCondition, ShopLocation shop, int[] itemFilter) {
        super(acceptCondition);
        this.storeLocation = shop.getStoreLocation();
        this.shopKeepSupplier = () -> NPCs.closest(shop.getNpcFilter());
        this.itemFilter = x -> Arrays.stream(itemFilter).anyMatch(i -> i == x.getId());
        setSimpleName("Buy at store");
    }

    @Override
    protected int onLoop() {
        if (itemFilter == null) {
            log("No item filter");
            return sleep();
        }

        if (shopKeepSupplier == null) {
            log("No shop supplier");
            return sleep();
        }

        if (storeLocation != null && !storeLocation.contains(Players.getLocal())) {
            log("Go to store");
            if (Walking.shouldWalk()) Walking.walk(storeLocation);
            return sleep();
        }

        if (Shop.isOpen()) {
            Item i = Shop.get(itemFilter);
            if (i == null && buyMode) {
                log("no item found in shop?");
                return sleep();
            }

            // check if the store has less stock than our limit if buyMode, otherwise more if we're selling something
            int inStoreAmount = i == null ? 0 : i.getAmount();
            if (buyMode == (inStoreAmount < stockLimit)) {
                log("Less than the min stock");
                if (hopToWorldFilter != null) {
                    int w = Worlds.getRandomWorld(hopToWorldFilter).getWorld();
                    log("Hopping to different world " + w);
                    Widgets.closeAll();
                    Sleep.sleepUntil(() -> !Widgets.isOpen(), 2400);
                    WorldHopper.hopWorld(w);
                    Sleep.sleepUntil(() -> Client.isLoggedIn() && Worlds.getCurrentWorld() == w, 6000);
                }
                return sleep();
            }

            if (buyMode) {
                log("Buying " + transactionQuantity);
                Shop.purchase(itemFilter, transactionQuantity);
            } else {
                log("Selling " + transactionQuantity);
                Shop.sell(itemFilter, transactionQuantity);
            }
            return sleep();
        }

        NPC shopKeep = shopKeepSupplier.get();
        if (shopKeep != null) {
            log("Trading shopkeep " + tradeAction);
            shopKeep.interact(tradeAction);
            return sleep();
        }
        return sleep();
    }
}

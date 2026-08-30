package org.dreambot.behaviour.fishing;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.BankAllItems;
import org.dreambot.generics.DropAllItems;
import org.dreambot.generics.GenericEntityInteraction;
import org.dreambot.generics.SleepFractal;
import org.dreambot.loadouts.InventoryLoadout;
import org.dreambot.loadouts.behavior.generic.TransactAtStore;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.loadouts.data.ShopLocation;

import java.util.function.BooleanSupplier;

public class FishingFractal extends IronFractal {
    @Setter
    @Accessors(chain = true)
    public static class Builder {
        BooleanSupplier acceptCondition;
        Area location;
        Filter<NPC> spotFilter;
        String action;
        boolean bankAll;
        boolean sellAll;
        ShopLocation store;
        Area cookingArea;
        InventoryLoadout loadout;

        public FishingFractal build() {
            return new FishingFractal(acceptCondition, location, spotFilter, action, bankAll, sellAll, store, cookingArea, loadout);
        }
    }


    public FishingFractal(BooleanSupplier acceptCondition, Area location, Filter<NPC> spotFilter, String action, boolean bankAll, boolean sellAll, ShopLocation store, Area cookingArea, InventoryLoadout loadout) {
        super(acceptCondition);
        log("Construct fishing");

        int[] fishIds = new int[]{
                ItemID.SMALL_FISHING_NET, ItemID.RAW_ANCHOVIES, ItemID.RAW_SHRIMPS,
                ItemID.RAW_SALMON, ItemID.RAW_SARDINE, ItemID.RAW_TROUT, ItemID.RAW_PIKE, ItemID.RAW_HERRING,
                ItemID.RAW_SWORDFISH, ItemID.RAW_LOBSTER,
        };

        addChildren(
                // bank / sell / cook all
                // todo exclude the appropriate fishing equipment here
                new BankAllItems(() -> bankAll && Inventory.isFull()).setSimpleName("Bank"),

                // todo on sell mode bank all that is not fishing gear and fish, on bank mode it'd do this on first pass anyway
                // sell
                new TransactAtStore(() -> sellAll && Inventory.isFull(), ShopLocation.GERRANTS_FISHY_BUSINESS, fishIds)
                        .setTransactionQuantity(50)
                        .setBuyMode(false)
                        .setStockLimit(50)
                        .setUnlockedWhen(() -> !Inventory.contains(fishIds))
                        .setLockedWhen(Inventory::isFull)
                        .setSimpleName("Sell fish"),

                new BankAllItems(() -> sellAll && Inventory.contains(ItemID.COINS_995)).setSimpleName("Bank coins"),

                new BankAllItems(
                        ItemID.SMALL_FISHING_NET, ItemID.RAW_ANCHOVIES, ItemID.RAW_SHRIMPS,
                        ItemID.FISHING_ROD, ItemID.FISHING_BAIT,
                        ItemID.RAW_SALMON, ItemID.RAW_SARDINE, ItemID.RAW_TROUT, ItemID.RAW_PIKE, ItemID.RAW_HERRING,
                        ItemID.HARPOON, ItemID.RAW_SWORDFISH, ItemID.RAW_LOBSTER,
                        ItemID.FEATHER, ItemID.FLY_FISHING_ROD
                ).setSimpleName("Safeguard banking"),

                // if didn't bank and didnt sell, drop all fish
                new DropAllItems(Inventory::isFull, fishIds)
                        .setSimpleName("Drop fish"),

                new SleepFractal(() -> location.contains(Players.getLocal()) && Players.getLocal().isAnimating())
                        .setSimpleName("Idle"),
                // interact with fishing spot
                new GenericEntityInteraction(() -> true, () -> NPCs.closest(spotFilter))
                        .setAction(action)
                        .setEntityLocation(location)
                        .setInventoryLoadout(loadout)
                        .setSimpleName("Fish")
        );
    }
}

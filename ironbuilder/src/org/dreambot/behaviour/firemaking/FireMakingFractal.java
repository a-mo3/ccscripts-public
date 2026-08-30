package org.dreambot.behaviour.firemaking;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.woodcutting.NormalTreeLocation;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.BankAllItems;
import org.dreambot.generics.GenericEntityInteraction;
import org.dreambot.generics.GenericItemUse;
import org.dreambot.loadouts.InventoryLoadout;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.loadouts.data.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class FireMakingFractal extends IronFractal {
    public FireMakingFractal(BooleanSupplier acceptCondition, FireMakingMode mode, boolean takeAsh, FiremakingLogType logType) {
        super(acceptCondition);
//        if (mode == FireMakingMode.FLOOR_LOGS) {
//            addChildren(
//                    new GenericItemUse(() -> true, () -> GroundItems.closest(ItemID.LOGS), "Tinderbox")
//                            .setLocation(loc.treeLocation)
//                            .setInventoryLoadout(new InventoryLoadout().addItem(Items.TINDERBOX))
//                            .setSimpleName("Floor logs " + loc)
//            );
//            return;
//        }

        NormalTreeLocation loc = NormalTreeLocation.values()[Calculations.random(NormalTreeLocation.values().length)];
        Filter<GroundItem> ashFilter = x -> x.getId() == ItemID.ASHES && x.distance() < 8;
        List<Integer> acceptableItems = new ArrayList<>();
        acceptableItems.add(ItemID.LOGS);
        acceptableItems.add(ItemID.TINDERBOX);
        acceptableItems.add(ItemID.BRONZE_AXE);
        // todo other axes
        Filter<Item> except = x -> acceptableItems.contains(x.getId());

        addChildren(
                new GenericEntityInteraction(() -> takeAsh && GroundItems.closest(ashFilter) != null,
                        () -> GroundItems.closest(ashFilter))
                        .setSimpleName("Take ash"),
                // chop trees and light fire
                // edge case of inv full but no logs? handled by "loadouts"?
                new BankAllItems(() -> Inventory.isFull() && Inventory.count(i -> !acceptableItems.contains(i.getId())) > 5, except)
                        .setSimpleName("Bank"),

                new SetFire(Inventory::isFull)
                        .setLockedWhen(Inventory::isFull)
                        .setUnlockedWhen(() -> !Inventory.containsAll(ItemID.TINDERBOX, ItemID.LOGS)),

                new GenericEntityInteraction(() -> true, () -> GameObjects.closest("Tree"))
                        .setEntityLocation(loc.treeLocation)
                        .setAction("Chop down")
                        .setSleepCondition(Inventory::isFull)
                        .setResetCondition(() -> Players.getLocal().isAnimating())
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(Items.BRONZE_AXE_STUMP)
                                .addItem(Items.TINDERBOX)
                        )
                        .setSimpleName("Get logs " + loc)
                // after locked fire make, depending on if you are bonfire or not
        );
    }
}

package org.dreambot.behaviour.woodcutting;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.BankAllItems;
import org.dreambot.generics.DropAllItems;
import org.dreambot.generics.GenericEntityInteraction;
import org.dreambot.generics.SleepFractal;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.loadouts.data.Items;

import java.util.function.BooleanSupplier;

public class WoodcutFractal extends IronFractal {
    public WoodcutFractal(BooleanSupplier acceptCondition, Filter<GameObject> treeFilter, Area location, boolean bank) {
        super(acceptCondition);

        addChildren(
                // todo sell at store option
                new BankAllItems(() -> bank && Inventory.isFull(), x -> x.getId() == ItemID.BRONZE_PICKAXE && Inventory.count(ItemID.BRONZE_PICKAXE) == 1)
                        .setSimpleName("Bank"),

                new BankAllItems(ItemID.LOGS, ItemID.OAK_LOGS, ItemID.YEW_LOGS,
                        ItemID.TINDERBOX, ItemID.MAGIC_LOGS, ItemID.MAPLE_LOGS, ItemID.WILLOW_LOGS, ItemID.BRONZE_AXE
                ).setSimpleName("Safe bank"),

                new DropAllItems(() -> !bank && Inventory.isFull(),
                        x -> !x.getName().toLowerCase().contains("axe")).setSimpleName("Drop all"),

                new SleepFractal(() -> location.contains(Players.getLocal()) && (!Walking.shouldWalk() || Players.getLocal().isAnimating())).setSimpleName("Idle"),
                new GenericEntityInteraction(() -> true, () -> GameObjects.closest(treeFilter))
                        .setEntityLocation(location)
                        .addInventoryItem(Items.BRONZE_AXE_STUMP)
                        .setSimpleName("Chop")
        );
    }
}

package org.dreambot.behaviour.mining;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.BankAllItems;
import org.dreambot.generics.DropAllItems;
import org.dreambot.generics.GenericEntityInteraction;
import org.dreambot.generics.SleepFractal;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.loadouts.data.Items;

import java.util.function.BooleanSupplier;

public class MiningFractal extends IronFractal {
    final Filter<GameObject> rockFilter;


    public MiningFractal(BooleanSupplier acceptCondition, Filter<GameObject> rockFilter, Area location, boolean bank) {
        super(acceptCondition);
        this.rockFilter = x -> location.contains(x) && rockFilter.match(x);
        addChildren(
                // todo sell at store option
                new BankAllItems(() -> bank && Inventory.isFull(), x -> x.getId() == ItemID.BRONZE_PICKAXE && Inventory.count(ItemID.BRONZE_PICKAXE) == 1)
                        .setSimpleName("Bank"),

                new BankAllItems(
                        ItemID.CLAY, ItemID.COPPER_ORE, ItemID.TIN_ORE, ItemID.IRON_ORE, ItemID.COAL,
                        ItemID.MITHRIL_ORE, ItemID.ADAMANTITE_ORE, ItemID.RUNITE_ORE, ItemID.GOLD_ORE,
                        ItemID.SILVER_ORE
                ).setSimpleName("Safeguard banking"),

                new DropAllItems(() -> !bank && Inventory.isFull(),
                        x -> !x.getName().toLowerCase().contains("pickaxe")).setSimpleName("Drop all"),

                new SleepFractal(() -> location.contains(Players.getLocal()) && Players.getLocal().isAnimating()).setSimpleName("Idle"),
                new GenericEntityInteraction(() -> true, () -> GameObjects.closest(this.rockFilter))
                        .setEntityLocation(location)
                        .addInventoryItem(Items.BRONZE_PICKAXE_STORE)
                        .setSimpleName("Mine")
        );
    }
}

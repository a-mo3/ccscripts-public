package org.dreambot.behaviour.fishing;

import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.loadouts.InventoryLoadout;
import org.dreambot.loadouts.data.Items;

import java.util.function.Supplier;

public enum FishingMode {
    SMALL_NET(x -> "Fishing spot".equals(x.getName()), () -> new InventoryLoadout()
            .addItem(Items.SMALL_FISHING_NET)
    ),
    BAIT(x -> "Rod Fishing spot".equals(x.getName()),
            () -> new InventoryLoadout()
                    .addItem(Items.FISHING_ROD)
                    .addItem(Items.FISHING_BAIT.setInventoryMin(1).setInventoryMax(100))
    ),
    CAGE(x -> "Fishing spot".equals(x.getName()),
            () -> new InventoryLoadout()
    ),
    // fly mode
    FLY(x -> "Rod Fishing spot".equals(x.getName()),
            () -> new InventoryLoadout()
                    .addItem(Items.FLY_FISHING_ROD)
                    .addItem(Items.FEATHERS_KILL_CHICKEN.setInventoryMin(1).setInventoryMax(1_000))
    ),
    ;

    // final String action;
    final Filter<NPC> spotFilter;
    // supplier because the coin method uses a fishing dto and this prevents stack overflow
    final Supplier<InventoryLoadout> loadout;

    FishingMode(Filter<NPC> spotFilter, Supplier<InventoryLoadout> loadout) {
        this.spotFilter = spotFilter;
        this.loadout = loadout;
    }
}

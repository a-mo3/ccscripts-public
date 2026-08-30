package org.dreambot.behaviour.training;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class CannonGiants extends Fractal {
    Tile SAFE_SPOT = new Tile(1444, 3624);
    Tile CANNON_SPOT = new Tile(1444, 3620);
    Area HILL_GIANT_AREA = new Area(1435, 3602, 1453, 3626);

    public CannonGiants(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.SHARK, 1, 4)
                .addItem(ItemID.CANNONBALL, 5, 2000)
                .addItem(ItemID.CANNON_BASE)
                .setEnabledCondition(() -> PlayerSettings.getConfig(2) < 1)
                .addItem(ItemID.CANNON_STAND)
                .setEnabledCondition(() -> PlayerSettings.getConfig(2) < 2)
                .addItem(ItemID.CANNON_BARRELS)
                .setEnabledCondition(() -> PlayerSettings.getConfig(2) < 3)
                .addItem(ItemID.CANNON_FURNACE)
                .setEnabledCondition(() -> PlayerSettings.getConfig(2) < 4)
        ;
    }

    @Override
    public int onLoop() {
        if (!HILL_GIANT_AREA.contains(Players.getLocal())) {
            if (Walking.shouldWalk(8)) Walking.walk(SAFE_SPOT);
            return ReactionGenerator.getNormal();
        }

        if (PlayerSettings.getConfig(2) < 4) {
            if (PlayerSettings.getConfig(2) >= 1) {
                // todo if not fully constructed, fully construct
                return ReactionGenerator.getNormal();
            }

            if (!CANNON_SPOT.equals(Players.getLocal().getTile())) {
                Walking.walkExact(CANNON_SPOT);
                return ReactionGenerator.getNormal();
            }

            Inventory.interact(ItemID.CANNON_BASE);
            Sleep.sleepUntil(() -> PlayerSettings.getConfig(2) == 4, 2400);
            return ReactionGenerator.getNormal();
        }

        // todo restock cannon
        if (PlayerSettings.getConfig(3) < 5) {
            
        }

        if (!SAFE_SPOT.equals(Players.getLocal().getTile())) {
            Walking.walkExact(SAFE_SPOT);
            return ReactionGenerator.getNormal();
        }

        return ReactionGenerator.getNormal();
    }
}

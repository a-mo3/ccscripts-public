package org.dreambot.behaviour.training.firemaking;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Supplier;

public class BurnLogs extends Fractal {
    final int restockQuantity;
    final int logID;
    Area FIREMAKING_AREA = new Area(3169, 3432, 3199, 3428);
    Area FIREMAKING_START = new Area(3171, 3431, 3175, 3429);

    public BurnLogs(Supplier<Boolean> acceptCondition, int restockQuantity, int logID) {
        super(acceptCondition);
        this.restockQuantity = restockQuantity;
        this.logID = logID;


        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.TINDERBOX)
                .addItem(logID, 1, 27)
                .setRefill(27);
    }

    @Override
    public int onLoop() {
        if (!FIREMAKING_AREA.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(FIREMAKING_START);
            return ReactionGenerator.getNormal();
        }

        Tile empty = Arrays.stream(Players.getLocal().getSurroundingArea(3).getTiles())
                .filter(x -> GameObjects.getTopObjectOnTile(x) == null)
                .filter(x -> FIREMAKING_AREA.contains(x))
                .min(Comparator.comparingDouble(Tile::distance))
                .orElse(null);

        if (empty != null && !Players.getLocal().getTile().equals(empty)) {
            Logger.info("Walking to empty tile " + Players.getLocal().getTile() + " | " + empty);
            if (Walking.shouldWalk()) Walking.walk(empty);
            return ReactionGenerator.getNormal();
        }

        Inventory.combine(ItemID.TINDERBOX, logID);
        Sleep.sleepUntil(() -> !Inventory.contains(logID), () -> Players.getLocal().isAnimating(), 800, 100);
        return ReactionGenerator.getNormal();
    }
}

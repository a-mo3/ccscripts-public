package org.dreambot.behaviour.method.huey;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.GraphicsObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.graphics.GraphicsObject;
import org.dreambot.api.wrappers.interactive.Locatable;
import org.dreambot.fractals.TickDecision;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class HueyLightningWatch extends TickDecision {
    private static HueyLightningWatch instance = null;

    private HueyLightningWatch() {
    }

    public static HueyLightningWatch getInstance() {
        if (instance == null) instance = new HueyLightningWatch();
        return instance;
    }

    boolean lastTickHadLightning = false;
    public static int dodgeTick = Integer.MAX_VALUE;

    @Override
    public boolean evaluate() {
        if (Client.getGameTick() == dodgeTick -1 || Client.getGameTick() == dodgeTick) {
            Map<Tile, GraphicsObject> lightningTile = new HashMap<>();
            for (GraphicsObject obj : GraphicsObjects.all(HueyData.LIGHTNING_GRAPHICS_OBJ_ID)) {
                lightningTile.put(obj.getTile(), obj);
            }

            if (!lightningTile.containsKey(Players.getLocal().getServerTile()) && Walking.getDestination() == null) {
                log("Not on lightning & not walking we're fine");
                return true;
            }

            if (Walking.getDestination() != null && !lightningTile.containsKey(Walking.getDestination())) {
                log("We're on the move to non lightning tile we're fine");
                return true;
            }

            // dodge to the closest tile to you
            // otherwise just get on the nearest avoidable tile
            log("Dodge lightning");
            Tile best = Arrays.stream(Players.getLocal().getTile().getArea(2).getTiles())
                    .filter(Locatable::canReach)
                    .filter(x -> !lightningTile.containsKey(x))
                    .min(Comparator.comparingDouble(Tile::distance))
                    .orElse(null);
            if (best == null) {
                log("Still cant dodge we're toast.");
                return false;
            }

            if (!best.equals(Walking.getDestination())) Walking.walkExact(best);
            return true;
        }

        boolean isLightning = !GraphicsObjects.all(HueyData.LIGHTNING_GRAPHICS_OBJ_ID).isEmpty();
        if (!lastTickHadLightning && isLightning) {
            dodgeTick = Client.getGameTick() + 2;
        }
        lastTickHadLightning = isLightning;
        return false;
    }
}

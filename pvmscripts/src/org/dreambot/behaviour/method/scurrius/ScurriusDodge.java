package org.dreambot.behaviour.method.scurrius;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.GraphicsObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.graphics.GraphicsObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PVMUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ScurriusDodge extends TickDecision {
    public ScurriusDodge() {
        setSimpleName("Scurrius dodge");
        GameObjects.setIncludeNullNames(true);
    }

    // graphic object for ruble falling
    public static final int SCURR_RUBLE = 2644;

    @Override
    public boolean evaluate() {
        NPC scurr = NPCs.closest("Scurrius");
        if (scurr == null) {
            log("Null scurr");
            return false;
        }
        Tile[] attackCalvTiles = PVMUtil.attackableTiles(scurr, 2);

        Map<Tile, GraphicsObject> rubleTiles = new HashMap<>();
        for (GraphicsObject obj : GraphicsObjects.all(SCURR_RUBLE)) {
            rubleTiles.put(obj.getTile(), obj);
        }

        if (!rubleTiles.containsKey(Players.getLocal().getTile())) {
            log("Not on a ruble tile");
            return false;
        }

        Tile safeTile = Arrays.stream(attackCalvTiles)
                .filter(t -> !rubleTiles.containsKey(t))
                .min(Comparator.comparingDouble(Tile::distance))
                .orElse(null);
        if (safeTile == null) {
            return false;
        }

        if (!safeTile.equals(Players.getLocal().getServerTile())) {
            Walking.walkExact(safeTile);
        }
        return false;
    }
}

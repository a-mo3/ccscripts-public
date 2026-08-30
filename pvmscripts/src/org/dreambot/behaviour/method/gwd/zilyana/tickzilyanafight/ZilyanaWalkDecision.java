package org.dreambot.behaviour.method.gwd.zilyana.tickzilyanafight;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.HashMap;

/**
 * Handles walking away from zilyana
 */
public class ZilyanaWalkDecision extends TickDecision {
    HashMap<Tile, Tile> fightTiles = new HashMap<>();

    public ZilyanaWalkDecision() {
        fightTiles.put(t1, t2);
        fightTiles.put(t2, t3);
        fightTiles.put(t3, t4);
        fightTiles.put(t4, t1);
        setSimpleName("Zilyana walk");
    }

    public static final Tile t1 = new Tile(2907, 5258);
    public static final Tile t2 = new Tile(2889, 5258);
    public static final Tile t3 = new Tile(2889, 5275);
    public static final Tile t4 = new Tile(2907, 5275);

    public static Tile currentTileTarget = t1;

    @Override
    public boolean evaluate() {
        NPC zil = NPCs.closest("Commander Zilyana");
        NPC starlight = NPCs.closest("Starlight");
        if (zil == null && starlight == null) {
            log("No walking, neither starlight or zil are alive.");
            return false;
        }

        if (!Walking.isRunEnabled()) Walking.toggleRun();
        Tile walkingDest = Walking.getDestination();
        Tile playerServerTile = Players.getLocal().getServerTile();
        if (fightTiles.containsKey(playerServerTile)) {
            log("Next Corner " + playerServerTile);
            currentTileTarget = fightTiles.get(playerServerTile);
        } else {
            log("Not on corner");
        }

        if (walkingDest == null) {
            log("Walk");
            Walking.walkExact(currentTileTarget);
            return true;
        }

        if (Inventory.contains(ItemID.VIAL, ItemID.PIE_DISH)) {
            log("drop vials n waste");
            Inventory.dropAll(ItemID.PIE_DISH, ItemID.VIAL);
        }
        return true;
    }
}

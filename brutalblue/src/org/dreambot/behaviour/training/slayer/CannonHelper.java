package org.dreambot.behaviour.training.slayer;

import lombok.Getter;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.List;

public class CannonHelper implements SpawnListener {
    @Getter
    private static Tile cannonTile = null;
    @Getter
    private static final Timer cannonPlacementTimer = new Timer(20 * 60 * 1000);

    public static int getAmmo() {
        return PlayerSettings.getConfig(4);
    }

    public static int getDownState() {
        return PlayerSettings.getConfig(2);
    }

    // decay message:
    // Your cannon has decayed, Speak to Nuludion to get a new one!

    public static boolean placeCannonDown(Tile tile) {
        if (getDownState() >= 4) return false; // cannon is fully placed
        if (tile.distance() > 10) return false;
        GameObject topObjectOnCannonTile = GameObjects.getTopObjectOnTile(tile);
        if (topObjectOnCannonTile != null && topObjectOnCannonTile.getName().toLowerCase().contains("cannon")) return false; // need empty space

        // todo check area

        // if you have base, walk to tile & place the base
        if (Inventory.contains(ItemID.CANNON_BASE)) {
            if (!tile.equals(Players.getLocal().getTile())) {
                Logger.info("Walking onto the cannon tile");
                if (Walking.shouldWalk()) Walking.walk(tile);
                return true;
            }

            Inventory.interact(ItemID.CANNON_BASE);
            Sleep.sleepUntil(() -> !Inventory.contains(ItemID.CANNON_BASE, ItemID.CANNON_STAND), 4000);
            return true;
        }

        // if you have the other pieces
        if (Inventory.contains(ItemID.CANNON_BARRELS, ItemID.CANNON_FURNACE, ItemID.CANNON_STAND)) {
            if (topObjectOnCannonTile != null && topObjectOnCannonTile.getName().toLowerCase().contains("cannon")) {
                // todo build rest of cannon, handle someone elses cannon
            }
        }

        // you have no pieces
        return false;
    }

    List<Integer> cannonIds = Arrays.asList(7, 8, 9, 6);
    @Override
    public void onGameObjectSpawn(GameObject object) {
        // check varbits and shit here
        if (!cannonIds.contains(object.getID())) return;
        if (Players.getLocal().getAnimation() != 827) {
            Logger.info("Not doing correct animation to track cannon");
            return;
        }
        Logger.info(String.format("Object spawned: %s, %d, Player Animation %d",
                object.getName(), object.getID(), Players.getLocal().getAnimation()
        ));
        // sure enough this is our cannon
        cannonTile = object.getTile();
        cannonPlacementTimer.reset();
    }

    @Override
    public void onGameObjectDespawn(GameObject object) {
        if (cannonTile != null && cannonTile.equals(object.getTile())) {
            Logger.info("Cannon on our tile despawned");
            cannonTile = null;
        }
        Logger.info(String.format("Object despawned: %s, %d, Player Animation %d",
                object.getName(), object.getID(), Players.getLocal().getAnimation()
        ));
    }
}

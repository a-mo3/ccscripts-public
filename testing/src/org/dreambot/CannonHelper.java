package org.dreambot;

import lombok.Getter;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;

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

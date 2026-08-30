package org.dreambot.behaviour.training.slayer;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;

public class CannonHelper {
    static Tile cannonTile = null;

    public static int getAmmo() {
        return PlayerSettings.getConfig(4);
    }

    public static int getDownState() {
        return PlayerSettings.getConfig(2);
    }

    // collect cannon if cannon is down and not on the right tile

    // place cannon somewhere, if already down on another go get that shit
}
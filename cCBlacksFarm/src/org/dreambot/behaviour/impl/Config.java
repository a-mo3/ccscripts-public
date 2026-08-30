package org.dreambot.behaviour.impl;

import lombok.Data;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.HashMap;
import java.util.Map;

@Data
public class Config {
    private Config() {
    }

    private static final Config config = new Config();
    private final Map<Tile, GameObject> trapMap = new HashMap<>();
    private String subStatus = "";
    private boolean failSafe = false;

    public static Config getConfig() {
        return config;
    }

    public void trapMapPut(Tile tile, GameObject object) {
        trapMap.put(tile, object);
    }

    public void trapMapRemove(Tile tile) {
        trapMap.remove(tile);
    }
}

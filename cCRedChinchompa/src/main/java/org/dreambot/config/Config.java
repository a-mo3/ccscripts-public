package org.dreambot.config;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.HashMap;
import java.util.Map;

public class Config {
    private static final Config config = new Config();
    private static final Map<Tile, GameObject> trapMap = new HashMap<>();
    private String subStatus = "";
    private int redChinCount = 0;
    private boolean failSafe = false;

    public static Config getConfig() {
        return config;
    }

    public Map<Tile, GameObject> getTrapMap() {
        return trapMap;
    }

    public void trapMapPut(GameObject object) {
        trapMap.put(object.getTile(), object);
    }

    public void trapMapPop(GameObject object) {
        trapMap.remove(object.getTile());
    }

    public String getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(String subStatus) {
        this.subStatus = subStatus;
    }

    public int getRedChinCount() {
        return redChinCount;
    }

    public void setRedChinCount(int redChinCount) {
        this.redChinCount = redChinCount;
    }

    public boolean isFailSafe() {
        return failSafe;
    }

    public void setFailSafe(boolean failSafe) {
        this.failSafe = failSafe;
    }
}

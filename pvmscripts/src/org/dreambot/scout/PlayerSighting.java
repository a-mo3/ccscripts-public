package org.dreambot.scout;

import com.google.gson.Gson;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;

/**
 * model in pano 5
 */
public class PlayerSighting {
    static Gson gson = new Gson();
    public PlayerSighting(Player p) {
        Logger.info("Player sighting " + p);
        username = p.getName();
        world = Worlds.getCurrentWorld();
        time = System.currentTimeMillis();
        region = p.getRegionId();
        equipment = p.getEquipment().stream().mapToInt(Item::getId).toArray();
        appearance = p.getAppearance();
        colors = p.getBodyColors();
    }

    private final String username;
    private final int world;
    private final long time;
    private final int region;
    private final int[] equipment;
    private final int[] appearance;
    private final int[] colors;

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}

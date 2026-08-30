package org.dreambot.behaviour.dragons;

import lombok.Setter;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.data.NpcID;

public class LocationConfig {
    private static Tile DEFAULT_SAFE_TILE = new Tile(3212, 3852);
    @Setter
    static DragonLocation location = new DragonLocation(DEFAULT_SAFE_TILE,
            () -> NPCs.closest(x -> x.getID() == NpcID.LAVA_DRAGON && x.distance() < 8),
            "Default location");

    public static Tile getSafeTile() {
        return location.getSafeTile();
    }

    public static NPC getDragon() {
        return location.dragonSupplier.get();
    }

    public static String getName() {
        return location.getName();
    }
}

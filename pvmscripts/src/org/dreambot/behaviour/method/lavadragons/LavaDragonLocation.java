package org.dreambot.behaviour.method.lavadragons;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.data.NpcID;

import java.util.function.Supplier;

public enum LavaDragonLocation {
    MIDDLE_WEST(
            new Tile(3188, 3843, 0),
            () -> NPCs.closest(x -> x.getId() == NpcID.LAVA_DRAGON && x.distance() < 12),
            "Middle West-side"
    ),
    BOTTOM_WEST(
            new Tile(3184, 3813, 0),
            () -> NPCs.closest(x -> x.getId() == NpcID.LAVA_DRAGON && x.distance() < 8),
            "Bottom West-side"
    ),
    MIDDLE_EAST(
            new Tile(3215, 3835, 0),
            () -> NPCs.closest(x -> x.getId() == NpcID.LAVA_DRAGON && LavaDragonConst.MID_DRAGON_AREA.contains(x)),
            "Middle East-side"
    );
    final Tile safeTile;
    final Supplier<NPC> dragonSupplier;
    final String name;

    LavaDragonLocation(Tile safeTile, Supplier<NPC> dragonSupplier, String name) {
        this.safeTile = safeTile;
        this.dragonSupplier = dragonSupplier;
        this.name = name;
    }
}

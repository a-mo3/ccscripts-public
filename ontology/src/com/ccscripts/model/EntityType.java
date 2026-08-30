package com.ccscripts.model;

import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;

public enum EntityType {
    GAME_OBJECT,
    NPC,
    PLAYER,
    GROUND_ITEM,
    UNKNOWN,
    ;

    public static EntityType findType(Entity entity) {
        if (entity instanceof GameObject) return GAME_OBJECT;
        if (entity instanceof NPC) return NPC;
        if (entity instanceof GroundItem) return GROUND_ITEM;
        if (entity instanceof Player) return PLAYER;
        return UNKNOWN;
    }
}

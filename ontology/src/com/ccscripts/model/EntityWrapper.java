package com.ccscripts.model;

import org.dreambot.api.wrappers.interactive.Entity;

/**
 * aids in the serialization of the entity hovered at the end of a mouse path
 */
public class EntityWrapper {
    final EntityType type;
    final String name;
    final TileWrapper tile;
    final int id;


    public EntityWrapper(Entity e) {
        this.type = EntityType.findType(e);
        this.name = e.getName();
        this.tile = new TileWrapper(e.getTile());
        this.id = e.getId();
    }
}

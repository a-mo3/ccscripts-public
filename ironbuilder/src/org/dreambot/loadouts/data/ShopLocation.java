package org.dreambot.loadouts.data;

import lombok.Getter;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.NPC;

@Getter
public enum ShopLocation {
    NURMOF_PICKAXES(new Area(2989, 9849, 3004, 9836), x -> "Nurmof".equals(x.getName())),
    LUM_GENERAL(new Tile(3211, 3246).getArea(8), x -> x.getName().contains("Shop")),
    GERRANTS_FISHY_BUSINESS( new Area(3011, 3229, 3017, 3220), x -> "Gerrant".equals(x.getName())),
    BETTYS_MAGIC_EMPORIUM(new Area(3011, 3261, 3016, 3256), x -> "Betty".equals(x.getName())),
    ROMMIK_CRAFTY_SUPPLIES(new Area(2946, 3208, 2952, 3202), x -> "Rommik".equals(x.getName()))
    ;

    final Area storeLocation;
    final Filter<NPC> npcFilter;

    ShopLocation(Area storeLocation, Filter<NPC> npc) {
        this.storeLocation = storeLocation;
        this.npcFilter = npc;
    }
}

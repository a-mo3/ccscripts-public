package org.dreambot.behaviour.quests.earnestthechicken;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;

public class ChickenNode {
    private static ChickenNode instance;

    private ChickenNode() {
        // ernest the chicken webnode
        WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));
    }

    public static ChickenNode getInstance() {
        instance = new ChickenNode();
        return instance;
    }
}

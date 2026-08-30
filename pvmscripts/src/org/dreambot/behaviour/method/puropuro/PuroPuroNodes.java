package org.dreambot.behaviour.method.puropuro;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;

public class PuroPuroNodes {
    private static PuroPuroNodes instance;
    private PuroPuroNodes() {
        WebFinder wf = WebFinder.getWebFinder();
        for (Tile t : path) {
            wf.createAndAddNode(t);
        }
        // connect circle
        AbstractWebNode node = wf.getNearest(new BasicWebNode(2592, 4347, 0));
        node.addDualConnections(wf.getNearest(new BasicWebNode(2585, 4347, 0)));
    }

    public static void init() {
        if (instance == null) {
            instance = new PuroPuroNodes();
        }
    }

    Tile[] path = {
            new Tile(2592, 4325, 0),
            new Tile(2592, 4329, 0),
            new Tile(2593, 4334, 0),
            new Tile(2594, 4340, 0),
            new Tile(2595, 4344, 0),
            new Tile(2596, 4347, 0),
            new Tile(2602, 4347, 0),
            new Tile(2610, 4347, 0),
            new Tile(2618, 4346, 0),
            new Tile(2620, 4342, 0),
            new Tile(2620, 4336, 0),
            new Tile(2620, 4330, 0),
            new Tile(2620, 4323, 0),
            new Tile(2620, 4317, 0),
            new Tile(2620, 4311, 0),
            new Tile(2620, 4302, 0),
            new Tile(2619, 4295, 0),
            new Tile(2619, 4291, 0),
            new Tile(2610, 4291, 0),
            new Tile(2602, 4291, 0),
            new Tile(2595, 4291, 0),
            new Tile(2587, 4291, 0),
            new Tile(2580, 4291, 0),
            new Tile(2570, 4291, 0),
            new Tile(2564, 4291, 0),
            new Tile(2564, 4297, 0),
            new Tile(2564, 4306, 0),
            new Tile(2564, 4312, 0),
            new Tile(2564, 4318, 0),
            new Tile(2564, 4325, 0),
            new Tile(2564, 4332, 0),
            new Tile(2564, 4340, 0),
            new Tile(2564, 4347, 0),
            new Tile(2571, 4347, 0),
            new Tile(2580, 4347, 0),
            new Tile(2585, 4347, 0),
            new Tile(2592, 4347, 0)
    };
}

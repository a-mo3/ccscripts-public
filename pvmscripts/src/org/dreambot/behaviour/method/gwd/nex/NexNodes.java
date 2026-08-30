package org.dreambot.behaviour.method.gwd.nex;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;

import java.util.Arrays;

public class NexNodes {
    private static NexNodes instance;

    private NexNodes() {
        WebFinder wf = WebFinder.getWebFinder();
        // door one
        EntranceWebNode en = new EntranceWebNode(2883, 5279, 2, "Frozen Door", "Open");
        wf.getNearest(en, 45).addDualConnections(en);

        // door on ancient prison side
        EntranceWebNode enAp = new EntranceWebNode(2856, 5228, 0, "Frozen Door", "Open");
        en.addDualConnections(enAp);

        BasicWebNode bwn = new BasicWebNode(2856, 5220, 0);
        enAp.addDualConnections(bwn);


        // path so gate
        Tile[] path = {
                new Tile(2855, 5225, 0),
                new Tile(2857, 5219, 0),
                new Tile(2864, 5219, 0),
                new Tile(2868, 5218, 0),
                new Tile(2873, 5219, 0),
                new Tile(2877, 5220, 0),
                new Tile(2879, 5215, 0),
                new Tile(2883, 5210, 0),
                new Tile(2888, 5207, 0),
                new Tile(2894, 5204, 0),
                new Tile(2903, 5203, 0)
        };

        wf.addWebNodes(en, enAp, bwn);
        Arrays.stream(path).forEach(wf::createAndAddNode);
    }

    public static void init() {
        if (instance == null) instance = new NexNodes();
    }
}

package org.dreambot.behaviour.quests.ascentofarceuus;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;

public class ArceuusNodes {
    private static ArceuusNodes instance;

    private ArceuusNodes() {
        WebFinder wf = WebFinder.getWebFinder();
        EntranceWebNode stairs = new EntranceWebNode(1584, 3820, 0, "Stairs", "Climb");
        stairs.addDualConnections(wf.getNearest(stairs));
        EntranceWebNode topStairs = new EntranceWebNode(1581, 3820, 1, "Stairs", "Climb");
        topStairs.addDualConnections(stairs);
        BasicWebNode basic = new BasicWebNode(1579, 3820, 1);
        basic.addDualConnections(topStairs);

        wf.addWebNodes(topStairs, stairs, basic);

        // todo if this is mixed with slayer branch it may cause issues
        EntranceWebNode konarElvator = new EntranceWebNode(new Tile(1311, 3807), "Elevator", "Activate");
        EntranceWebNode konarExit = new EntranceWebNode(new Tile(1311, 10185), "Cave exit", "Exit");
        konarExit.addDualConnections(konarElvator);
        konarElvator.addDualConnections(wf.getNearest(konarElvator));
        konarExit.addDualConnections(wf.getNearest(konarExit, 15));
        wf.addWebNodes(konarElvator, konarExit);

    }

    public static void init() {
        if (instance == null) instance = new ArceuusNodes();
    }
}

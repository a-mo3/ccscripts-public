package org.dreambot.behaviour.quests.pip;

import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.CustomWebPath;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;

public class PipNodes {
    private static PipNodes instance;

    public static void init() {
        if (instance == null) instance = new PipNodes();
    }

    private PipNodes() {
        // add nodes
        WebFinder wf = WebFinder.getWebFinder();
        BasicWebNode groundFloor = new BasicWebNode(3412, 3489);
        wf.getNearest(groundFloor, 20).addDualConnections(groundFloor);

        EntranceWebNode groundFloorStairs = new EntranceWebNode(3419, 3484, 0, "Staircase", "Climb-up");
        EntranceWebNode secondToFirstFloorStairs = new EntranceWebNode(3417, 3485, 1, "Staircase", "Climb-down");

        BasicWebNode secondFloor = new BasicWebNode(3413, 3487, 1);
        EntranceWebNode ladderUp = new EntranceWebNode(3410, 3485, 1, "Ladder", "Climb-up");
        EntranceWebNode ladderDown = new EntranceWebNode(3410, 3485, 2, "Ladder", "Climb-down");

        BasicWebNode topFloorBasic = new BasicWebNode(3411, 3489, 2);

        wf.addCustomWebPath(new CustomWebPath(
                groundFloor,
                groundFloorStairs,
                secondToFirstFloorStairs,

                secondFloor,

                ladderUp,
                ladderDown,
                topFloorBasic
        ));
    }

}

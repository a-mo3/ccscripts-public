package org.dreambot.behaviour.method.mta;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.WebNodeType;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.mta.alchemy.AlchemyRoomMTA;
import org.dreambot.behaviour.method.mta.enchant.EnchantRoomMTA;
import org.dreambot.behaviour.method.mta.graveyard.GraveyardRoomMTA;
import org.dreambot.behaviour.method.mta.telekinetic.TelekenticSolutions;
import org.dreambot.behaviour.method.mta.telekinetic.TelekineticNodeManager;
import org.dreambot.fractals.events.AbstractResponseEvent;

public class MTANodes {
    private static MTANodes instance;

    private MTANodes() {
        // nodes into MTA, ground floor
//        Tile[] path = {
//                new Tile(3363, 3297, 0),
//                new Tile(3363, 3300, 0),
//                new Tile(3363, 3303, 0),
//                new Tile(3363, 3307, 0),
//                new Tile(3363, 3311, 0),
//                new Tile(3363, 3315, 0),
//                new Tile(3363, 3319, 0)
//        };
//

        AbstractResponseEvent.addGlobalExitCondition(() -> {
            Player lp = Players.getLocal();
            TelekineticNodeManager.manage();
            if (EnchantRoomMTA.ENCHANT_ARENA.contains(lp)
                    || (Client.isDynamicRegion() && TelekenticSolutions.findCurrentMaze() != null)
                    || AlchemyRoomMTA.ALCHEMY_ROOM.contains(lp)
                    || GraveyardRoomMTA.GRAVE_ROOM.contains(lp)
            ) {
                WebFinder.getWebFinder().disableWebNodeType(WebNodeType.TELEPORT_NODE);
            } else {
                WebFinder.getWebFinder().enableWebNodeType(WebNodeType.TELEPORT_NODE);
            }
            return false;
        }, "MTA_TELEPORT_NODE_MANAGEMENT");

        WebFinder wf = WebFinder.getWebFinder();
//        Arrays.stream(path).forEach(wf::createAndAddNode);

        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Doorway", "Enter"));

        // entrance node to alchemy
        EntranceWebNode alchemyEnter = new EntranceWebNode(3363, 3321, 0, "Alchemists Teleport", "Enter");
        alchemyEnter.addDualConnections(wf.getNearest(alchemyEnter, 15));
        EntranceWebNode alchemyExit = new EntranceWebNode(3362, 9623, 2, "Exit Teleport", "Enter");
        alchemyExit.addDualConnections(alchemyEnter);
        Tile t = alchemyExit.getTile().clone().translate(2, 1);
        BasicWebNode alchemyInsideBasic = new BasicWebNode(t.getX(), t.getY(), 2);
        alchemyExit.addDualConnections(alchemyInsideBasic);
        wf.addWebNodes(alchemyEnter, alchemyExit, alchemyInsideBasic);

        // enchant nodes
        EntranceWebNode enchantEnter = new EntranceWebNode(3360, 3318, 0, "Enchanters Teleport", "Enter");
        enchantEnter.addDualConnections(wf.getNearest(enchantEnter));
        EntranceWebNode enchantExit = new EntranceWebNode(3353, 9640, 0, "Exit Teleport", "Enter");
        enchantExit.addDualConnections(enchantEnter);
        BasicWebNode enchantBasic = new BasicWebNode(3362, 9640, 0);
        enchantExit.addDualConnections(enchantBasic);
        wf.addWebNodes(enchantBasic, enchantExit, enchantEnter);

//        EntranceWebNode mtaRewardStairs = new EntranceWebNode(3367, 3306, 0, "Stairs", "Climb-up");
//        mtaRewardStairs.addDualConnections(wf.getNearest(mtaRewardStairs));
//        EntranceWebNode mtaRewardStairsTop = new EntranceWebNode(3367, 3306, 1, "Stairs", "Climb-down");
//        mtaRewardStairs.addDualConnections(mtaRewardStairsTop);
//        BasicWebNode mtaRewardsBasic = new BasicWebNode(3363, 3312, 1);
//        mtaRewardsBasic.addDualConnections(mtaRewardStairsTop);
//        wf.addWebNodes(mtaRewardsBasic, mtaRewardStairsTop, mtaRewardStairs);

        // graveyard nodes
        EntranceWebNode graveyardEnter = new EntranceWebNode(3366, 3318, 0, "Graveyard Teleport", "Enter");
        graveyardEnter.addDualConnections(wf.getNearest(graveyardEnter));
        EntranceWebNode graveyardExit = new EntranceWebNode(3363, 9640, 1, "Exit Teleport", "Enter");
        graveyardExit.addDualConnections(graveyardEnter);
        BasicWebNode graveyardBasic = new BasicWebNode(3356, 9635, 1);
        graveyardExit.addDualConnections(graveyardBasic);
        wf.addWebNodes(graveyardBasic, graveyardExit, graveyardEnter);


        // telekinetic entrance only because the exits are added and removed per trip
        telekineticEntrance = new EntranceWebNode(3363, 3315, 0, "Telekinetic Teleport", "Enter");
        telekineticEntrance.addDualConnections(wf.getNearest(telekineticEntrance, 15));
        wf.addWebNodes(telekineticEntrance);
    }

    public static EntranceWebNode telekineticEntrance;

    public static void init() {
        if (instance == null) instance = new MTANodes();
    }
}

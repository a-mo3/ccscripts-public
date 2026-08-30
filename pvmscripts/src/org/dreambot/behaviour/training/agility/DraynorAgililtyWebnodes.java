package org.dreambot.behaviour.training.agility;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.AgilityWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;

// singleton so we only make the nodes once
public class DraynorAgililtyWebnodes {
    private static DraynorAgililtyWebnodes singleton;

    public static void create() {
        if (singleton == null) singleton = new DraynorAgililtyWebnodes();
    }

    private DraynorAgililtyWebnodes() {
        WebFinder wf = WebFinder.getWebFinder();
        AgilityWebNode roughWallDraynor = new AgilityWebNode(3103, 3279);
        BasicWebNode firstRoofNode = new BasicWebNode(3100, 3279, 3);
        BasicWebNode nearest = (BasicWebNode) wf.getNearest(roughWallDraynor, 10);
        roughWallDraynor.setObjectName("Rough wall");
        roughWallDraynor.setAction("Climb");
        roughWallDraynor.setLevel(1);
        connectNodes(nearest, roughWallDraynor, firstRoofNode);

        AgilityWebNode secondObstacle = new AgilityWebNode(3098, 3277, 3);
        secondObstacle.setObjectName("Tightrope");
        secondObstacle.setAction("Cross");
        secondObstacle.setLevel(1);
        BasicWebNode secondRoofNode = new BasicWebNode(3090, 3276, 3);
        connectNodes(firstRoofNode, secondObstacle, secondRoofNode);

        AgilityWebNode thirdObstacle = new AgilityWebNode(3092, 3276, 3);
        thirdObstacle.setObjectName("Tightrope");
        thirdObstacle.setAction("Cross");
        thirdObstacle.setLevel(1);
        BasicWebNode thirdRoofNode = new BasicWebNode(3092, 3266, 3);
        connectNodes(secondRoofNode, thirdObstacle, thirdRoofNode);

        AgilityWebNode fourthObstacle = new AgilityWebNode(3089, 3264, 3);
        fourthObstacle.setObjectName("Narrow wall");
        fourthObstacle.setAction("Balance");
        fourthObstacle.setLevel(1);
        BasicWebNode fourthRoofNode = new BasicWebNode(3088, 3261, 3);
        connectNodes(thirdRoofNode, fourthObstacle, fourthRoofNode);

        AgilityWebNode fifthObstacle = new AgilityWebNode(3088, 3256, 3);
        fifthObstacle.setObjectName("Wall");
        fifthObstacle.setAction("Jump-up");
        fifthObstacle.setLevel(1);
        BasicWebNode fifthRoofNode = new BasicWebNode(3089, 3255, 3);
        connectNodes(fourthRoofNode, fifthObstacle, fifthRoofNode);

        AgilityWebNode sixthObstacle = new AgilityWebNode(3095, 3255, 3);
        sixthObstacle.setObjectName("Gap");
        sixthObstacle.setAction("Jump");
        sixthObstacle.setLevel(1);
        BasicWebNode sixthRoof = new BasicWebNode(3096, 3256, 3);
        connectNodes(fifthRoofNode, sixthObstacle, sixthRoof);

        AgilityWebNode seventhObstacle = new AgilityWebNode(3102, 3261, 3);
        seventhObstacle.setObjectName("Crate");
        seventhObstacle.setAction("Climb-down");
        seventhObstacle.setLevel(1);
        BasicWebNode agilityExit = (BasicWebNode) wf.getNearest(new Tile(3103, 3261, 0), 5);
        connectNodes(sixthRoof, seventhObstacle, agilityExit);
    }

    /**
     * method so i dont have to remember the connection sequence
     *
     * @param startNode      the node before the agility obstacle
     * @param agilityWebNode the obstacle
     * @param endNode        the node after the agility obstacle
     * @return
     */
    private BasicWebNode connectNodes(BasicWebNode startNode, AgilityWebNode agilityWebNode, BasicWebNode endNode) {
        WebFinder wf = WebFinder.getWebFinder();
        startNode.addOutgoingConnections(agilityWebNode);
        agilityWebNode.addOutgoingConnections(endNode);
        wf.addWebNode(agilityWebNode);
        wf.addWebNode(endNode);
        return endNode;
    }
}

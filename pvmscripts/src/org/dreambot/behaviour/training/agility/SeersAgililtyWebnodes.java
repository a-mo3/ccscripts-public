package org.dreambot.behaviour.training.agility;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.WebNodeType;
import org.dreambot.api.methods.walking.web.node.impl.AgilityWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;

// singleton so we only make the nodes once
public class SeersAgililtyWebnodes {
    private static SeersAgililtyWebnodes singleton;

    public static void create() {
        if (singleton == null) singleton = new SeersAgililtyWebnodes();
    }

    private SeersAgililtyWebnodes() {
        WebFinder wf = WebFinder.getWebFinder();

        AgilityWebNode first = makeAgilityNode("Wall", "Climb-up", new Tile(2729, 3489));
        BasicWebNode nearest = (BasicWebNode) wf.getNodesWithin(10, first.getTile()).stream().filter(x -> x.getType() == WebNodeType.BASIC_NODE).findFirst().orElse(null);
        BasicWebNode firstRoof = new BasicWebNode(2725, 3492, 3);
        connectNodes(nearest, first, firstRoof);

        AgilityWebNode second = makeAgilityNode("Gap", "Jump", new Tile(2720, 3492, 3));
        BasicWebNode secondRoof = new BasicWebNode(2710, 3494, 2);
        connectNodes(firstRoof, second, secondRoof);

        AgilityWebNode third = makeAgilityNode("Tightrope", "Cross", new Tile(2710, 3489, 2));
        BasicWebNode thirdRoof = new BasicWebNode(2712, 3478, 2);
        connectNodes(secondRoof, third, thirdRoof);

        AgilityWebNode fourth = makeAgilityNode("Gap", "Jump", new Tile(2710, 3476, 2));
        BasicWebNode fourthRoof = new BasicWebNode(2705, 3472, 3);
        connectNodes(thirdRoof, fourth, fourthRoof);

        AgilityWebNode fifth = makeAgilityNode("Gap", "Jump", new Tile(2700, 3469, 3));
        BasicWebNode fifthRoof = new BasicWebNode(2702, 3465, 2);
        connectNodes(fourthRoof, fifth, fifthRoof);

        AgilityWebNode finalGap = makeAgilityNode("Edge", "Jump", new Tile(2703, 3461, 2));
        connectNodes(fifthRoof, finalGap, null);

    }

    private AgilityWebNode makeAgilityNode(String objectName, String action, Tile location) {
        AgilityWebNode w = new AgilityWebNode(location.getX(), location.getY(), location.getZ());
        w.setAction(action);
        w.setObjectName(objectName);
        w.setLevel(1);
        return w;
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

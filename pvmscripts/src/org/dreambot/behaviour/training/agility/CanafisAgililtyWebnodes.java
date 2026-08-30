package org.dreambot.behaviour.training.agility;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.AgilityWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;

// singleton so we only make the nodes once
public class CanafisAgililtyWebnodes {
    private static CanafisAgililtyWebnodes singleton;

    public static void create() {
        if (singleton == null) singleton = new CanafisAgililtyWebnodes();
    }

    private CanafisAgililtyWebnodes() {
        WebFinder wf = WebFinder.getWebFinder();

        AgilityWebNode first = makeAgilityNode("Tall tree", "Climb", new Tile(3505, 3489));
        BasicWebNode nearest = (BasicWebNode) wf.getNearest(first, 10);
        BasicWebNode firstRoof = new BasicWebNode(3507, 3495, 2);
        connectNodes(nearest, first, firstRoof);

        AgilityWebNode second = makeAgilityNode("Gap", "Jump", new Tile(3505, 3498, 2));
        BasicWebNode secondRoof = new BasicWebNode(3502, 3504, 2);
        connectNodes(firstRoof, second, secondRoof);

        AgilityWebNode third = makeAgilityNode("Gap", "Jump", new Tile(3496, 3504, 2));
        BasicWebNode thirdRoof = new BasicWebNode(3490, 3501, 2);
        connectNodes(secondRoof, third, thirdRoof);

        AgilityWebNode fourth = makeAgilityNode("Gap", "Jump", new Tile(3485, 3499, 2));
        BasicWebNode fourthRoof = new BasicWebNode(3479, 3499, 3);
        connectNodes(thirdRoof, fourth, fourthRoof);

        AgilityWebNode fifth = makeAgilityNode("Gap", "Jump", new Tile(3478, 3491, 3));
        BasicWebNode fifthRoof = new BasicWebNode(3478, 3486, 2);
        connectNodes(fourthRoof, fifth, fifthRoof);

        AgilityWebNode sixth = makeAgilityNode("Pole-vault", "Vault", new Tile(3480, 3483, 2));
        BasicWebNode sixthRoof = new BasicWebNode(3489, 3480, 3);
        connectNodes(fifthRoof, sixth, sixthRoof);

        AgilityWebNode seventh = makeAgilityNode("Gap", "Jump", new Tile(3503, 3476, 3));
        BasicWebNode seventhRoof = new BasicWebNode(3510, 3476, 2);
        connectNodes(sixthRoof, seventh, seventhRoof);

        AgilityWebNode eight = makeAgilityNode("Gap", "Jump", new Tile(3510, 3483, 2));
        connectNodes(seventhRoof, eight, nearest);
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

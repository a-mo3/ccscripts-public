package org.dreambot.behaviour.training.agility;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.AgilityWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;

// singleton so we only make the nodes once
public class FaladorAgililtyWebnodes {
    private static FaladorAgililtyWebnodes singleton;

    public static void create() {
        if (singleton == null) singleton = new FaladorAgililtyWebnodes();
    }

    private FaladorAgililtyWebnodes() {
        WebFinder wf = WebFinder.getWebFinder();

        AgilityWebNode first = makeAgilityNode("Rough wall", "Climb", new Tile(3036, 3341));
        BasicWebNode nearest = (BasicWebNode) wf.getNearest(first, 10);
        BasicWebNode firstRoof = new BasicWebNode(3038, 3343, 3);

        connectNodes(nearest, first, firstRoof);

        AgilityWebNode second = makeAgilityNode("Tightrope", "Cross", new Tile(3040, 3343, 3));
        BasicWebNode secondRoof = new BasicWebNode(3048, 3346, 3);
        connectNodes(firstRoof, second, secondRoof);

        AgilityWebNode third = makeAgilityNode("Hand holds", "Cross", new Tile(3050, 3350, 3));
        BasicWebNode thirdRoof = new BasicWebNode(3050, 3357, 3);
        connectNodes(secondRoof, third, thirdRoof);

        AgilityWebNode fourth = makeAgilityNode("Gap", "Jump", new Tile(3048, 3359, 3));
        BasicWebNode fourthRoof = new BasicWebNode(3047, 3364, 3);
        connectNodes(thirdRoof, fourth, fourthRoof);

        AgilityWebNode fifth = makeAgilityNode("Gap", "Jump", new Tile(3044, 3361, 3));
        BasicWebNode fifthRoof = new BasicWebNode(3037, 3362, 3);
        connectNodes(fourthRoof, fifth, fifthRoof);

        AgilityWebNode sixth = makeAgilityNode("Tightrope", "Cross", new Tile(3034, 3361, 3));
        BasicWebNode sixthRoof = new BasicWebNode(3028, 3354, 3);
        connectNodes(fifthRoof, sixth, sixthRoof);

        AgilityWebNode seventh = makeAgilityNode("Tightrope", "Cross", new Tile(3026, 3353, 3));
        BasicWebNode seventhRoof = new BasicWebNode(3020, 3353, 3);
        connectNodes(sixthRoof, seventh, seventhRoof);

        AgilityWebNode eighth = makeAgilityNode("Gap", "Jump", new Tile(3016, 3352, 3));
        BasicWebNode eighthRoof = new BasicWebNode(3017, 3347, 3);
        connectNodes(seventhRoof, eighth, eighthRoof);

        AgilityWebNode ninth = makeAgilityNode("Ledge", "Jump", new Tile(3015, 3345, 3));
        BasicWebNode ninthRoof = new BasicWebNode(3013, 3345, 3);
        connectNodes(eighthRoof, ninth, ninthRoof);

        AgilityWebNode tenth = makeAgilityNode("Ledge", "Jump", new Tile(3011, 3343, 3));
        BasicWebNode tenthRoof = new BasicWebNode(3013, 3339, 3);
        connectNodes(ninthRoof, tenth, tenthRoof);

        AgilityWebNode eleventh = makeAgilityNode("Ledge", "Jump", new Tile(3014, 3335, 3));
        BasicWebNode eleventhRoof = new BasicWebNode(3013, 3333, 3);
        connectNodes(tenthRoof, eleventh, eleventhRoof);

        AgilityWebNode twelfth = makeAgilityNode("Ledge", "Jump", new Tile(3018, 3332, 3));
        BasicWebNode twelfthRoof = new BasicWebNode(3021, 3333, 3);
        connectNodes(eleventhRoof, twelfth, twelfthRoof);

        AgilityWebNode thirteenth = makeAgilityNode("Edge", "Jump", new Tile(3025, 3332, 3));
        connectNodes(twelfthRoof, thirteenth, nearest);

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

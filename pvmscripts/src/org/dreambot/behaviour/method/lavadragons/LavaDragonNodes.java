package org.dreambot.behaviour.method.lavadragons;

import org.dreambot.api.Client;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;

public class LavaDragonNodes {
    private static LavaDragonNodes instance;

    private LavaDragonNodes() {
        // mainly just nodes for the lever
        EntranceWebNode edgevilleWildernessLever = new EntranceWebNode(
                3090, 3475, 0,
                "Lever", "Pull"
        );
        edgevilleWildernessLever.setCondition(Client::isMembers);

        EntranceWebNode wildernessEdgevilleLever = new EntranceWebNode(
                3153, 3923, 0,
                "Lever", "Edgeville"
        );
        wildernessEdgevilleLever.setActions(new String[]{"Edgeville", "Pull", "Ardougne"});
        wildernessEdgevilleLever.setCondition(Client::isMembers);

        BasicWebNode wildernessBasic = new BasicWebNode(3156, 3936, 0);

        WebFinder wf = WebFinder.getWebFinder();

        wf.addWebNode(wildernessBasic);
        edgevilleWildernessLever.addDualConnections(wildernessEdgevilleLever);
        wildernessEdgevilleLever.addDualConnections(wildernessBasic);
        wf.getNearest(edgevilleWildernessLever.getTile(), 12).addDualConnections(edgevilleWildernessLever);

        wf.addWebNode(edgevilleWildernessLever);
        wf.addWebNode(wildernessEdgevilleLever);
        wf.getNearest(edgevilleWildernessLever.getTile(), 12).addDualConnections(edgevilleWildernessLever);

        wf.addWebNode(edgevilleWildernessLever);
        wf.addWebNode(wildernessEdgevilleLever);

        AbstractWebNode webNode0 = new BasicWebNode(3158, 3950, 0);
        AbstractWebNode webNode1 = new BasicWebNode(3159, 3942, 0);
        webNode0.addDualConnections(WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15));
        WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15).addDualConnections(webNode0);
        webNode0.addDualConnections(webNode1);
        webNode1.addDualConnections(webNode0);

        AbstractWebNode[] webNodes = {webNode0, webNode1,};
        WebFinder.getWebFinder().addWebNodes(webNodes);
        webNode1.addDualConnections(wildernessBasic);
    }

    public static void init() {
        if (instance == null) instance = new LavaDragonNodes();
    }
}

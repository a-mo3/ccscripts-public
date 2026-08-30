package org.dreambot.behaviour.method.mta.telekinetic;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.method.mta.MTANodes;
import org.dreambot.behaviour.method.mta.UnlockMTA;

import java.util.ArrayList;
import java.util.List;

public class TelekineticNodeManager {
    /*
    Telekinetic room is an instance/dynamic region unlike the other rooms
    You cannot teleport outside of it.

    I could
    1. use instance walking like in sulphurs, but id need to replace all walking with it
    because i dont know when we will be walking out of it

    2. every time we enter a new telekinetic room add the entrance and basic node
    and then remove them all once we aren't in instance

    im going with option 2 and this is what this is for
     */

    static final List<AbstractWebNode> addedNodes = new ArrayList<>();

    public static void manage() {
        WebFinder wf = WebFinder.getWebFinder();
        if (Client.isDynamicRegion()) {
            if (addedNodes.isEmpty()) {
                Logger.info("Tel node mgr - add");
                GameObject exit = GameObjects.closest("Exit teleport");
                if (exit == null) {
                    Logger.info("Failed to find exit");
                    return;
                }

                if (MTANodes.telekineticEntrance == null) {
                    Logger.info("Failed to get entrance");
                    return;
                }

                EntranceWebNode tempExit = new EntranceWebNode(exit.getTile(), exit.getName(), "Enter");
                BasicWebNode tempBasic = new BasicWebNode(exit.getX() - 1, exit.getY(), exit.getZ());
                addedNodes.add(tempBasic);
                addedNodes.add(tempExit);
                tempBasic.addDualConnections(tempExit);
                MTANodes.telekineticEntrance.addDualConnections(tempExit);
                WebFinder.getWebFinder().addWebNodes(tempBasic, tempExit);
            }
        } else {
            // check for first time dialogue
            WidgetChild chatText = Widgets.get(229, 3);
            if (chatText != null && chatText.getText().contains("You must talk to the Entrance")) {
                Logger.info("Force ");
                UnlockMTA.force = true;
            }

            if (!addedNodes.isEmpty()) {
                Logger.info("Tel node mgr - clear");
                addedNodes.forEach(wf::removeNode);
                addedNodes.clear();
            }
        }
    }
}

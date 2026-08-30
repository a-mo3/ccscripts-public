package org.dreambot.behaviour.quests.perilousmoon;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;

// singleton to add all the nodes for perilous moons and boss room nodes for moons of peril.
public class PerilousMoonNodes {
    private static PerilousMoonNodes instance;

    public static void init() {
        if (instance == null) instance = new PerilousMoonNodes();
    }


    private PerilousMoonNodes() {
        WebFinder webFinder = WebFinder.getWebFinder();

        Tile[] raosRise = {
                new Tile(1435, 3109, 0),
                new Tile(1431, 3105, 0),
                new Tile(1426, 3106, 0),
                new Tile(1423, 3113, 0),
                new Tile(1415, 3116, 0),
                new Tile(1413, 3121, 0),
                new Tile(1412, 3127, 0),
                new Tile(1412, 3136, 0),
                new Tile(1411, 3141, 0),
                new Tile(1411, 3147, 0),
                new Tile(1414, 3150, 0),
                new Tile(1415, 3158, 0),
                new Tile(1414, 3163, 0),
                new Tile(1414, 3167, 0),
                new Tile(1413, 3172, 0),
                new Tile(1414, 3177, 0),
                new Tile(1414, 3183, 0),
                new Tile(1416, 3187, 0),
                new Tile(1421, 3189, 0),
                new Tile(1423, 3193, 0),
                new Tile(1427, 3194, 0),
                new Tile(1429, 3188, 0),
                new Tile(1427, 3184, 0),
                new Tile(1426, 3178, 0),
                new Tile(1426, 3173, 0),
                new Tile(1431, 3172, 0),
                new Tile(1436, 3172, 0),
                new Tile(1439, 3172, 0),
                new Tile(1441, 3168, 0),
                new Tile(1444, 3163, 0),
                new Tile(1445, 3158, 0),
                new Tile(1446, 3153, 0),
                new Tile(1446, 3146, 0),
                new Tile(1445, 3138, 0)
        };
        for (Tile t : raosRise) {
            webFinder.createAndAddNode(t);
        }


        // steambound cavern
        Tile[] steamboundpath = {
                new Tile(1481, 9672, 0),
                new Tile(1482, 9677, 0),
                new Tile(1484, 9682, 0),
                new Tile(1485, 9687, 0),
                new Tile(1483, 9692, 0),
                new Tile(1488, 9697, 0),
                new Tile(1493, 9699, 0),
                new Tile(1500, 9699, 0),
                new Tile(1508, 9699, 0),
                new Tile(1511, 9703, 0),
                new Tile(1514, 9708, 0),
                new Tile(1520, 9712, 0),
                new Tile(1520, 9704, 0),
                new Tile(1520, 9697, 0),
                new Tile(1516, 9691, 0),
                new Tile(1519, 9686, 0),
                new Tile(1520, 9680, 0),
                new Tile(1515, 9679, 0),
                new Tile(1510, 9676, 0)
        };
        for (Tile t : steamboundpath) {
            webFinder.createAndAddNode(t);
        }

        // earthbound cavern
        Tile[] earthboundPath = {
                new Tile(1400, 9716, 0),
                new Tile(1400, 9721, 0),
                new Tile(1392, 9722, 0),
                new Tile(1388, 9724, 0),
                new Tile(1383, 9726, 0),
                new Tile(1378, 9726, 0),
                new Tile(1374, 9721, 0),
                new Tile(1372, 9717, 0),
                new Tile(1370, 9713, 0),
                new Tile(1366, 9710, 0),
                new Tile(1365, 9702, 0),
                new Tile(1366, 9697, 0),
                new Tile(1368, 9692, 0),
                new Tile(1372, 9692, 0),
                new Tile(1376, 9695, 0),
                new Tile(1375, 9700, 0),
                new Tile(1376, 9705, 0),
                new Tile(1377, 9709, 0),
                new Tile(1381, 9708, 0),
                new Tile(1386, 9707, 0),
                new Tile(1392, 9706, 0),
                new Tile(1396, 9704, 0),
                new Tile(1398, 9703, 0),
                new Tile(1397, 9697, 0),
                new Tile(1396, 9691, 0),
                new Tile(1397, 9685, 0),
                new Tile(1393, 9679, 0),
                new Tile(1389, 9677, 0),
                new Tile(1382, 9677, 0),
                new Tile(1377, 9677, 0),
                new Tile(1373, 9673, 0)
        };
        for (Tile t : earthboundPath) {
            webFinder.createAndAddNode(t);
        }
        // ancient prison
        Tile[] prisonPath = {
                new Tile(1388, 9571, 0),
                new Tile(1388, 9566, 0),
                new Tile(1388, 9560, 0),
                new Tile(1383, 9560, 0),
                new Tile(1378, 9561, 0),
                new Tile(1376, 9556, 0),
                new Tile(1376, 9548, 0),
                new Tile(1372, 9545, 0),
                new Tile(1365, 9545, 0),
                new Tile(1360, 9545, 0),
                new Tile(1355, 9544, 0),
                new Tile(1355, 9552, 0),
                new Tile(1355, 9559, 0),
                new Tile(1356, 9562, 0),
                new Tile(1355, 9567, 0),
                new Tile(1355, 9575, 0),
                new Tile(1357, 9578, 0),
                new Tile(1365, 9580, 0),
                new Tile(1374, 9581, 0),
                new Tile(1377, 9584, 0),
                new Tile(1382, 9589, 0),
                new Tile(1374, 9589, 0),
                new Tile(1366, 9589, 0),
                new Tile(1360, 9589, 0),
                new Tile(1355, 9589, 0),
                new Tile(1350, 9589, 0)
        };
        for (Tile t : prisonPath) {
            webFinder.createAndAddNode(t);
        }

        // add entrances connecting to boss rooms and between rooms
        // steambound to rewards room
        EntranceWebNode steamboundToRewards = new EntranceWebNode(
                1527, 9670, 0,
                "Entrance",
                "Pass-through"
        );

        EntranceWebNode rewardsToSteambound = new EntranceWebNode(
                1512, 9597, 0,
                "Entrance",
                "Pass-through"
        );

        BasicWebNode rewardsNorthNode = new BasicWebNode(1513, 9592, 0);

        steamboundToRewards.addDualConnections(webFinder.getNearest(steamboundToRewards, 20));
        steamboundToRewards.addDualConnections(rewardsToSteambound);
        rewardsToSteambound.addDualConnections(rewardsNorthNode);
        webFinder.addWebNodes(steamboundToRewards, rewardsToSteambound, rewardsNorthNode);

        // steambound to eclipse moon

        EntranceWebNode steamboundToEclipseMoon = new EntranceWebNode(
                1509, 9673, 0,
                "Entrance",
                "Pass-through"
        );

        EntranceWebNode eclipseMoonToSteambound = new EntranceWebNode(
                1458, 9631, 0,
                "Entrance",
                "Pass-through"
        );

        BasicWebNode eclipseMoonBasicWebnode = new BasicWebNode(1466, 9632);

        steamboundToEclipseMoon.addDualConnections(webFinder.getNearest(steamboundToEclipseMoon));
        steamboundToEclipseMoon.addDualConnections(eclipseMoonToSteambound);
        eclipseMoonToSteambound.addDualConnections(eclipseMoonBasicWebnode);
        webFinder.addWebNodes(steamboundToEclipseMoon, eclipseMoonBasicWebnode, eclipseMoonToSteambound);

        // steambound to earthbound (north)
        EntranceWebNode steamBoundToEarthBound = new EntranceWebNode(
                1521, 9720, 0,
                "Entrance",
                "Pass-through"
        );

        EntranceWebNode earthBoundToSteamBound = new EntranceWebNode(
                1389, 9674, 0,
                "Entrance",
                "Pass-through"
        );

        steamBoundToEarthBound.addDualConnections(webFinder.getNearest(steamBoundToEarthBound, 15));
        earthBoundToSteamBound.addDualConnections(webFinder.getNearest(earthBoundToSteamBound, 15));

        earthBoundToSteamBound.addDualConnections(steamBoundToEarthBound);
        webFinder.addWebNodes(steamBoundToEarthBound, earthBoundToSteamBound);

        // earthbound to blue moon

        EntranceWebNode earthBoundToBlueMoon = new EntranceWebNode(
                1404, 9703, 0,
                "Entrance",
                "Pass-through"
        );

        EntranceWebNode blueMoonToEarthBound = new EntranceWebNode(
                1439, 9650, 0,
                "Entrance",
                "Pass-through"
        );

        BasicWebNode blueMoonBasic = new BasicWebNode(1440, 9658);

        earthBoundToBlueMoon.addDualConnections(webFinder.getNearest(earthBoundToBlueMoon, 15));
        blueMoonBasic.addDualConnections(blueMoonToEarthBound);
        blueMoonToEarthBound.addDualConnections(earthBoundToBlueMoon);
        webFinder.addWebNodes(earthBoundToBlueMoon, blueMoonBasic, blueMoonToEarthBound);

        // earthbound to prison
        EntranceWebNode earthBoundToPrison = new EntranceWebNode(
                1373, 9664, 0,
                "Entrance",
                "Pass-through"
        );

        EntranceWebNode prisonToEarthBound = new EntranceWebNode(
                1345, 9590, 0,
                "Entrance",
                "Pass-through"
        );
        earthBoundToPrison.addDualConnections(prisonToEarthBound);
        earthBoundToPrison.addDualConnections(webFinder.getNearest(earthBoundToPrison, 15));
        prisonToEarthBound.addDualConnections(webFinder.getNearest(prisonToEarthBound, 15));
        webFinder.addWebNodes(prisonToEarthBound, earthBoundToPrison);

        // prison to red moon
        EntranceWebNode prisonToRedMoon = new EntranceWebNode(
                1388, 9590, 0,
                "Entrance",
                "Pass-through"
        );

        EntranceWebNode redMoonToPrison = new EntranceWebNode(
                1419, 9631, 0,
                "Entrance",
                "Pass-through"
        );

        BasicWebNode redMoonBasicNode = new BasicWebNode(1413, 9632, 0);

        prisonToRedMoon.addDualConnections(webFinder.getNearest(prisonToRedMoon, 15));
        prisonToRedMoon.addDualConnections(redMoonToPrison);
        redMoonToPrison.addDualConnections(redMoonBasicNode);
        webFinder.addWebNodes(redMoonToPrison, prisonToRedMoon, redMoonBasicNode);

        // prison to reward
        EntranceWebNode prisonToRewards = new EntranceWebNode(
                1354, 9536, 0,
                "Entrance",
                "Pass-through"
        );

        EntranceWebNode rewardsToPrison = new EntranceWebNode(
                1512, 9560, 0,
                "Entrance",
                "Pass-through"
        );

        BasicWebNode rewardsSouthernNode = new BasicWebNode(1513, 9563, 0);

        prisonToRewards.addDualConnections(webFinder.getNearest(prisonToRewards, 15));
        prisonToRewards.addDualConnections(rewardsToPrison);
        rewardsToPrison.addDualConnections(rewardsSouthernNode);
        webFinder.addWebNodes(prisonToRewards, rewardsToPrison, rewardsSouthernNode);

        // link basic prison reward and steambound reward basic nodes to complete the map
        rewardsSouthernNode.addDualConnections(rewardsNorthNode);
    }
}

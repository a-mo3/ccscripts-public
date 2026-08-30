package org.dreambot.webnodes;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.CustomWebPath;
import org.dreambot.api.methods.walking.web.node.WebNodeType;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.utilities.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrollStrongholdNodes {
    private TrollStrongholdNodes() {
        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Rocks", "Climb"));
        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Prison Door", "Unlock"));

        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Arena Exit", "Open"));
        // entrance nodes to eadgars jaunt
        WebFinder wf = WebFinder.getWebFinder();

        // delete some death plateau nodes so we will walk up the secret path

        Area unwantedDeathNodes = new Area(
                new Tile(2841, 3599, 0),
                new Tile(2851, 3606, 0),
                new Tile(2862, 3607, 0),
                new Tile(2869, 3607, 0),
                new Tile(2876, 3603, 0),
                new Tile(2886, 3592, 0),
                new Tile(2885, 3580, 0),
                new Tile(2865, 3579, 0),
                new Tile(2837, 3596, 0));
        List<AbstractWebNode> death = wf.getAll().stream().filter(x -> unwantedDeathNodes.contains(x.getTile())).collect(Collectors.toList());
        death.forEach(wf::removeNode);

        EntranceWebNode eadgarCaveEntrance = new EntranceWebNode(2892, 3672, 0);
        eadgarCaveEntrance.setAction("Enter");
        eadgarCaveEntrance.setEntityName("Cave Entrance");
        wf.getNearest(eadgarCaveEntrance).addDualConnections(eadgarCaveEntrance);


        EntranceWebNode eadgarCaveExit = new EntranceWebNode(2892, 10072, 2);
        eadgarCaveExit.setAction("Exit");
        eadgarCaveExit.setEntityName("Cave Exit");
        eadgarCaveEntrance.addDualConnections(eadgarCaveExit);

        BasicWebNode eadgarCave = new BasicWebNode(2889, 10078, 2);
        eadgarCaveExit.addDualConnections(eadgarCave);
        wf.addWebNode(eadgarCaveEntrance);
        wf.addWebNode(eadgarCaveExit);
        wf.addWebNode(eadgarCave);

        // entrance from golden tree -> troll prison
//        EntranceWebNode secretDoor = new EntranceWebNode(2827, 3647, 0);
//        secretDoor.setEntityName("Secret Door");
//        secretDoor.setCondition(PaidQuest.TROLL_STRONGHOLD::isFinished);
//        secretDoor.setAction("Open");
//        wf.getNearest(secretDoor, 20).addDualConnections(secretDoor);
//
//        EntranceWebNode trollPrisonExit = new EntranceWebNode(2823, 10048, 0);
//        trollPrisonExit.setAction("Open");
//        trollPrisonExit.setEntityName("Exit");
//        secretDoor.addDualConnections(trollPrisonExit);
//
//        BasicWebNode trollPrisonBasic = new BasicWebNode(2833, 10060, 0);
//        wf.getNearest(trollPrisonBasic, 20).addDualConnections(trollPrisonBasic);
//        trollPrisonBasic.addDualConnections(trollPrisonExit);
//
//        wf.addWebNode(secretDoor);
//        wf.addWebNode(trollPrisonExit);
//        wf.addWebNode(trollPrisonBasic);

        // staircase from prison to middle level
        EntranceWebNode stairsToMiddle = (EntranceWebNode) wf.getNodesWithin(5, new Tile(2852, 10105, 0)).stream()
                .filter(x -> x.getType() == WebNodeType.ENTRANCE_NODE)
                .findFirst()
                .orElse(null);
        Logger.info("Dreambots stone node " + stairsToMiddle);
        stairsToMiddle.setEntityName("Stone Staircase");
        stairsToMiddle.setAction("Climb-up");

        EntranceWebNode middleLowerStairs = new EntranceWebNode(2852, 10107, 1);
        middleLowerStairs.setAction("Climb-down");
        middleLowerStairs.setEntityName("Stone Staircase");
        middleLowerStairs.addDualConnections(stairsToMiddle);

        // theres a prison door obstacle here that might have to be added to local walker
        BasicWebNode middleBasic = new BasicWebNode(2847, 10107, 1);
        middleBasic.addDualConnections(middleLowerStairs);

        // stairs to middle already added by dreambot
        wf.addWebNode(middleLowerStairs);
        wf.addWebNode(middleBasic);

        // middle to top floor / exit / troll generals
        EntranceWebNode middleStairs = new EntranceWebNode(2842, 10108, 1);
        middleStairs.setEntityName("Stone Staircase");
        middleStairs.setAction("Climb-up");
        middleStairs.addDualConnections(middleBasic);

        EntranceWebNode topStairs = new EntranceWebNode(2843, 10108, 2);
        topStairs.setAction("Climb-down");
        topStairs.setEntityName("Stone Staircase");
        topStairs.addDualConnections(middleStairs);

        BasicWebNode topBasic = new BasicWebNode(2839, 10102, 2);
        topBasic.addDualConnections(topStairs);

        wf.addWebNode(middleStairs);
        wf.addWebNode(topStairs);
        wf.addWebNode(topBasic);

        // exit to trollheim
        EntranceWebNode exitToTrollheim = new EntranceWebNode(2838, 10090, 2);
        exitToTrollheim.setAction("Leave");
        exitToTrollheim.setEntityName("Exit");
        exitToTrollheim.addDualConnections(topBasic);

        EntranceWebNode strongholdEntrance = new EntranceWebNode(2839, 3689, 0);
        strongholdEntrance.setAction("Enter");
        strongholdEntrance.setEntityName("Stronghold");
        Logger.info("connect stronghold entrance ");
        wf.getNearest(strongholdEntrance, 20).addDualConnections(strongholdEntrance);
        strongholdEntrance.addDualConnections(exitToTrollheim);

        wf.addWebNode(exitToTrollheim);
        wf.addWebNode(strongholdEntrance);

        // node to get you to burnmeat
        AbstractWebNode webNode0 = new BasicWebNode(2844, 10104, 1);
        AbstractWebNode webNode1 = new BasicWebNode(2839, 10100, 1);
        AbstractWebNode webNode2 = new BasicWebNode(2837, 10095, 1);
        AbstractWebNode webNode3 = new BasicWebNode(2836, 10087, 1);
        AbstractWebNode webNode4 = new BasicWebNode(2837, 10079, 1);
        AbstractWebNode webNode5 = new BasicWebNode(2836, 10070, 1);
        AbstractWebNode webNode6 = new BasicWebNode(2837, 10061, 1);
        AbstractWebNode webNode7 = new BasicWebNode(2839, 10055, 1);
        AbstractWebNode webNode8 = new BasicWebNode(2846, 10052, 1);
        webNode0.addDualConnections(WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15));
        WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15).addDualConnections(webNode0);
        webNode0.addDualConnections(webNode1);
        webNode1.addDualConnections(webNode0);
        webNode1.addDualConnections(webNode2);
        webNode2.addDualConnections(webNode1);
        webNode2.addDualConnections(webNode3);
        webNode3.addDualConnections(webNode2);
        webNode3.addDualConnections(webNode4);
        webNode4.addDualConnections(webNode3);
        webNode4.addDualConnections(webNode5);
        webNode5.addDualConnections(webNode4);
        webNode5.addDualConnections(webNode6);
        webNode6.addDualConnections(webNode5);
        webNode6.addDualConnections(webNode7);
        webNode7.addDualConnections(webNode6);
        webNode7.addDualConnections(webNode8);
        webNode8.addDualConnections(webNode7);

        AbstractWebNode[] webNodes = {webNode0, webNode1, webNode2, webNode3, webNode4, webNode5, webNode6, webNode7, webNode8,};
        WebFinder.getWebFinder().addWebNodes(webNodes);

        // nodes from burntmeat to the stockroom
        EntranceWebNode kitchenToStockroomStairs = new EntranceWebNode(2852, 10061, 1);
        kitchenToStockroomStairs.setAction("Climb-down");
        kitchenToStockroomStairs.setEntityName("Stone Staircase");
        wf.getNearest(kitchenToStockroomStairs, 25).addDualConnections(kitchenToStockroomStairs);

        EntranceWebNode stockRoomStairs = (EntranceWebNode) wf.getNodesWithin(5, new Tile(2852, 10061, 0)).stream()
                .filter(x -> x.getType() == WebNodeType.ENTRANCE_NODE)
                .findFirst()
                .orElse(null);

        stockRoomStairs.setEntityName("Stone Staircase");
        stockRoomStairs.setAction("Climb-up");
        stockRoomStairs.addDualConnections(kitchenToStockroomStairs);
        // dreambot comes with nodes for the rest of this

        // troll stronghold nodes to walk from plateau
        EntranceWebNode trollCaveEnter = new EntranceWebNode(2903, 3644, 0);
        trollCaveEnter.setEntityName("Cave Entrance");
        trollCaveEnter.setAction("Enter");
        wf.getNearest(trollCaveEnter, 15).addDualConnections(trollCaveEnter);

        EntranceWebNode trollCaveEnterInside = new EntranceWebNode(2906, 10017, 0);
        trollCaveEnterInside.setAction("Exit");
        trollCaveEnterInside.setEntityName("Cave Exit");
        trollCaveEnterInside.addDualConnections(wf.getNearest(trollCaveEnterInside, 15));
        // connect entrances
        trollCaveEnterInside.addDualConnections(trollCaveEnter);

        EntranceWebNode trollCaveExitInside = new EntranceWebNode(2906, 10036);
        trollCaveExitInside.setEntityName("Cave Exit");
        trollCaveExitInside.setAction("Exit");
        trollCaveExitInside.addDualConnections(wf.getNearest(trollCaveExitInside, 15));

        EntranceWebNode trollCaveExitOutside = new EntranceWebNode(2907, 3652);
        trollCaveExitOutside.setAction("Enter");
        trollCaveExitOutside.setEntityName("Cave Entrance");
        trollCaveExitOutside.addDualConnections(wf.getNearest(trollCaveExitOutside, 15));
        trollCaveExitOutside.addDualConnections(trollCaveExitInside);

        wf.addWebNode(trollCaveEnterInside);
        wf.addWebNode(trollCaveEnterInside);

        wf.addWebNode(trollCaveExitInside);
        wf.addWebNode(trollCaveExitOutside);

        // nodes from troll general to main corridor
        Tile[] path = {
                new Tile(2836, 10090, 2),
                new Tile(2836, 10086, 2),
                new Tile(2836, 10081, 2),
                new Tile(2836, 10075, 2),
                new Tile(2836, 10069, 2),
                new Tile(2838, 10064, 2),
                new Tile(2840, 10059, 2),
                new Tile(2840, 10057, 2),
                new Tile(2835, 10057, 2),
                new Tile(2831, 10061, 2),
                new Tile(2831, 10064, 2),
                new Tile(2831, 10070, 2),
                new Tile(2831, 10073, 2),
                new Tile(2830, 10076, 2),
                new Tile(2827, 10078, 2),
                new Tile(2825, 10084, 2)
        };
        Arrays.stream(path).forEach(wf::createAndAddNode);
    }

    private static TrollStrongholdNodes instance = null;

    public static void init() {
        if (instance != null) return;
        instance = new TrollStrongholdNodes();
    }
}

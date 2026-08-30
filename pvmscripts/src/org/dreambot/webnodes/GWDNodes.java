package org.dreambot.webnodes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;

public class GWDNodes {
    private static GWDNodes instance;

    private GWDNodes() {
        WebFinder wf = WebFinder.getWebFinder();
        Logger.info("");

        // nodes for main room are added, now nodes for each boss section

        wf.getNodesWithin(5, new Tile(2898, 3716, 0))
                .stream()
                .map(x -> (BasicWebNode) x)
                .forEach(x -> x.setValid(() -> Skills.getBoostedLevel(Skill.STRENGTH) >= 60));
        ;
        // GWD agility entrance web nodes
        PassableObstacle boulder = new PassableObstacle("Boulder", "Move");
        boulder.setCondition(() -> Skills.getBoostedLevel(Skill.STRENGTH) >= 60);
        LocalPathFinder.getLocalPathFinder().addObstacle(boulder);
        EntranceWebNode agilityEntranceGWD = new EntranceWebNode(2900, 3713, 0);
        agilityEntranceGWD.setCondition(() -> Skills.getBoostedLevel(Skill.AGILITY) >= 60);
        agilityEntranceGWD.setEntityName("Little crack");
        agilityEntranceGWD.setAction("Crawl-through");

        EntranceWebNode agilityExitGWD = new EntranceWebNode(2904, 3719, 0);
        agilityExitGWD.setCondition(() -> Skills.getBoostedLevel(Skill.AGILITY) >= 60);
        agilityExitGWD.setEntityName("Little crack");
        agilityExitGWD.setAction("Crawl-through");

        agilityExitGWD.addDualConnections(agilityEntranceGWD);
        wf.getNearest(agilityExitGWD, 5).addDualConnections(agilityExitGWD);
        wf.getNearest(new Tile(2899, 3710), 5).addDualConnections(agilityEntranceGWD);
        wf.addWebNode(agilityExitGWD);
        wf.addWebNode(agilityEntranceGWD);


        // into gwd
        EntranceWebNode intoGWDHole = new EntranceWebNode(2917, 3745, 0);
        intoGWDHole.setEntityName("Hole");
        intoGWDHole.setAction("Climb-down");

        // zilyana | saradomin
        EntranceWebNode zilRopeOneTop = new EntranceWebNode(2913, 5300, 2);
        wf.getNearest(zilRopeOneTop, 25).addDualConnections(zilRopeOneTop);
        zilRopeOneTop.setAction("Climb-down");
        zilRopeOneTop.setEntityName("Rock");

        EntranceWebNode zilRopeOneBottom = new EntranceWebNode(2914, 5300, 1);
        zilRopeOneBottom.addDualConnections(zilRopeOneTop);
        zilRopeOneBottom.setAction("Climb-up");
        zilRopeOneBottom.setEntityName("Rope");

        BasicWebNode zilOneBasic = new BasicWebNode(2918, 5288, 1);
        zilOneBasic.addDualConnections(zilRopeOneBottom);

        EntranceWebNode zilRopeTwoTop = new EntranceWebNode(2920, 5274, 1);
        zilRopeTwoTop.setAction("Climb-down");
        zilRopeTwoTop.setEntityName("Rock");
        zilOneBasic.addDualConnections(zilRopeTwoTop);

        EntranceWebNode zilRopeTwoBottom = new EntranceWebNode(2920, 5274, 0);
        zilRopeTwoBottom.setEntityName("Rope");
        zilRopeTwoBottom.setAction("Climb-up");
        zilRopeTwoTop.addDualConnections(zilRopeTwoBottom);

        BasicWebNode outsideZil = new BasicWebNode(2915, 5270);
        outsideZil.addDualConnections(zilRopeTwoBottom);

        wf.addWebNode(zilRopeOneTop);
        wf.addWebNode(zilRopeOneBottom);
        wf.addWebNode(zilOneBasic);
        wf.addWebNode(zilRopeOneBottom);
        wf.addWebNode(zilRopeTwoBottom);
        wf.addWebNode(outsideZil);

        // BANDOS NODES
        Tile[] path = {
                new Tile(2856, 5331, 2),
                new Tile(2847, 5333, 2),
                new Tile(2846, 5339, 2),
                new Tile(2849, 5347, 2),
                new Tile(2855, 5350, 2),
                new Tile(2860, 5353, 2)
        };
        Arrays.stream(path).forEach(wf::createAndAddNode);

        PassableObstacle bigDoor = new PassableObstacle("Big door", "Open");
        bigDoor.setCondition(() -> Inventory.contains(ItemID.HAMMER));
        LocalPathFinder.getLocalPathFinder().addObstacle(bigDoor);

        // ZAMMY NODES
        EntranceWebNode zammyBridgeEnter = new EntranceWebNode(new Tile(2885, 5333, 2), "Ice bridge", "Climb-off");
        wf.getNearest(zammyBridgeEnter, 20).addDualConnections(zammyBridgeEnter);
        EntranceWebNode zammyBridgeExit = new EntranceWebNode(new Tile(2885, 5344, 2), "Ice bridge", "Climb-off");
        zammyBridgeEnter.addDualConnections(zammyBridgeExit);

        BasicWebNode zamBasic = new BasicWebNode(2885, 5348, 2);
        zamBasic.addDualConnections(zammyBridgeExit);

        wf.addWebNodes(zammyBridgeEnter, zammyBridgeExit, zamBasic);


        path = new Tile[]{
                new Tile(2887, 5352, 2),
                new Tile(2893, 5353, 2),
                new Tile(2896, 5350, 2),
                new Tile(2900, 5348, 2),
                new Tile(2905, 5347, 2),
                new Tile(2908, 5344, 2),
                new Tile(2911, 5340, 2),
                new Tile(2914, 5338, 2),
                new Tile(2919, 5339, 2),
                new Tile(2925, 5338, 2)
        };
        Arrays.stream(path).forEach(wf::createAndAddNode);

//        // zammy nodes, entrance is quite far away forom nearest node
//        EntranceWebNode iceBridgeEnter = new EntranceWebNode(2885, 5333, 2, "Ice bridge", "Climb-off");
//        iceBridgeEnter.addDualConnections(wf.getNearest(iceBridgeEnter, 20));
//        EntranceWebNode iceBridgeExit = new EntranceWebNode(2885, 5344, 2, "Ice bridge", "Climb-off");
//        iceBridgeEnter.addDualConnections(iceBridgeExit);

    }

    public static void init() {
        if (instance == null) instance = new GWDNodes();
    }
}

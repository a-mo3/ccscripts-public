package org.dreambot.webnodes;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.pathaware.ShipWebNode;

import java.util.List;
import java.util.stream.Collectors;

public class KeldagrimNodes {
    private static boolean hasAdded = false;

    public static void addNodes() {
        if (hasAdded) return;

//        // entrance to troll dungeon
        WebFinder wf = WebFinder.getWebFinder();
//        List<AbstractWebNode> wns = wf.getNodesWithin(3, new Tile(2731, 3711, 0));
//        wf.removeNode(wns.get(0)); // remove node that is already there
//
//        // create entrance
//        EntranceWebNode fremTrollCaveEntrance = new EntranceWebNode(2731, 3712, 0, "Tunnel", "Enter");
//        wf.getNearest(fremTrollCaveEntrance, 10).addDualConnections(fremTrollCaveEntrance);
//
//        EntranceWebNode fremTrollCaveExit = new EntranceWebNode(2771, 10161, 0, "Tunnel", "Enter");
//        fremTrollCaveExit.addDualConnections(fremTrollCaveEntrance);
//
//        BasicWebNode fremTrollCaveBasic = new BasicWebNode(2775, 10162);
//        fremTrollCaveExit.addDualConnections(fremTrollCaveBasic);
//        wf.addWebNode(fremTrollCaveBasic);

        // keldagrim entrance
//        EntranceWebNode keldagrimEntrance = new EntranceWebNode(2781, 10161, 0, "Cave entrance", "Go-through");
//        keldagrimEntrance.addDualConnections(fremTrollCaveBasic);

//        EntranceWebNode keldagrimExit = new EntranceWebNode(2838, 10123, 0, "Entrance", "Go-through");
//        keldagrimExit.addDualConnections(keldagrimEntrance);

//        BasicWebNode keldagrimBasic = new BasicWebNode(2838, 10125, 0);
//        keldagrimBasic.addDualConnections(keldagrimExit);
//        wf.addWebNode(keldagrimBasic);


        ShipWebNode boatToKeldagrim = new ShipWebNode(new Tile(2842, 10129, 0), new Tile(2884, 10227, 0),
                "Dwarven Boatman", "Travel", () -> true, 2);

        wf.addWebNode(boatToKeldagrim);
        wf.getNearest(boatToKeldagrim, 10).addDualConnections(boatToKeldagrim);

        ShipWebNode ferry = new ShipWebNode(new Tile(2839, 10128, 0), new Tile(2836, 10146, 0),
                "Dwarven Ferryman", "Travel", () -> true, 2);

        ferry.addDualConnections(wf.getNearest(ferry));
        // todo
        wf.addWebNode(ferry);

        BasicWebNode offFerry = new BasicWebNode(2836, 10149, 0);
        ferry.addOutgoingConnections(offFerry);
        wf.addWebNode(offFerry);

        Area dondakanExistingNodes = new Area(2817, 10173, 2850, 10159);
        List<AbstractWebNode> dragonNodes = wf.getAll().stream().filter(x -> dondakanExistingNodes.contains(x.getTile())).collect(Collectors.toList());
        dragonNodes.forEach(wf::removeNode);

        wf.createAndAddNode(new Tile(2839, 10148, 0));
        wf.createAndAddNode(new Tile(2846, 10148, 0));
        wf.createAndAddNode(new Tile(2853, 10148, 0));
        wf.createAndAddNode(new Tile(2858, 10147, 0));
        wf.createAndAddNode(new Tile(2866, 10152, 0));
        wf.createAndAddNode(new Tile(2869, 10161, 0));
        wf.createAndAddNode(new Tile(2866, 10167, 0));
        wf.createAndAddNode(new Tile(2858, 10166, 0));
        wf.createAndAddNode(new Tile(2851, 10162, 0));
        wf.createAndAddNode(new Tile(2843, 10162, 0));
        wf.createAndAddNode(new Tile(2838, 10164, 0));
        wf.createAndAddNode(new Tile(2846, 10161, 0));
        wf.createAndAddNode(new Tile(2835, 10163, 0));
        wf.createAndAddNode(new Tile(2829, 10166, 0));

        // ferry to get off the donda island


        ShipWebNode ferryToGetOff = new ShipWebNode(new Tile(2856, 10144, 0), new Tile(2863, 10133, 0),
                "Dwarven Ferryman", "Travel", () -> true, 2);

        wf.getNearest(ferryToGetOff, 6).addDualConnections(ferryToGetOff);
//        wf.addWebNode(ferryToGetOff);

        BasicWebNode ferryExit = new BasicWebNode(2867, 10133, 0);
        ferryToGetOff.addOutgoingConnections(ferryExit);
        wf.addWebNode(ferryExit);

        // todo nodes from the off dondakan island ferry to the onto keldagrim boat

        wf.createAndAddNode(new Tile(2868, 10130, 0));
        wf.createAndAddNode(new Tile(2863, 10126, 0));
        wf.createAndAddNode(new Tile(2852, 10126, 0));
        wf.createAndAddNode(new Tile(2844, 10128, 0));


//        BasicWebNode keldagrimOffBoatBasic = new BasicWebNode()


        hasAdded = true;
    }
}

package org.dreambot.behaviour.method.barrows.handlecrypt;

import org.apache.log4j.spi.NOPLoggerRepository;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.util.Direction;
import org.dreambot.behaviour.method.barrows.BarrowsVarbits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * when walking, in crypt, web walker doesnt consider what doors are active
 * this causes a walking back a forth when the local and web walkers
 * here we use the active doors to decide what connections to remove from the center node
 */
public class BarrowsNodeManager {
    private static Direction getActiveDoor() {
        if (PlayerSettings.getBitValue(BarrowsVarbits.BARROWS_DOOR_NORTH) == 0) return Direction.NORTH;
        if (PlayerSettings.getBitValue(BarrowsVarbits.BARROWS_DOOR_SOUTH) == 0) return Direction.SOUTH;
        if (PlayerSettings.getBitValue(BarrowsVarbits.BARROWS_DOOR_WEST) == 0) return Direction.WEST;
        if (PlayerSettings.getBitValue(BarrowsVarbits.BARROWS_DOOR_EAST) == 0) return Direction.EAST;
        return Direction.NULL;
    }

    static Area NORTH_ROOM = new Area(3546, 9716, 3556, 9707);
    static Area EAST_ROOM = new Area(3564, 9699, 3573, 9690);
    static Area SOUTH_ROOM = new Area(3546, 9682, 3556, 9673);
    static Area WEST_ROOM = new Area(3530, 9699, 3539, 9690);


    static Map<Direction, Area> areaMap = new HashMap<>();

    static {
        areaMap.put(Direction.NORTH, NORTH_ROOM);
        areaMap.put(Direction.SOUTH, SOUTH_ROOM);
        areaMap.put(Direction.EAST, EAST_ROOM);
        areaMap.put(Direction.WEST, WEST_ROOM);
    }

    static Direction lastDirection = Direction.NULL;

    public static void manage() {
        Direction d = getActiveDoor();
        Logger.info("Dir " + d);
        if (lastDirection == d) return;

//        lastDirection = d;
//        // kind of dangerous, could fuck up a lot of plugins & training, but should only be used once ur at barrows so shouldn't
//        wf.resetWebNodes();
//
//        // detach connections to chest node from directions the doors do not open from
//        AbstractWebNode centerNode = wf.getNearest(HandleCryptBranch.BARROWS_CRYPT.getCenter(), 10);
//        if (centerNode == null) {
//            Logger.error("FAILED TO FIND BARROWS CENTER NODE");
//            return;
//        }
//
//        Logger.info("Delete others " + areaMap.get(d));
//        List<AbstractWebNode> nodes = new ArrayList<>(centerNode.getConnections());
//        for (int i = 0; i < nodes.size(); i++) {
//            if (areaMap.get(d).contains(nodes.get(i).getTile())) continue;
//            nodes.get(i).removeDualConnections(centerNode);
//        }
    }
}

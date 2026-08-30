package org.dreambot.behaviour.method.huey;

import org.dreambot.api.Client;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.GraphicsObject;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.util.Direction;
import org.dreambot.api.wrappers.map.Region;

import javax.print.DocFlavor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HueyData {
    public static Area IN_FIGHT_AREA = new Area(
            new Tile(1504, 3293, 0),
            new Tile(1512, 3295, 0),
            new Tile(1520, 3294, 0),
            new Tile(1523, 3287, 0),
            new Tile(1526, 3286, 0),
            new Tile(1535, 3286, 0),
            new Tile(1535, 3275, 0),
            new Tile(1526, 3267, 0),
            new Tile(1514, 3266, 0),
            new Tile(1507, 3271, 0),
            new Tile(1505, 3275, 0),
            new Tile(1502, 3277, 0),
            new Tile(1500, 3285, 0));

    public static boolean isInHueyFight() {
        if (PlayerSettings.getBitValue(11362) == 0) return false;
        if (Client.isDynamicRegion()) {
            return IN_FIGHT_AREA.contains(Region.fromInstance(Players.getLocal().getTile()));
        }
        return IN_FIGHT_AREA.contains(Players.getLocal());
    }

    public static boolean useBurningClaws = false;

    public static final Tile EAST_SIDE_MAGIC_SAFESPOT = new Tile(1516, 3289);
    public static final Tile WEST_SIDE_MAGIC_SAFESPOT = new Tile(1508, 3289);

    public static Tile getMagicSafespot(Direction nextWave) {
        if (nextWave == Direction.EAST) {
            if (Client.isDynamicRegion()) return Region.toInstance(EAST_SIDE_MAGIC_SAFESPOT).get(0);
            return EAST_SIDE_MAGIC_SAFESPOT;
        }
        if (Client.isDynamicRegion()) return Region.toInstance(WEST_SIDE_MAGIC_SAFESPOT).get(0);
        return WEST_SIDE_MAGIC_SAFESPOT;
    }

    public static List<Tile> getHueyAttackTiles() {
        if (Client.isDynamicRegion()) {
            return hueyAttackTiles.stream().map(x -> Region.toInstance(x).get(0)).collect(Collectors.toList());
        }
        return hueyAttackTiles;
    }

    public static final int TAIL_SLAM_ANIMATION = 11722;
    public static final int HUEY_TAIL_ANIMATION = 11676;

    public static final int HUEY_ENCOUNTERED = 11363;
    public static final int HUEY_MET_DWARF_WORKER = 11364;


    // wave heading east that you run through west
    public static final int WAVE_HEADING_EAST_GRAPHIC = 2984;
    private static final Tile WESTERN_WAVE_DODGE = new Tile(1509, 3285);

    public static Tile getWesternWaveDodge() {
        if (Client.isDynamicRegion()) {
            return Region.toInstance(WESTERN_WAVE_DODGE).get(0);
        }
        return WESTERN_WAVE_DODGE;
    }

    // opposite direction
    public static final int WAVE_HEADING_WEST_GRAPHIC = 2983;
    private static final Tile EASTERN_WAVE_DODGE = new Tile(1515, 3285);

    public static Tile getEasternWaveDodge() {
        if (Client.isDynamicRegion()) {
            return Region.toInstance(EASTERN_WAVE_DODGE).get(0);
        }
        return EASTERN_WAVE_DODGE;
    }

    private static Tile diag = new Tile(1512, 3289);
    public static Tile getDiagTile() {
        if (Client.isDynamicRegion()) {
            return Region.toInstance(diag).get(0);
        }
        return diag;
    }

    public static final List<Tile> hueyAttackTiles = Arrays.asList(
            new Tile(1513, 3289),
            new Tile(1511, 3289)
    );

    public static final int LIGHTNING_GRAPHICS_OBJ_ID = 3001;

    public static final Area HUEY_MAIN_AREA = new Area(
            new Tile(1502, 3284, 0),
            new Tile(1506, 3294, 0),
            new Tile(1518, 3294, 0),
            new Tile(1523, 3288, 0),
            new Tile(1523, 3278, 0),
            new Tile(1518, 3275, 0),
            new Tile(1504, 3276, 0)
    );

    public static final Tile NORTH_EAST = new Tile(1530, 3276, 0);
    public static final Tile NORTH_WEST = new Tile(1524, 3277, 0);
    public static final Tile NORTH_SOUTH_EAST = new Tile(1527, 3273, 0);
    public static final Tile SOUTH_WEST = new Tile(1520, 3273);
    public static final Tile SOUTH_MOST = new Tile(1524, 3270);

    // bodies tiles to the tiles where they are attackable
    public static final Map<Tile, Tile[]> tailBodyAttackableTile = new HashMap<>();
    public static List<Tile> attackableTiles;

    static {
        tailBodyAttackableTile.put(NORTH_SOUTH_EAST, new Tile[]{
                NORTH_SOUTH_EAST.clone().translate(0, 1),
                NORTH_SOUTH_EAST.clone().translate(-1, 0),
        });

        tailBodyAttackableTile.put(NORTH_EAST, new Tile[]{
                NORTH_EAST.clone().translate(-1, 0),
        });


        tailBodyAttackableTile.put(NORTH_WEST, new Tile[]{
                NORTH_WEST.clone().translate(1, 0),
        });

        tailBodyAttackableTile.put(SOUTH_MOST, new Tile[]{
                SOUTH_MOST.clone().translate(0, 1),
        });

        tailBodyAttackableTile.put(SOUTH_WEST, new Tile[]{
                SOUTH_WEST.clone().translate(0, -1),
        });

        attackableTiles = Arrays.asList(
                SOUTH_WEST.clone().translate(0, -1),
                SOUTH_MOST.clone().translate(0, 1),
                NORTH_WEST.clone().translate(1, 0),
                NORTH_EAST.clone().translate(-1, 0),
                NORTH_SOUTH_EAST.clone().translate(0, 1),
                NORTH_SOUTH_EAST.clone().translate(-1, 0)
        );
    }

    public static final Filter<GraphicsObject> isShockwave = x -> x.getId() >= 2977 && x.getId() <= 3000;

    public static boolean leaveFight() {
        // if in huey phase use quick slide
        if (NPCs.closest("Hueycoatl body") == null) {
            Logger.info("Use quick slide");
            GameObject slide = GameObjects.closest(x -> x.hasAction("Quick-slide"));
            if (slide == null) {
                Logger.info("Failed to find slide");
                return true;
            }

            slide.interact("Quick-slide");
            Sleep.sleepUntil(() -> !HueyData.isInHueyFight(), 4000);

        }

        // otherwise use chain
        GameObject chain = GameObjects.closest("Chain");
        if (chain != null) {
            Logger.info("leave huey fight by chain");
            chain.interact("Quick-climb");
        }
        return true;
    }
}

package org.dreambot.behaviour.method.spindel;

import org.dreambot.api.methods.map.Tile;

import java.util.Arrays;
import java.util.List;

public class SpindelData {
    private static final int WEB_OBJ_ID = 47084; // this is a gameobject

    private static final List<Integer> webObjIds = Arrays.asList(
            WEB_OBJ_ID,
            WEB_OBJ_ID + 1,
            WEB_OBJ_ID + 2
    );

    // the tile you land on when you enter, and where we'll be placing the web.
    public static final Tile SPINDEL_WEB_PLACEMENT = new Tile(1630, 11557, 2);

    public static boolean isWeb(int id) {
        return webObjIds.contains(id);
    }

    public static final int SPIDERLING_ID = 12001; // this is an npc

    public static final int SPINDEL_ID = 11998; // never seen this change

    public static final int RANGE_ATK_ANI = 9989;
    public static final int MAGE_ATK_ANI = 9990;
    public static final int MELEE_ATK_ANI = 9991;

    public static final int WEB_PROJECTILE = 2360;
}

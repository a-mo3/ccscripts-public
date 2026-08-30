package org.dreambot.behaviour.method.venenatis;

import org.dreambot.api.Client;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

import java.util.Arrays;
import java.util.List;

public class VenenatisData {
    private static final int WEB_OBJ_ID = 47084; // this is a gameobject

    private static final List<Integer> webObjIds = Arrays.asList(
            WEB_OBJ_ID,
            WEB_OBJ_ID + 1,
            WEB_OBJ_ID + 2
    );


    /**
     * sets the tick
     *
     * @param projTile the target of the web projectile
     */
    public static void setProspectiveWebs(Tile projTile) {
        webProjTick = Client.getGameTick();
        webLandingArea = new Area(
                new Tile(projTile.getX() - 3, projTile.getY() - 3, 2),
                new Tile(projTile.getX() + 3, projTile.getY() + 3, 2)
        );
    }

    public static Area getWebLandingArea() {
        if (Client.getGameTick() - webProjTick < 8) return webLandingArea;
        return null;
    }

    static int webProjTick = 1;
    static Area webLandingArea = null;


    public static boolean isWeb(int id) {
        return webObjIds.contains(id);
    }

    public static final String VENENATIS_NAME = "Venenatis";

    public static final int RANGE_ATK_ANI = 9989;
    public static final int MAGE_ATK_ANI = 9990;
    public static final int MELEE_ATK_ANI = 9991;

    public static final int WEB_PROJECTILE = 2360;
}

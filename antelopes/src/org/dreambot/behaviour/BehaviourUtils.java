package org.dreambot.behaviour;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;

// was some abstract bullshit before i just dont wanna move everything outt here
public abstract class BehaviourUtils {

    public static final Tile BLACK_TILE = new Tile(3152, 3769);
    public static final Area BLACK_CHINS = new Area(3150, 3771, 3155, 3766, 0);
    public static final Area FEROX_BANK = new Area(3129, 3632, 3132, 3629, 0);

    public static void stdWalk(Area area) {
        if (Walking.shouldWalk()) {
          if (Walking.shouldWalk(6)) Walking.walk(area.getCenter());
        }
    }

    // + 1 than normal because in wild
    public static int getTrapLimit() {
        int level = Skills.getRealLevel(Skill.HUNTER);
        if (level < 20) {
            return 2;
        } else if (level < 40) {
            return 3;
        } else if (level < 60) {
            return 4;
        } else if (level < 80) {
            return 5;
        } else {
            return 6;
        }
    }

    public static Tile getBestTile() {
        int x = BLACK_TILE.getX();
        int y = BLACK_TILE.getY();
        Tile[] tileSet = new Tile[]{
                BLACK_TILE,
                new Tile(x - 1, y - 1),
                new Tile(x + 1, y - 1),
                new Tile(x - 1, y + 1),
                new Tile(x + 1, y + 1),
                new Tile(x + 1, y),
                new Tile(x - 1, y),
                new Tile(x, y + 1),
                new Tile(x, y - 1)
        };

        for (Tile tile : tileSet) {
            if (GameObjects.getTopObjectOnTile(tile) == null
                    || !GameObjects.getTopObjectOnTile(tile).getName().toLowerCase().contains("box")) {
                return tile;
            }
        }
        return null;
    }
}

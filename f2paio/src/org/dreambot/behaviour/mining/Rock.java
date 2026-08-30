package org.dreambot.behaviour.mining;


import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

public enum Rock {
    CLAY(6705),
    COPPER(4645),
    TIN(53),
    IRON(2576),
    SILVER(74),
    COAL(10508),
    GOLD(8885),
    MITHRIL(-22239),
    ADAMANTITE(21662),
    ;

    public final short COLOR;

    Rock(int COLOUR) {
        this.COLOR = (short) COLOUR;
    }

    public List<GameObject> getRocksWithOre(Rock rock) {
        return GameObjects.all(obj -> {
            short[] color = obj.getModelColors();
            if (color != null) {
                for (short c : color) {

                    if (c == rock.COLOR) return true;
                }
            }
            return false;
        });
    }

    public GameObject getClosest() {
        return GameObjects.closest(gameObject -> {
            short[] colours = gameObject.getModelColors();
            if (colours != null) {
                for (short c : colours) {

                    if (c == COLOR) return true;
                }
            }
            return false;
        });
    }
}

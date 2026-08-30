package org.dreambot.behaviour.method.spindel.range;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.spindel.SpindelData;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.Comparator;

/**
 * walk away from spindel,
 * prefer closer to exit cave
 */
public class GetDistanceFromSpindel extends Fractal {

    @Override
    public boolean isValid() {
        NPC spin = NPCs.closest(SpindelData.SPINDEL_ID);
        return spin != null && spin.distance() <= 5;
    }

    @Override
    public int onLoop() {
        NPC spindel = NPCs.closest(SpindelData.SPINDEL_ID);

//        List<GraphicsObject> graphicsObjects = Client.getGraphicsObjects().stream().filter(x -> x.getId() == SpindelData.WEB_OBJ_ID).collect(Collectors.toList());
        GameObjects.setIncludeNullNames(true);

        Area spindelRadius = spindel.getTile().getArea(9);
        Tile safest = Arrays.stream(spindelRadius.getTiles())
                .filter(x -> x.distance(spindel) > 5)
//                .filter(x -> graphicsObjects.stream().noneMatch(i -> i.getTile().equals(x)))
                .filter(x -> Arrays.stream(GameObjects.getObjectsOnTile(x)).noneMatch(o -> o != null && SpindelData.isWeb(o.getId())))// make sure its not webbed
                .min(Comparator.comparingDouble(Tile::distance)) // maybe something here to prefer southern tiles
                .orElse(null);
        if (safest == null) {
            Logger.warn("Was unable to find a safe tile");
            return ReactionGenerator.getQuick();
        }

        Walking.walkExact(safest);
        return ReactionGenerator.getQuick();
    }
}

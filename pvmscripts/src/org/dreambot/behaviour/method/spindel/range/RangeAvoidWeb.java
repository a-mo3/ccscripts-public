package org.dreambot.behaviour.method.spindel.range;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.spindel.SpindelData;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Supplier;

/**
 * anticipate web shot and walk off web if you are one it
 */
public class RangeAvoidWeb extends Fractal {

    public RangeAvoidWeb(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        NPC spindel = NPCs.closest(SpindelData.SPINDEL_ID);
        GameObject web = GameObjects.closest(x -> SpindelData.isWeb(x.getId()));

        if (web == null) {
            // web attack is anticipated, run to edge of the ring
            Logger.info("meant to anticipate here");
            if (RangeSpindelBranch.webTile != null) {
                Area spindelRadius = Players.getLocal().getTile().getArea(9);
                Area anticipatedWebRadius = RangeSpindelBranch.webTile.getArea(4);
                Tile safest = Arrays.stream(spindelRadius.getTiles())
                        .filter(x -> !anticipatedWebRadius.contains(x))
                        .filter(x -> x.distance(spindel) > 5)
                        .filter(x -> x.distance(spindel) < 9)
                        .filter(x -> Arrays.stream(GameObjects.getObjectsOnTile(x)).noneMatch(o -> o != null && SpindelData.isWeb(o.getId())))// make sure its not webbed
                        .max(Comparator.comparingDouble(x -> x.distance(spindel))) // maybe something here to prefer southern tiles
                        .orElse(null);

                if (safest != null) Walking.walkExact(safest);
            } else {
                Logger.info("Null web tile");
            }
            return ReactionGenerator.getQuick();
        }

        // webs on the ground, get off it
        if (spindel == null) {
            Logger.warn("Null spindel when trying to avoid web");
            return ReactionGenerator.getQuick();
        }

        Area spindelRadius = Players.getLocal().getTile().getArea(9);
        Tile safest = Arrays.stream(spindelRadius.getTiles())
                .filter(x -> x.distance(spindel) > 5)
                .filter(x -> x.distance(spindel) < 9)
                .filter(x -> Arrays.stream(GameObjects.getObjectsOnTile(x)).noneMatch(o -> o != null && SpindelData.isWeb(o.getId())))// make sure its not webbed
                .min(Comparator.comparingDouble(x -> x.distance(spindel))) // maybe something here to prefer southern tiles
                .orElse(null);
        if (safest == null) {
            Logger.warn("Was unable to find a safe tile");
            return ReactionGenerator.getQuick();
        }


        Logger.info("Walk to safest tile");
        Walking.walkExact(safest);
        return ReactionGenerator.getQuick();
    }
}

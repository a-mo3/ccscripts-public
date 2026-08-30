package org.dreambot.behaviour.method.nightmare.phosani;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.behaviour.method.nightmare.PhosaniBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.scripts.PhosaniScript;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.List;

import static org.dreambot.behaviour.method.nightmare.PhosaniBranch.isGoodFlower;

public class PhosaniQuartersPhase extends Fractal {
    @Override
    public boolean isValid() {
        return GameObjects.closest(x -> isGoodFlower(x.getId())) != null;
    }

    @Override
    public int onLoop() {
        Area safe = getGoodFlowerArea();
        if (safe == null) return ReactionGenerator.getQuick();
        PhosaniScript.lastCornerArea = safe;
        if (!safe.contains(Players.getLocal().getServerTile())) {
            Logger.info("Walking into safe area");
            Walking.walkExact(safe.getCenter());
        }

        // todo maybe attack here
        return ReactionGenerator.getQuick();
    }

    public static Area getGoodFlowerArea() {
        List<GameObject> goodFlowers = GameObjects.all(x -> PhosaniBranch.isGoodFlower(x.getId()));
        if (goodFlowers.isEmpty()) {
            Logger.info("Good flowers empty when making area");
            return null;
        }
        // the corners of the safe area will be area(greatestY flower, greatestX flower)
        double greatestDistance = 0;
        GameObject flowerA = null;
        GameObject flowerB = null;
        for (GameObject flower : goodFlowers) {
            for (GameObject otherFlower : goodFlowers) {
                double dist = flower.distance(otherFlower);
                if (dist > greatestDistance) {
                    greatestDistance = dist;
                    flowerA = flower;
                    flowerB = otherFlower;
                }
            }
        }

        if (flowerA == null || flowerB == null) {
            Logger.error("Null flowers nightmare quarter");
            return null;
        } else {
            Area area = new Area(flowerA.getTile(), flowerB.getTile());
            PhosaniScript.lastCornerArea = area;
            return area;
        }
    }
}

package org.dreambot.behaviour.method.gwd.zilyana.tickkillcount;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.util.PrayerUtils;

public class GoToZilKCDecision extends TickDecision {
    public GoToZilKCDecision() {
        setSimpleName("Go to KC spot");
        KC_AREA = possibleKCAreas[Calculations.random(0, possibleKCAreas.length)];
    }

    Area KC_AREA;
    public static final Area ROCK_THROW_AREA = new Area(
            new Tile(2879, 3692, 0),
            new Tile(2884, 3696, 0),
            new Tile(2894, 3701, 0),
            new Tile(2904, 3697, 0),
            new Tile(2907, 3702, 0),
            new Tile(2901, 3710, 0),
            new Tile(2876, 3700, 0));
    Area[] possibleKCAreas = new Area[]{
//            new Area( // top floor (close to rope)
//                    new Tile(2898, 5310, 2),
//                    new Tile(2912, 5301, 2),
//                    new Tile(2912, 5295, 2),
//                    new Tile(2899, 5296, 2)
//            ),
            new Area(2913, 5301, 2924, 5276, 1), // default
//            new Area(2880, 5300, 2892, 5284, 2) // top floor sort of in the middle
    };


    @Override
    public boolean evaluate() {
        if (!KC_AREA.contains(Players.getLocal())) {
            log("Go to kc area");
            // pray range
            if (ROCK_THROW_AREA.contains(Players.getLocal())) {
                PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MISSILES);
            } else {
                PrayerUtils.disable(Prayer.values());
            }

            if (Walking.shouldWalk()) Walking.walk(KC_AREA);
            return true;
        }
        return false;
    }
}

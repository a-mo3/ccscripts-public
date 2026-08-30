package org.dreambot.behaviour.method.venenatis.tickvenenatis;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.Projectiles;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.behaviour.method.venenatis.VenenatisData;
import org.dreambot.fractals.TickDecision;

import java.util.Arrays;
import java.util.Comparator;

public class VenenatisWebPlacement extends TickDecision {
    Tile[] placementTiles = {
            new Tile(3429, 10194, 2),
            new Tile(3430, 10195, 2),
            new Tile(3432, 10197, 2),
            new Tile(3433, 10199, 2),
            new Tile(3433, 10203, 2),
            new Tile(3432, 10205, 2),
            new Tile(3432, 10208, 2),
            new Tile(3431, 10211, 2),
            new Tile(3430, 10212, 2),
            new Tile(3427, 10213, 2),
            new Tile(3423, 10214, 2),
            new Tile(3419, 10213, 2),
            new Tile(3416, 10212, 2),
            new Tile(3415, 10209, 2),
            new Tile(3413, 10206, 2),
            new Tile(3413, 10204, 2),
            new Tile(3414, 10201, 2),
            new Tile(3414, 10199, 2),
            new Tile(3415, 10197, 2),
    };

    @Override
    public boolean evaluate() {
        if (Projectiles.closest(VenenatisData.WEB_PROJECTILE) != null) return false;
        if (TickVenenatisBranch.venenatisAttackStyle != Skill.MAGIC) return false;
        if (TickVenenatisBranch.venenatisAttackCounter < 2 || TickVenenatisBranch.venenatisAttackCounter > 3)
            return false;

        // get on closest placement tile
        log("Get onto placement tile");
        Tile t = Arrays.stream(placementTiles).min(Comparator.comparingDouble(Tile::distance)).orElse(null);
        if (Walking.shouldWalk() && !Players.getLocal().getTile().equals(t)) Walking.walkExact(t);
        return true;
    }
}

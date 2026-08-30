package org.dreambot.behaviour.method.spindel.melee;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.Projectiles;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.spindel.SpindelData;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * in melee mode it is more important we go to the side of the arena so its out the way for the rest of the fight
 */
public class MeleeAvoidWeb extends Fractal {
    public MeleeAvoidWeb(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    List<Tile> sideTiles = Arrays.asList(
            // we dont reall want to be on top, but this is the tile you enter on, its funny to put people into combat instantly
            // plus the stamina drain helps with pkers a bit
//            new Tile(1630, 11557),

            // east wall
            new Tile(1639, 11554, 2),
            new Tile(1639, 11552, 2),
            new Tile(1639, 11550, 2),
            new Tile(1639, 11548, 2),
            new Tile(1639, 11545, 2),
            new Tile(1639, 11542, 2),
            new Tile(1639, 11539, 2),
            new Tile(1639, 11539, 2),
            new Tile(1637, 11539, 2),
            new Tile(1635, 11539, 2),
            new Tile(1633, 11539, 2),
            new Tile(1630, 11539, 2),
            new Tile(1628, 11539, 2),
            new Tile(1626, 11539, 2),
            new Tile(1623, 11539, 2),
            new Tile(1623, 11554, 2),
            new Tile(1623, 11552, 2),
            new Tile(1623, 11550, 2),
            new Tile(1623, 11548, 2),
            new Tile(1623, 11545, 2),
            new Tile(1623, 11542, 2),
            new Tile(1623, 11539, 2));

    @Override
    public int onLoop() {
        NPC spindel = NPCs.closest(SpindelData.SPINDEL_ID);
        GameObject web = GameObjects.closest(x -> SpindelData.isWeb(x.getId()));

        // use mage overhead because you are running away
        if (spindel != null && spindel.distance() > 4) {
            Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
        } else {
            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
        }

        // walk off if you are on a web
        if (web != null || Projectiles.closest(x -> x.getId() == SpindelData.WEB_PROJECTILE) != null) {
            Logger.info("walking off web");
            Area anticipatedWebRadius = MeleeSpindelBranch.webTile.getArea(4);
            Area spindelRadius = Players.getLocal().getTile().getArea(9);
            Tile safest = Arrays.stream(spindelRadius.getTiles())
                    .filter(x -> !anticipatedWebRadius.contains(x))
                    .filter(x -> x.distance(spindel) >= 3)
//                    .filter(x -> x.distance(spindel) >= 2)
                    .filter(x -> Arrays.stream(GameObjects.getObjectsOnTile(x)).noneMatch(o -> o != null && SpindelData.isWeb(o.getId())))// make sure its not webbed
                    .min(Comparator.comparingDouble(x -> x.distance(spindel))) // maybe something here to prefer southern tiles
                    .orElse(null);


            if (safest != null) {
                Walking.walkExact(safest);
            } else {
                Logger.warn("Safest tile was null melee avoid web");
            }

            return ReactionGenerator.getNormal();
        }

        // get the closest side tile and walk there if the projectile is null
        Tile closestSideTile = sideTiles.stream()
                .min(Comparator.comparingDouble(Tile::distance))
                .orElse(null);
        if (closestSideTile != null) {
            Logger.info("Going to side tile with distance " + closestSideTile.distance() + " " + closestSideTile);
            if (!Players.getLocal().getTile().equals(closestSideTile)) {
                Walking.walkExact(closestSideTile);
            } else {
                // todo attack if you are in range
            }
        }
        return ReactionGenerator.getQuick();
    }
}

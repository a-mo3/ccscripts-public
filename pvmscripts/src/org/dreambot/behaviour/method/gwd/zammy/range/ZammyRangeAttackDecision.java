package org.dreambot.behaviour.method.gwd.zammy.range;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.method.gwd.zammy.ZammyCounters;
import org.dreambot.fractals.TickDecision;

/**
 * Attack and/or move
 */
public class ZammyRangeAttackDecision extends TickDecision {
    final Tile[] rotation = new Tile[]{
            // the first tile we only move from once zammy is in the true tile area
            new Tile(2918, 5325, 2),
            new Tile(2928, 5321, 2),
            new Tile(2934, 5325, 2),
            new Tile(2930, 5318, 2),
            new Tile(2922, 5321, 2),
    };


    /**
     * the area zams true tile needs to be in for you to start the cycle
     */
    Area ZAM_FIRST_TILE_AREA = new Area(2921, 5323, 2919, 5327, 2);

    @Override
    public boolean evaluate() {
        if (!Walking.isRunEnabled()) Walking.toggleRun();

        Tile ourTile = Players.getLocal().getServerTile();
        Tile targetTile = rotation[TickRangeZammyBranch.rotationIndex % rotation.length];

        if (ourTile.equals(targetTile)) {
            TickRangeZammyBranch.rotationIndex++;
//            if (ZammyCounters.ourCounter == 0) {
            log("Can attack so attacking");
            NPC zam = NPCs.closest(ZammyCounters.ZAMMY_NAME);
            if (zam != null) zam.interact();
            Antiban.sleepUntil(() -> Players.getLocal().getAnimation() == 7552, 1200);
            ZammyCounters.ourCounter = 4;
//            return true;
//            }
        }
//        else {
        targetTile = rotation[TickRangeZammyBranch.rotationIndex % rotation.length];
        if (!targetTile.equals(Walking.getDestination())) {
            // for 2nd tile red click
            // for my scuffed altar method
//                        && Players.getLocal().getX() < 2922
            if (TickRangeZammyBranch.rotationIndex % rotation.length == 1) {
                // use altar to get a red click until we would be travelling diagonally
                // wait for zammy to be in the right spot
                NPC zam = NPCs.closest(ZammyCounters.ZAMMY_NAME);
                if (zam != null && ZAM_FIRST_TILE_AREA.contains(zam.getServerTile())) {
//                        ObjectUtil.interact("Zamorak altar");
                    GroundItem i = GroundItems.closest(x -> DropItemOnTile.ITEM_TILE.equals(x.getTile()));
                    if (i != null) {
                        log("Red click item");
                        i.interact();
                    } else {
                        log("No item on the tile aaaah");
                    }
                } else {
                    if (zam != null && !Players.getLocal().isMoving() && Players.getLocal().getInteractingCharacter() == null)
                        zam.interact();
                }
                return true;
            }
            log("Walk to target tile");
            Walking.walkExact(targetTile);
        }
//        }
        return false;
    }
}

package org.dreambot.behaviour.quests.deathplateau;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;

public class PlaceStones extends Fractal {
    enum Stone {
        RED(ItemID.STONE_BALL_RED, new Tile(2894, 3563)),
        BLUE(ItemID.STONE_BALL_BLUE, new Tile(2894, 3562)),
        YELLOW(ItemID.STONE_BALL_YELLOW, new Tile(2895, 3564)),
        PINK(ItemID.STONE_BALL_PINK, new Tile(2895, 3562)),
        GREEN(ItemID.STONE_BALL_GREEN, new Tile(2895, 3563)),
        ;

        final int stoneId;
        final Tile tile;

        Stone(int stoneId, Tile tile) {
            this.stoneId = stoneId;
            this.tile = tile;
        }
    }

    @Override
    public int onLoop() {
        // walk to stone area
        // todo gameobject can reach check for if the door is closed
        if (Stone.RED.tile.distance() > 5) {
            if (Walking.shouldWalk()) Walking.walk(Stone.RED.tile);
            return ReactionGenerator.getNormal();
        }

        // check if the stones been placed
        Stone incompleteStone = Arrays.stream(Stone.values())
                .filter(stone -> GroundItems.closest(x -> x.getTile().equals(stone.tile)) == null)
                .findFirst()
                .orElse(null);

        if (incompleteStone == null) {
            Logger.info("All stones complete?");
            return ReactionGenerator.getNormal();
        }

        Item stone = Inventory.get(incompleteStone.stoneId);
        if (stone != null) {
            // use on the mech
            GameObject mech = GameObjects.closest(x -> x.getName().equals("Stone Mechanism")
                    && x.getTile().equals(incompleteStone.tile));
            stone.useOn(mech);
            Sleep.sleep(2400);
            return ReactionGenerator.getNormal();
        }

        // if not pick it up and place
        GroundItem stoneOnGround = GroundItems.closest(x -> x.getId() == incompleteStone.stoneId);
        if (stoneOnGround != null) {
            stoneOnGround.interact();
            Sleep.sleepUntil(() -> Inventory.contains(incompleteStone.stoneId), 3500);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}

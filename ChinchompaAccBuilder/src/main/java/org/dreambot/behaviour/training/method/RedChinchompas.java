package org.dreambot.behaviour.training.method;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.training.HunterUtils;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.LinkedList;
import java.util.List;

public class RedChinchompas extends Fractal implements SpawnListener {
    List<Tile> trapTiles = new LinkedList<>();

    public RedChinchompas() {
//        Listeners.register(this);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.FELDIP_HILLS_TELEPORT, 5)
                .addItem(ItemID.BOX_TRAP, 14);
    }

    // like the feldip hills area
    protected final Area LARGE_REDCHIN_AREA = new Area(2527, 2938, 2566, 2903);
    // the actual 3x3 area you catch chins in
    protected final Area SMALL_AREA_RED = new Area(2557, 2917, 2559, 2915, 0);

    @Override
    public boolean isValid() {
        return Skills.getRealLevel(Skill.HUNTER) >= 63;
    }

    @Override
    public int onLoop() {
        if (!LARGE_REDCHIN_AREA.contains(Players.getLocal())) {
            // probably walk handles the interaction, but i wrote this on dreambot
            if (Inventory.contains("Feldip hills teleport")) {
                Inventory.interact("Feldip hills teleport", "Teleport");
                Sleep.sleepUntil(() -> LARGE_REDCHIN_AREA.contains(Players.getLocal()), 5000);
                return ReactionGenerator.getNormal();
            }

            return ReactionGenerator.getNormal();
        }

//        if (!SMALL_AREA_RED.contains(Players.getLocal())) {
//          if (Walking.shouldWalk(6)) Walking.walk(SMALL_AREA_RED.getCenter());
//            return ReactionGenerator.getNormal();
//        }


        trapTiles.removeIf(t -> {
            GameObject obj = GameObjects.getTopObjectOnTile(t);
            return obj == null || !obj.getName().toLowerCase().contains("box");
        });
        // pick up traps on floor
        GroundItem fallenTrap = GroundItems.closest(ItemID.BOX_TRAP);
        if (fallenTrap != null && fallenTrap.distance() < 5 && fallenTrap.interact("Take")) {
            Sleep.sleepUntil(() -> GroundItems.closest(x -> x.equals(fallenTrap)) == null, 5000);
            return ReactionGenerator.getNormal();
        }

        // place new traps
        Logger.info(trapTiles.size() + "/" + HunterUtils.getTrapLimit());
        if (trapTiles.size() < HunterUtils.getTrapLimit()) {
            Tile bestTile = getBestTile();
            if (bestTile != null) {
                if (Players.getLocal().getTile().equals(bestTile)) {
                    Inventory.interact(ItemID.BOX_TRAP, "Lay");
                    Sleep.sleepUntil(() -> {
                        GameObject trap = GameObjects.getTopObjectOnTile(bestTile);
                        return trap != null && trap.getName().contains("trap");
                    }, 5000);
                    return ReactionGenerator.getNormal();
                }
                if (Walking.shouldWalk(6)) Walking.walk(bestTile);
                Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(bestTile), 5000);
            }
            return ReactionGenerator.getNormal();
        }

        for (GameObject trap : GameObjects.all(x -> x.getName().toLowerCase().contains("box"))) {
            if (!trapTiles.contains(trap.getTile())) continue;
            if (trap.getID() == 9383 || trap.getID() == 9385) {
                if (trap.interact(trap.getActions()[0])) {
                    Sleep.sleepUntil(() -> GameObjects.closest(x -> x.equals(trap)) == null, 3700);
                    return ReactionGenerator.getNormal();
                }
            }
        }
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onGameObjectSpawn(GameObject object) {
//        Logger.info("something spawned " + object.toString());
        if (object.getID() == 9380) {
            if (Players.getLocal().getTile().equals(object.getTile())) {
                // this is to stop boxes failing to catch getting added
                Logger.info("----------------------------------------------");
                Logger.info("Ani: " + Players.getLocal().getAnimation());
                Logger.info("Dist: " + object.distance(Players.getLocal()));
                Logger.info("----------------------------------------------");
                if (Players.getLocal().getAnimation() == 5208) {
                    Logger.info("adding tile: " + object.getTile());
                    trapTiles.add(object.getTile());
                }
            }
        }
    }

    private Tile getBestTile() {
        Tile[] tileSet = new Tile[]{
                new Tile(2558, 2916),
                new Tile(2559, 2917),
                new Tile(2557, 2917),
                new Tile(2557, 2915),
                new Tile(2559, 2915),
                new Tile(2559, 2916),
                new Tile(2558, 2917),
                new Tile(2557, 2916),
                new Tile(2558, 2915)
        };

        for (Tile tile : tileSet) {
            GameObject obj = GameObjects.getTopObjectOnTile(tile);
            if (obj == null || !obj.getName().toLowerCase().contains("box")) {
                return tile;
            }
        }
        return null;
    }

}

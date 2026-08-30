package org.dreambot.behaviour.impl;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.AbstractTask;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RedChinNode extends AbstractTask {
    @Override
    public boolean accept() {
        return Skills.getRealLevel(Skill.HUNTER) >= 63;
    }

    @Override
    public int execute() {
        if (Tabs.getOpen() != Tab.INVENTORY) {
            Tabs.open(Tab.INVENTORY);
        }
        // 9383 caught red chin
        if (!LARGE_REDCHIN_AREA.contains(Players.getLocal())) {
            if (Inventory.contains("Feldip hills teleport")) {
                Inventory.interact("Feldip hills teleport", "Teleport");
                Sleep.sleepUntil(() -> LARGE_REDCHIN_AREA.contains(Players.getLocal()), 5000);
                config.setSubStatus("sleeping 2 minutes to let old traps die");
                Sleep.sleep(120000);
                config.setSubStatus("");
            }
        }

        // if ur close enough to the spot to trap
        if (!SMALL_AREA_RED.getCenter().getArea(10).contains(Players.getLocal())) {
            walkTo(SMALL_AREA_RED);
        }


        GroundItem groundTrap = GroundItems.closest("Box trap");
        if (groundTrap != null) {
            if (groundTrap.interact()) {
                Sleep.sleepUntil(() -> !groundTrap.exists(), 5000);
            }
        }

        // ITERATE OVER TRAP MAP
        Iterator<Map.Entry<Tile, GameObject>> it = config.getTrapMap().entrySet().iterator();
        config.setSubStatus("validating traps...");
        while (it.hasNext()) {
            Map.Entry<Tile, GameObject> entry = it.next();
            Tile tile = entry.getKey();
            if (GameObjects.getTopObjectOnTile(tile) == null || !GameObjects.getTopObjectOnTile(tile).getName().toLowerCase().contains("box")) {
                it.remove();
            }
        }
        config.setSubStatus("");

        // PLACE TRAPS OR DO FOR LOOP ON TRAPS
        if (!config.isFailSafe()) {
            if (config.getTrapMap().size() >= getTrapLimit()) {
                // FOR LOOP / MONITOR AND COLLECT TRAPS
                List<GameObject> trapList = GameObjects.all(x -> x.getName().toLowerCase().contains("box"));
                trapList.sort(Comparator.comparingInt(Entity::getID));
                config.setSubStatus("awaiting rodent");
                for (GameObject trap : trapList) {
                    if (config.getTrapMap().get(trap.getTile()) != null) {
                        // if this trap is one of ours
                        if (!trap.exists()) {
                            config.trapMapPop(trap);
                            break;
                        }
                        if (trap.getID() == 9383 || trap.getID() == 9385) {
                            if (trap.interact()) {
                                config.setSubStatus("interaction sleep");
                                Sleep.sleepUntil(() -> !trap.exists(), 3700);
                                config.setSubStatus("");
                                break;
                            }
                        }
                    }
                }
            } else {
                config.setSubStatus("placing trap.");
                // PLACE TRAPS
                if (getBestTile() != null && Players.getLocal().getTile().equals(getBestTile())) {
                    if (Inventory.contains("box trap")) {
                        if (Inventory.get("Box trap").interact()) {
                            config.setSubStatus("place trap sleep");
                            Sleep.sleepUntil(() -> GameObjects.getTopObjectOnTile(Players.getLocal().getTile()) != null
                                    && GameObjects.getTopObjectOnTile(Players.getLocal().getTile()).getName().contains("trap"), 5000);
                            Sleep.sleepUntil(() -> Players.getLocal().isStandingStill(), 1500);
                            config.setSubStatus("");
                        }
                    }
                } else {
                    if (Walking.shouldWalk(6)) Walking.walkOnScreen(getBestTile());
                    Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(getBestTile()), 1700);
                }
            }

        } else {
            Logger.log("sleeping 90 seconds for your traps to be reset.");
            Sleep.sleep(90000);
            config.setFailSafe(false);
        }
        return 250;
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
            if (GameObjects.getTopObjectOnTile(tile) == null
                    || !GameObjects.getTopObjectOnTile(tile).getName().toLowerCase().contains("box")) {
                return tile;
            }
        }
        return null;
    }
}

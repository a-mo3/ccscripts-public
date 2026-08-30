package org.dreambot.behaviour.training;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CopperLongtails extends Fractal implements SpawnListener {
    private static final int CAUGHT_SNARE_ID = 9379;
    private static final int BROKEN_SNARE_ID = 9344;
    private static final int NORMAL_SNARE_ID = 9345;
    public static int listSize = 0;

    public CopperLongtails() {
        Client.getInstance().addEventListener(this);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.PISCATORIS_TELEPORT, 1, 5)
                .addItem(ItemID.VARROCK_TELEPORT, 1, 5)
                .addItem(ItemID.BIRD_SNARE, 1, 20).setBuyPrice(100);
        setAppendLogic(() -> {
            if (Inventory.count(ItemID.BIRD_SNARE) > 20) {
                Inventory.drop(ItemID.BIRD_SNARE);
                return true;
            }
            return false;
        });
        this.paintArraySupplier = () -> new String[]{
                "Snares: " + mySnares.size() + "/" + HunterUtils.getTrapLimit()
        };
    }

    List<Tile> mySnares = new LinkedList<>();
    public static final Area LONGTAIL_AREA = new Area(2336, 3603, 2349, 3597, 0);

    @Override
    public boolean isValid() {
        return Skills.getRealLevel(Skill.HUNTER) < 43;
    }

    @Override
    public int onLoop() {
        GroundItem groundSnare = GroundItems.closest(ItemID.BIRD_SNARE);
        if (groundSnare != null && Inventory.count(ItemID.BIRD_SNARE) < 15 && groundSnare.interact("Take")) {
            Sleep.sleepUntil(() -> GroundItems.closest(x -> x.equals(groundSnare)) == null, 5000);
            return ReactionGenerator.getNormal();
        }

        if (shouldHop() && mySnares.size() == 0) {
            Logger.info("Hopping worlds");
            WorldHopper.hopWorld(Worlds.getRandomWorld(
                    w -> !w.isF2P() && w.isNormal() && w.getMinimumLevel() < Skills.getTotalLevel()
            ));
            return ReactionGenerator.getNormal();
        }

        if (Inventory.emptySlotCount() < 3) {
            Inventory.dropAll(ItemID.BONES, ItemID.RAW_BIRD_MEAT);
            return ReactionGenerator.getNormal();
        }
        mySnares.removeIf(t -> {
            GameObject topObj = GameObjects.getTopObjectOnTile(t);
            return topObj == null || !topObj.getName().contains("snare");
        });

        listSize = mySnares.size();
        if (!LONGTAIL_AREA.contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(LONGTAIL_AREA.getCenter());
            return ReactionGenerator.getNormal();
        }

        if (mySnares.size() < HunterUtils.getTrapLimit() && !shouldHop()) {
            Tile targetTile = getBestTile();
            if (targetTile != null && !Players.getLocal().getTile().equals(targetTile)) {
                if (Walking.shouldWalk(6)) Walking.walkExact(targetTile);
                Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(targetTile), 5000);
                return ReactionGenerator.getNormal();
            }

            Inventory.interact("Bird snare", "Lay");
            Sleep.sleepUntil(() -> {
                GameObject topObj = GameObjects.getTopObjectOnTile(Players.getLocal().getTile());
                if (topObj == null) return false;
                return topObj.getName().contains("snare");
            }, 5000);
            return ReactionGenerator.getNormal();
        }

        for (GameObject snare : GameObjects.all(x -> x.getName().contains("snare"))) {
            if (mySnares.contains(snare.getTile())) {
                if (snare.getID() == BROKEN_SNARE_ID || snare.getID() == CAUGHT_SNARE_ID) {
                    // if the snare is broken or has a bird
                    Logger.info("interacting with caught or broken snare");
                    if (snare.interact(snare.getActions()[0])) {
                        Sleep.sleepUntil(() -> GameObjects.closest(x -> x.equals(snare)) == null, 3700);
                        break;
                    }
                    return ReactionGenerator.getNormal();
                }
            }
        }

        return ReactionGenerator.getNormal();
    }

    private Tile getBestTile() {
        // spaced 2 apart so moving west wont make player stand ontop of a snare
        // standing on a snare stops the snare from working UPDATE: this is not true, wiki is wrong
        Tile a = new Tile(2344, 3599);
        Tile b = new Tile(2342, 3599);
        Tile c = new Tile(2340, 3599);

        Tile[] tiles = new Tile[]{a, b, c};
        for (Tile t : tiles) {
            if (Arrays.stream(GameObjects.getObjectsOnTile(t)).noneMatch(x -> x.getName().equals("Bird snare"))) {
                return t;
            }
        }
        return null;
    }


    @Override
    public void onGameObjectSpawn(GameObject object) {

        switch (object.getID()) {
            case NORMAL_SNARE_ID:
                if (Players.getLocal().getTile().equals(object.getTile())) {
                    Logger.info("Adding to snares");
                    mySnares.add(object.getTile());
                }
                break;


            case BROKEN_SNARE_ID:
            case CAUGHT_SNARE_ID:
                if (mySnares.contains(object.getTile())) {
                    mySnares.add(object.getTile());
                }
        }
    }

    private boolean shouldHop() {
        if (ScriptSettings.getSettingsData().avoidCompetition && LONGTAIL_AREA.contains(Players.getLocal())) {
            return Players.closest(x -> !x.getName().equals(Players.getLocal().getName()) && x.distance() < 12) != null;
        }
        return false;
    }
}

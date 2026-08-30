package org.dreambot.behaviour.training.hunter;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Hash;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.quests.quizbranch.HunterUtils;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class CopperLongtails extends Fractal implements SpawnListener {
    private static final int CAUGHT_SNARE_ID = 9379;
    private static final int BROKEN_SNARE_ID = 9344;
    private static final int NORMAL_SNARE_ID = 9345;
    public static int listSize = 0;

    public CopperLongtails(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        Client.getInstance().addEventListener(this);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.PISCATORIS_TELEPORT, 1, 5)
                .addItem(ItemID.BIRD_SNARE, 1, 20).setBuyPrice(100);

        setPrependLogic(() -> {
            if (Inventory.count(ItemID.BIRD_SNARE) > 20) {
                Inventory.drop(ItemID.BIRD_SNARE);
                return true;
            }
            return false;
        });
    }

    public CopperLongtails() {
        super(() -> Skills.getRealLevel(Skill.HUNTER) < 43);
//        Listeners.register(this);
        Client.getInstance().addEventListener(this);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.PISCATORIS_TELEPORT, 1, 5)
                .addItem(ItemID.BIRD_SNARE, 1, 20).setBuyPrice(100);

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH);

        setPrependLogic(() -> {
            if (Inventory.count(ItemID.BIRD_SNARE) > 20) {
                Inventory.drop(ItemID.BIRD_SNARE);
                return true;
            }
            return false;
        });
    }

    Set<Tile> mySnares = new HashSet<>();
    public static final Area LONGTAIL_AREA = new Area(2336, 3603, 2349, 3597, 0);


    @Override
    public int onLoop() {
        GroundItem groundSnare = GroundItems.closest(ItemID.BIRD_SNARE);
        if (groundSnare != null && Inventory.count(ItemID.BIRD_SNARE) < 15 && groundSnare.interact("Take")) {
            Sleep.sleepUntil(() -> GroundItems.closest(x -> x.equals(groundSnare)) == null, 5000);
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

        Logger.info("snares: " + mySnares.size() + "/" + HunterUtils.getTrapLimit());
        if (mySnares.size() < HunterUtils.getTrapLimit()) {
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
                if (snare.getId() == BROKEN_SNARE_ID || snare.getId() == CAUGHT_SNARE_ID) {
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

        GameObject obj = GameObjects.getTopObjectOnTile(a);
        if (obj != null && !obj.getName().equals("Bird snare")) {
            return a;
        }
        obj = GameObjects.getTopObjectOnTile(b);
        if (obj != null && !obj.getName().equals("Bird snare")) {
            return b;
        }
        obj = GameObjects.getTopObjectOnTile(c);
        if (obj != null && !obj.getName().equals("Bird snare")) {
            return c;
        }
        return null;
    }


    @Override
    public void onGameObjectSpawn(GameObject object) {
        switch (object.getId()) {
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
}

//    @Override
//    public void notify(GameObjectDespawnedEvent despawnedEvent) {
//        GameObject object = despawnedEvent.getGameObject();
//        Tile tile = despawnedEvent.getGameObject().getTile();
//        if (tile == null) return;
//        switch (object.getId()) {
//            case NORMAL_SNARE_ID:
//                if (GroundItems.closest(x -> x.getName().contains("snare") && x.getTile().equals(tile)) != null) {
//                    mySnares.remove(tile);
//                    // remove the tile only if the normal snare fell over
//                }
//                break;
//            case BROKEN_SNARE_ID:
//            case CAUGHT_SNARE_ID:
//                mySnares.remove(tile);
//        }
//    }


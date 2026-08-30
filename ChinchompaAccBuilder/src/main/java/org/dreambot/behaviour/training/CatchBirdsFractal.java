package org.dreambot.behaviour.training;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
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
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CatchBirdsFractal extends Fractal implements SpawnListener {
    final Area area;
    final BirdType birdType;
    final Supplier<Tile> tilePattern;
    final Map<Tile, GameObject> snareMap = new HashMap<>();

    public static final int BIRD_SNARE = 10006;
    public static final int PISCATORIS_TELEPORT = 12408;
    public static final int VARROCK_TELEPORT = 8007;

    public CatchBirdsFractal(Supplier<Boolean> acceptCondition, Area area, BirdType birdType, Supplier<Tile> tilePattern) {
        super(acceptCondition);
        this.area = area;
        this.birdType = birdType;
        this.tilePattern = tilePattern;
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(BIRD_SNARE, 1, 10)
                .addItem(PISCATORIS_TELEPORT, 1, 8)
                .addItem(VARROCK_TELEPORT, 1, 8);
//        Listener.register(this);
        this.paintArraySupplier = () -> new String[]{
                "Snares down: " + snareMap.size(),
                "Hunter lvl: " + Skills.getRealLevel(Skill.HUNTER)
        };
    }

    @Override
    public int onLoop() {
        if (GrandExchange.isOpen() || Bank.isOpen()) {
            GrandExchange.close();
            Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (!area.contains(Players.getLocal())) {
            setStatus("Walking to bird area");
            if (Walking.shouldWalk(6)) Walking.walk(area.getCenter());
            return ReactionGenerator.getNormal();
        }

        GroundItem groundSnare = GroundItems.closest(x -> x.getName().contains("snare"));
        if (groundSnare != null && groundSnare.interact()) {
            Sleep.sleep(2400);
            return ReactionGenerator.getNormal();
        }

        // clear out the entries without snares
        Iterator<Map.Entry<Tile, GameObject>> iterator = snareMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Tile tile = iterator.next().getKey();
            GameObject topobj = GameObjects.getTopObjectOnTile(tile);
            if (topobj == null || !topobj.getName().contains("snare")) {
                Logger.info("Removed Tile - " + tile + " " + topobj);
                iterator.remove();
            }
        }

        if (Inventory.emptySlotCount() < 3) {
            setStatus("Emptying inventory");
            Inventory.dropAll("Raw bird meat");
            buryBones();
            return ReactionGenerator.getNormal();
        }

        Tile bestTile = tilePattern.get();
        if (snareMap.size() >= getTrapLimit()) {
            setStatus("Waiting for traps to fall");
            List<GameObject> traps = GameObjects.all(x -> x.getName().contains("snare"));
            for (GameObject trap : traps) {
                if (!snareMap.containsKey(trap.getTile())) {
                    return ReactionGenerator.getNormal();
                }
                if (trap.getID() == birdType.BROKEN_SNARE_ID || trap.getID() == birdType.CAUGHT_SNARE_ID) {
                    setStatus("Collecting trap");
                    trap.interact();
                    Sleep.sleepUntil(() -> true, 2400);
                }
            }
            return ReactionGenerator.getNormal();
        }

        setStatus("Setting new trap");
        if (!Players.getLocal().getTile().equals(bestTile)) {
            if (Walking.shouldWalk(6)) Walking.walkExact(bestTile);
            Sleep.sleepUntil(() -> bestTile.equals(Players.getLocal().getTile()), 2400);
            return ReactionGenerator.getNormal();
        }

        Item snare = Inventory.get("Bird snare");
        if (snare != null) {
            Inventory.interact(snare, "Law");
            Sleep.sleepUntil(() -> {
                GameObject obj = GameObjects.getTopObjectOnTile(Players.getLocal().getTile());
                return obj != null && obj.getName().contains("snare");
            }, 4400);
        }
        return ReactionGenerator.getNormal();
    }

    private int getTrapLimit() {
        int level = Skills.getRealLevel(Skill.HUNTER);
        if (level < 20) return 50;
        if (level < 40) return 2;
        if (level < 60) return 3;
        if (level < 80) return 4;
        return 5;
    }

    public static void buryBones() {
        if (!ScriptSettings.shouldBuryBones()) {
            Inventory.dropAll("Bones");
            return;
        }

        Item bones = Inventory.get("Bones");
        if (bones != null) {
            Inventory.interact(bones, "Bury");
            buryBones();
        }
    }

    @Override
    public void onGameObjectSpawn(GameObject object) {
        Logger.info("Object spawned " + object.getName() + " " + object.getTile());
        if (object.getName().contains("snare")) {
            if (object.getTile().equals(Players.getLocal().getTile())) {
                Logger.info("Added snare");
                snareMap.put(object.getTile(), object);
            } else {
                Logger.info("Snare spawn at a non player tile");
            }
        }
    }
}

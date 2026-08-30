package org.dreambot.behaviour.method.rogueschest;

import org.dreambot.api.Client;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.muling.Log;
import org.dreambot.scriptdata.RoguesChestSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.*;
import java.util.function.Supplier;

public class OpenRogueChests extends Fractal implements SpawnListener {
    final Area CHEST_AREA = new Area(
            new Tile(3282, 3946, 0),
            new Tile(3282, 3942, 0),
            new Tile(3287, 3942, 0),
            new Tile(3287, 3938, 0),
            new Tile(3290, 3936, 0),
            new Tile(3291, 3947, 0),
            new Tile(3282, 3947, 0));
    public static final HashMap<GameObject, Timer> chestMap = new HashMap<>();
    public static Timer lastOpenedTimer = new Timer();

    public OpenRogueChests(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Open Rogues chest");
        Client.getInstance().addEventListener(this);

        this.loadoutCondition = () -> !Combat.isInWild();

        int[] cutWeapons = new int[]{
                ItemID.BRONZE_AXE,
                ItemID.IRON_SCIMITAR,
                ItemID.IRON_SWORD,
        };

        this.eventBreakCondition = Combat::isInWild;
        this.paintArraySupplier = () -> new String[]{
                String.format("Inventory Value: %d / %d", inventoryValue(), SettingsRepository.findInstanceOf(new RoguesChestSettings()).lootThreshold)
        };

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemVariants.STAMINA_POTION)
                .setRefill(20)
                .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 2)
                .setRefill(20)
                .addItem(ItemID.BLIGHTED_MANTA_RAY, 7)
                .setRefill(400)
                .addItem(ItemID.BLIGHTED_KARAMBWAN, 3)
                .setRefill(100)
        ;
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .setRefill(10)
                .addItem(EquipmentSlot.WEAPON, cutWeapons[ShuffleFractal.getLoginValue() % cutWeapons.length])
                .setRefill(10)
        ;

        // add webnodes for the wilderness lever
        EntranceWebNode edgevilleWildernessLever = new EntranceWebNode(
                3090, 3475, 0,
                "Lever", "Pull"
        );

        EntranceWebNode wildernessEdgevilleLever = new EntranceWebNode(
                3153, 3923, 0,
                "Lever", "Pull"
        );
        wildernessEdgevilleLever.setActions(new String[]{"Edgeville", "Pull", "Ardougne"});

        BasicWebNode wildernessBasic = new BasicWebNode(3156, 3936, 0);

        WebFinder wf = WebFinder.getWebFinder();

        wf.addWebNode(wildernessBasic);
        edgevilleWildernessLever.addDualConnections(wildernessEdgevilleLever);
        wildernessEdgevilleLever.addDualConnections(wildernessBasic);
        wf.getNearest(edgevilleWildernessLever.getTile(), 12).addDualConnections(edgevilleWildernessLever);

        wf.addWebNode(edgevilleWildernessLever);
        wf.addWebNode(wildernessEdgevilleLever);

        AbstractWebNode webNode0 = new BasicWebNode(3158, 3950, 0);
        AbstractWebNode webNode1 = new BasicWebNode(3159, 3942, 0);
        webNode0.addDualConnections(WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15));
        WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15).addDualConnections(webNode0);
        webNode0.addDualConnections(webNode1);
        webNode1.addDualConnections(webNode0);

        AbstractWebNode[] webNodes = {webNode0, webNode1,};
        WebFinder.getWebFinder().addWebNodes(webNodes);
        webNode1.addDualConnections(wildernessBasic);

    }

    public static List<Integer> ignoredIds = Arrays.asList(
            ItemID.BLIGHTED_SUPER_RESTORE4,
            ItemID.BLIGHTED_MANTA_RAY,
            ItemID.BLIGHTED_KARAMBWAN
    );

    public static int inventoryValue() {
        return Inventory.all()
                .stream()
                .mapToInt(x -> {
                    if (x == null) return 0;
                    if (ignoredIds.contains(x.getId())) return 0;
                    return (x.getLivePrice()) * x.getAmount();
                })
                .sum() + LootingBag.value()
                ;
    }

    Tile CLOSED_DOOR_TILE = new Tile(3279, 3939, 0);

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            if (Dialogues.canEnterInput()) {
                log("input handle");
                if (Widgets.isOpen()) Widgets.closeAll();
                Keyboard.type("1 ", true);
                return ReactionGenerator.getNormal();
            }
            log("Dialog handle");
            Dialog.solve("don't show this warning", "");
            return ReactionGenerator.getNormal();
        }

        if (!CHEST_AREA.contains(Players.getLocal())) {
            if (Walking.shouldWalk(8)) Walking.walk(CHEST_AREA.getCenter());
            return ReactionGenerator.getNormal();
        }

        if (Walking.getRunEnergy() < 20 && ItemVariants.STAMINA_POTION.getItem() != null) {
            Inventory.interact(x -> ItemVariants.STAMINA_POTION.contains(x.getId()), "Drink");
        }

        if (!Walking.isRunEnabled()) Walking.toggleRun();

        if (Skills.getBoostedLevel(Skill.PRAYER) < 15 && ItemVariants.BLIGHTED_SUPER_RESTORE.getItem() != null) {
            Inventory.interact(ItemVariants.BLIGHTED_SUPER_RESTORE.getItem(), "Drink");
        }

        if (Inventory.contains(ItemID.VIAL)) {
            Inventory.dropAll(ItemID.VIAL);
        }

        // closed door is (3279, 3939, 0) ID: 14749 Name: Door Action: Open
        GameObject closedDoor = GameObjects.closest(x -> x.getId() == 14749 && x.getTile().equals(CLOSED_DOOR_TILE));
        if (closedDoor != null && closedDoor.hasAction("Open")) {
            log("Opening door to stairs");
            closedDoor.interact("Open");
            Sleep.sleepUntil(() -> GameObjects.closest(x -> x.getId() == 14749 && x.getTile().equals(CLOSED_DOOR_TILE)) == null
                    , 2000);
            return ReactionGenerator.getNormal();
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) >= 1) {
            // do this here so we will still open chests when trying to die, getting agro
            if (inventoryValue() > SettingsRepository.findInstanceOf(new RoguesChestSettings()).lootThreshold
                    || Inventory.count(ItemID.BLIGHTED_MANTA_RAY) + Inventory.count(ItemID.BLIGHTED_KARAMBWAN) < 5
                    || Inventory.isFull()) {
                log("Should suicide");
                Prayers.toggle(false, Prayer.PROTECT_FROM_MELEE);
                if (!Players.getLocal().isInCombat()) {
                    NPC rogue = NPCs.closest(x -> x.getName().equals("Rogue") && x.canReach());
                    if (rogue != null) {
                        log("Attack rogue");
                        rogue.interact("Attack");
                    }
                }
                return ReactionGenerator.getNormal();
            } else {
                Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
            }
        }

        if (Combat.getHealthPercent() < 70) {
            Inventory.interact(ItemID.BLIGHTED_MANTA_RAY, "Eat");
        }

        chestMap.entrySet().removeIf(entry -> entry.getValue().elapsed() > 22_000);

        if (Inventory.isFull() && !Players.getLocal().isInCombat()) {
            log("Finding rogue");
            NPC rogue = NPCs.closest("Rogue");
            if (rogue != null) {
                log("Attack rogue");
                rogue.interact("Attack");
            }
            return ReactionGenerator.getNormal();
        }


        GameObject chest = GameObjects.closest(x -> x.getName().equals("Chest") && x.hasAction("Search for traps"));
        if (chest != null) {
            chest.interact("Search for traps");
            lastOpenedTimer.reset();
            Sleep.sleepUntil(() -> CombatUtil.getThreat() != null
                            || !chest.equals(GameObjects.closest(x -> x.getTile().equals(chest.getTile()))),
                    5400);
            return ReactionGenerator.getNormal();
        }

        chestMap.entrySet().removeIf(x -> x.getKey().distance() > 20 || !CHEST_AREA.contains(x.getKey()));
        // walk to next resetting chest
        GameObject lowestTimerGameObject = chestMap.entrySet().stream()
                .max(Comparator.comparingDouble(entry -> entry.getValue().elapsed()))
                .map(Map.Entry::getKey)
                .orElse(null); // Handle the case where the map is empty
        log("next chest " + lowestTimerGameObject);
        if (lowestTimerGameObject != null) Log.info("Chest tile " + lowestTimerGameObject.getTile());

        if (lowestTimerGameObject != null && lowestTimerGameObject.distance() > 2) {
            log("Walking to next chest");
            if (Walking.shouldWalk(8)) Walking.walkExact(lowestTimerGameObject.getTile()
                    .translate(0, lowestTimerGameObject.getY() < 3945 ? 1 : -1));
        }
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onGameObjectSpawn(GameObject object) {
        if (object.getName().toLowerCase().contains("chest") && CHEST_AREA.contains(object)) {
            log("Adding object " + object.getName());
            chestMap.put(object, new Timer(20_400));
        }
    }

    @Override
    public void onGameObjectDespawn(GameObject object) {
        chestMap.remove(object);
    }
}

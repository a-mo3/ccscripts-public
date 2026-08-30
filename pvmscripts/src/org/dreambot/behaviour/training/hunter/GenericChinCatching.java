package org.dreambot.behaviour.training.hunter;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scripts.BlackChinchompasScript;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class GenericChinCatching extends Fractal implements ChatListener, SpawnListener {
    public static List<BoxTrapState> boxTrapStates = Collections.synchronizedList(new CopyOnWriteArrayList<>());
    Tile lastActionedTile = null;
    final Tile[] hunterLocationCenters = new Tile[]{
            new Tile(3145, 3774),
            new Tile(3156, 3772),
            new Tile(3137, 3782),

    };

    final Tile trapCenter;
    final Tile[] tileOptions;
    List<Integer> pickupTrapIds = Arrays.asList(
            9383,
            9382,
            9385,
            721
    );

    boolean avoidCompetition;

    public GenericChinCatching(Supplier<Boolean> acceptCondition, Tile trapCenter, boolean avoidCompetition) {
        super(acceptCondition);
        this.avoidCompetition = avoidCompetition;
        this.trapCenter = trapCenter;
        Client.getInstance().addEventListener(this);
        this.paintArraySupplier = () -> new String[]{
                "Traps down: " + boxTrapStates.size(),
                String.format("My traps %d/%d", boxTrapStates.stream()
                        .filter(s -> s.getOwner() == BoxTrapState.Owner.ME)
                        .count(), HunterUtils.getTrapLimit())
        };

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.BOX_TRAP, 10, 20)
                .setEnabledCondition(() -> (long) boxTrapStates.size() + Inventory.count(ItemID.BOX_TRAP) < 7)
//                .strictIgnore(ItemID.BLACK_CHINCHOMPA)
                .addItem(ItemVariants.GAMES_NECKLACE)
                .addItem(ItemVariants.SKILLS_NECKLACE)
                .setEnabledCondition(() -> !Combat.isInWild() && Players.getLocal().getY() < 3600) // include corp lair
        ;
        this.setEventBreakCondition(() -> !Client.isLoggedIn());

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING);


        tileOptions = new Tile[]{
                trapCenter,
                pureTranslate(trapCenter, 1, 1),
                pureTranslate(trapCenter, 1, -1),
                pureTranslate(trapCenter, -1, 1),
                pureTranslate(trapCenter, -1, -1),
                pureTranslate(trapCenter, 0, 1),
                pureTranslate(trapCenter, 1, 0),
                pureTranslate(trapCenter, -1, 0),
                pureTranslate(trapCenter, 0, -1),
        };

        // todo web node for getting out of falconry area, singleton it
    }

    public static final Area FALCONRY_AREA = new Area(2363, 3621, 2394, 3572);

    Timer eatTimer = new Timer(1800);

    int lastWorld = 0;

    @Override
    public int onLoop() {
        int curr = Worlds.getCurrentWorld();
        if (curr != lastWorld) {
            BlackChinAntiPkNode.avoidCount++;
            lastWorld = curr;
            boxTrapStates.clear();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("Yes.");
            return ReactionGenerator.getQuick();
        }

        if (!trapCenter.getArea(8).contains(Players.getLocal())) {
            if (eatTimer.finished() && Combat.getHealthPercent() < 70 && Inventory.contains(ItemID.JUG_OF_WINE)) {
                Inventory.interact(ItemID.JUG_OF_WINE, "Drink");
                return ReactionGenerator.getQuick();
            }

            if (Players.getLocal().isHealthBarVisible() && Walking.getRunEnergy() > 5 && !Walking.isRunEnabled()) {
                Walking.toggleRun();
            }
            if (Walking.shouldWalk()) Walking.walk(trapCenter.getArea(8));
            return ReactionGenerator.getQuick();
        }

        // clean list
        boxTrapStates.removeIf(x -> x.getTrap() == null);

        // check unknown boxes
        GameObject unknownTrap = boxTrapStates.stream()
                .filter(b -> b.getOwner() == BoxTrapState.Owner.UNKNOWN)
                .map(BoxTrapState::getTrap)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (unknownTrap != null) {
            unknownTrap.interact();
            lastActionedTile = unknownTrap.getTile();
            Sleep.sleepUntil(() -> !unknownTrap.exists(), 1400);
            return ReactionGenerator.getQuick();
        }

        int myBoxes = (int) boxTrapStates.stream().filter(BoxTrapState::isMine).count();

        // pick up traps on floor
        GroundItem fallenTrap = GroundItems.closest(ItemID.BOX_TRAP);
        if (Inventory.emptySlotCount() > 2 && fallenTrap != null && fallenTrap.distance() < 5 && fallenTrap.interact("Take")) {
            Sleep.sleepUntil(() -> GroundItems.closest(x -> x.equals(fallenTrap)) == null, 5000);
            return ReactionGenerator.getQuick();
        }

        if (shouldHop() && myBoxes == 0) {
            Logger.info("Hopping worlds");
            WorldHopper.hopWorld(Worlds.getRandomWorld(
                    w -> !w.isF2P() && w.isNormal() && w.getMinimumLevel() < Skills.getTotalLevel()
            ));
            return ReactionGenerator.getQuick();
        }

        if (!shouldHop() && myBoxes < HunterUtils.getTrapLimit()) {
            Tile best = getBestTile();
            Logger.info("Placing trap " + best);
            if (best != null) {
                if (!Players.getLocal().getTile().equals(best)) {
                    Walking.walkExact(best);
                    if (Sleep.sleepUntil(() -> Players.getLocal().getServerTile().equals(best), 2400)) {
                        Inventory.interact(ItemID.BOX_TRAP, "Lay");
                        if (Sleep.sleepUntil(() -> Arrays.stream(GameObjects.getObjectsOnTile(best))
                                .anyMatch(g -> g.getName().toLowerCase().contains("box")), Menu.isMenuManipulationActive() ? 2800 : 4400)) {
                            boxTrapStates.add(new BoxTrapState(best, BoxTrapState.Owner.ME));
                        }
                    }
                } else {
                    Inventory.interact(ItemID.BOX_TRAP, "Lay");
                    if (Sleep.sleepUntil(() -> Arrays.stream(GameObjects.getObjectsOnTile(best))
                            .anyMatch(g -> g.getName().toLowerCase().contains("box")), Menu.isMenuManipulationActive() ? 2800 : 4400)) {
                        boxTrapStates.add(new BoxTrapState(best, BoxTrapState.Owner.ME));
                    }
                }
                return ReactionGenerator.getQuick();
            }
        }


        // pick up boxes
        GameObject readyBox = boxTrapStates.stream()
                .map(BoxTrapState::getTrap)
                .filter(Objects::nonNull)
                .filter(t -> pickupTrapIds.contains(t.getId()) && t.getAnimationID() != 5175) // 5175 animation for (red) chin entering a trap
                .sorted(Comparator.comparingInt(GameObject::getId))
                .findFirst()
                .orElse(null);
        if (Inventory.isFull()) {
            Inventory.drop(ItemID.BOX_TRAP);
            return ReactionGenerator.getQuick();
        }

        if (readyBox != null && readyBox.interact()) {
            lastActionedTile = readyBox.getTile();
            Sleep.sleepUntil(() -> !readyBox.exists(), 2400);
        }
        return ReactionGenerator.getQuick();
    }

    private Tile getBestTile() {
        return Arrays.stream(tileOptions)
                .filter(x -> Arrays.stream(GameObjects.getObjectsOnTile(x)).noneMatch(g -> g.getName() != null && g.getName().toLowerCase().contains("box")))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void onGameObjectSpawn(GameObject object) {
//        if (trapCenter.distance() > 8) return;
//        if (object.getId() == 9380) {
//            if (Players.getLocal().getTile().equals(object.getTile())) {
//                // this is to stop boxes failing to catch getting added
//                if (Players.getLocal().getAnimation() == 5208) {
//                    Logger.info("adding tile: " + object.getTile());
//                    boxTrapStates.add(new BoxTrapState(object.getTile(), BoxTrapState.Owner.ME));
//                }
//            }
//        }
    }

    /**
     * translate a tile without modifying it, im not sure what normal translate does
     */
    private Tile pureTranslate(Tile t, int xOff, int yOff) {
        return new Tile(t.getX() + xOff, t.getY() - yOff);
    }

    @Override
    public void onGameMessage(Message message) {
        if (trapCenter.distance() > 8) return;
        if (message.getMessage().toLowerCase().contains("t your trap")) {
            boxTrapStates.stream()
                    .filter(b -> b.getTile().equals(lastActionedTile))
                    .forEach(b -> b.setOwner(BoxTrapState.Owner.SOMEONE_ELSE));
        }

        if (message.getMessage().toLowerCase().contains("set up only") && !message.getMessage().toLowerCase().contains("congratulations")) {
            Logger.log("Box tracking failure, this is normally caused by lag, dismissing random events or being crashed by another player");
            for (GameObject obj : GameObjects.all(x -> x.distance() < 5 && x.getName().toLowerCase().contains("box"))) {
                if (boxTrapStates.stream().noneMatch(x -> x.getTile().equals(obj.getTile()))) {
                    boxTrapStates.add(new BoxTrapState(obj.getTile(), BoxTrapState.Owner.UNKNOWN));
                }
            }
            return;
        }

        if (message.getMessage().toLowerCase().contains("6 traps at a")) {
            Logger.log("Box tracking failure, this is normally caused by lag, dismissing random events or being crashed by another player");
            for (GameObject obj : GameObjects.all(x -> x.distance() < 5 && x.getName().toLowerCase().contains("box"))) {
                if (boxTrapStates.stream().noneMatch(x -> x.getTile().equals(obj.getTile()))) {
                    boxTrapStates.add(new BoxTrapState(obj.getTile(), BoxTrapState.Owner.UNKNOWN));
                }
            }
        }
    }


    private boolean shouldHop() {
        if (avoidCompetition && trapCenter.distance() < 6) {
            return Players.closest(x -> !x.getName().equals(Players.getLocal().getName()) && x.distance() < 6) != null;
        }
        return false;
    }
}

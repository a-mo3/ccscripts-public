package org.dreambot.behaviour.method;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.training.HunterUtils;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class NewRedChins extends Fractal implements ChatListener, SpawnListener, ItemContainerListener {
    public static List<BoxTrapState> boxTrapStates = Collections.synchronizedList(new CopyOnWriteArrayList<>());
    Tile lastActionedTile = null;
    final Tile[] hunterLocationCenters = new Tile[]{
            new Tile(2558, 2916),
            new Tile(2558, 2931),
    };
    final Area FALCONRY_AREA = new Area(2363, 3621, 2394, 3572);
    public static int chinsCaught = 0;

    final Tile trapCenter = hunterLocationCenters[ShuffleFractal.getLoginValue() % hunterLocationCenters.length];
    final Tile[] tileOptions = new Tile[]{
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

    List<Integer> pickupTrapIds = Arrays.asList(
            9383,
            9385
    );

    public NewRedChins(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        Client.getInstance().addEventListener(this);
        this.paintArraySupplier = () -> new String[]{
                "Traps down: " + boxTrapStates.size(),
                String.format("My traps %d/%d", boxTrapStates.stream()
                        .filter(s -> s.getOwner() == BoxTrapState.Owner.ME).count(), HunterUtils.getTrapLimit())
        };


        this.inventoryLoadout = new InventoryLoadout()
                .setStrict(true)
                .strictIgnore(ItemID.RED_CHINCHOMPA_10034)
                .addItem(ItemID.FELDIP_HILLS_TELEPORT, 1, 5)
                .addItem(ItemID.BOX_TRAP, 1, 14).setRefill(100)
                .addItem(ItemID.VARROCK_TELEPORT, 1, 5)
                .setRefill(ScriptSettings.getSettingsData().getBoxTrapRestock())
                .setBuyPrice(ScriptSettings.getSettingsData().getBoxTrapBuyPrice())
                .setMuleRequestAmount(ScriptSettings.getSettingsData().getBoxTrapRestock() * ScriptSettings.getSettingsData().getBoxTrapBuyPrice())
        ;

        this.appendLogic = () -> {
            if (FALCONRY_AREA.contains(Players.getLocal())) {
                Magic.castSpell(Normal.HOME_TELEPORT);
                Sleep.sleepUntil(() -> !FALCONRY_AREA.contains(Players.getLocal()), 30_0000);
                return true;
            }
            return false;
        };
    }

    @Override
    public int onLoop() {
        if (!trapCenter.getArea(8).contains(Players.getLocal())) {
            Walking.walk(trapCenter.getArea(8));
            return ReactionGenerator.getNormal();
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

        if (shouldHop() && myBoxes == 0) {
            Logger.info("Hopping worlds");
            WorldHopper.hopWorld(Worlds.getRandomWorld(
                    w -> !w.isF2P() && w.isNormal() && w.getMinimumLevel() < Skills.getTotalLevel()
            ));
            return ReactionGenerator.getNormal();
        }


        // pick up traps on floor
        GroundItem fallenTrap = GroundItems.closest(ItemID.BOX_TRAP);
        if (fallenTrap != null && fallenTrap.distance() < 5 && Inventory.count(ItemID.BOX_TRAP) < 14 && fallenTrap.interact("Take")) {
            Sleep.sleepUntil(() -> !fallenTrap.exists(), 2400);
            return ReactionGenerator.getNormal();
        }

        if (!shouldHop() && myBoxes < HunterUtils.getTrapLimit()) {
            Tile best = getBestTile();
            Logger.info("Placing trap " + best);
            if (best != null) {
                if (!Players.getLocal().getTile().equals(best)) {
                    Walking.walkExact(best);
                    if (Sleep.sleepUntil(() -> Players.getLocal().getServerTile().equals(best), 1200)) {
                        Inventory.interact(ItemID.BOX_TRAP, "Lay");
                        if (Sleep.sleepUntil(() -> Arrays.stream(GameObjects.getObjectsOnTile(best))
                                .anyMatch(g -> g.getName().toLowerCase().contains("box")), Menu.isMenuManipulationActive() ? 2400 : 4400)) {
                            boxTrapStates.add(new BoxTrapState(best, BoxTrapState.Owner.ME));
                        }
                        return ReactionGenerator.getNormal();
                    }
                } else if (!Players.getLocal().isAnimating()) {
                    Inventory.interact(ItemID.BOX_TRAP, "Lay");
                    if (Sleep.sleepUntil(() -> Arrays.stream(GameObjects.getObjectsOnTile(best))
                            .anyMatch(g -> g.getName().toLowerCase().contains("box")), Menu.isMenuManipulationActive() ? 2400 : 4400)) {
                        boxTrapStates.add(new BoxTrapState(best, BoxTrapState.Owner.ME));
                    }
                }
            }


            return ReactionGenerator.getQuick();
        }

        // pick up boxes
        GameObject readyBox = boxTrapStates.stream()
                .map(BoxTrapState::getTrap)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(GameObject::getID))
                .filter(t -> pickupTrapIds.contains(t.getID()) && t.getAnimationID() != 5175) // 5175 animation for (red) chin entering a trap
                .findFirst().orElse(null);
        if (readyBox != null && readyBox.interact()) {
            Sleep.sleepUntil(() -> !readyBox.exists(), 2400);
        }

        return ReactionGenerator.getNormal();
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
//        if (object.getID() == 9380) {
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
        if (message.getMessage().toLowerCase().contains("isn't your trap")) {
            boxTrapStates.stream()
                    .filter(b -> b.getTile().equals(lastActionedTile))
                    .forEach(b -> b.setOwner(BoxTrapState.Owner.SOMEONE_ELSE));
        }

        if (message.getMessage().toLowerCase().contains("set up only") && !message.getMessage().toLowerCase().contains("congratulations")) {
            Logger.log("Box tracking failure, this is normally caused by lag, dismissing random events or being crashed by another player");
            for (GameObject obj : GameObjects.all(x -> x.distance() < 12 && x.getName().toLowerCase().contains("box"))) {
                if (boxTrapStates.stream().noneMatch(x -> x.tile.equals(obj.getTile()))) {
                    boxTrapStates.add(new BoxTrapState(obj.getTile(), BoxTrapState.Owner.UNKNOWN));
                }
            }
        }
    }


    private boolean shouldHop() {
        if (ScriptSettings.getSettingsData().avoidCompetition && trapCenter.distance() < 8) {
            return Players.closest(x -> !x.getName().equals(Players.getLocal().getName()) && x.distance() < 6) != null;
        }
        return false;
    }


    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (trapCenter.distance() > 8) return;
        if (incoming.getID() == ItemID.RED_CHINCHOMPA_10034) {
            int amountAdded = incoming.getAmount() - existing.getAmount();
            // amount other than 1 means something sus happeneing
            if (amountAdded == 1) {
                Logger.info(gson.toJson(incoming));
                Logger.info(gson.toJson(existing));
                chinsCaught++;
            }
        }
    }
}

package org.dreambot.behaviour.impl;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
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
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.BehaviourUtils;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.dreambot.behaviour.BehaviourUtils.*;


public class BlackChinsNode extends Fractal implements SpawnListener {
    Config config = Config.getConfig();

    public BlackChinsNode() {
        Client.getInstance().addEventListener(this);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.BOX_TRAP, 6 - config.getTrapMap().size(), 20)
                .setRefill(ScriptSettings.getSettingsData().getBoxTrapRestock())
                .setBuyPrice(ScriptSettings.getSettingsData().getBoxTrapBuyPrice())
                .setMuleRequestAmount(ScriptSettings.getSettingsData().getBoxTrapRestock() * ScriptSettings.getSettingsData().getBoxTrapBuyPrice())
//                .strictIgnore(ItemID.BLACK_CHINCHOMPA)
                .addItem(ItemVariants.GAMES_NECKLACE)
                .setStrictSupplier(() -> !Combat.isInWild());
        ;

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
        ;


        Area FALCONRY_AREA = new Area(2363, 3621, 2394, 3572);
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
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            Dialog.solve("Yes.");
            return ReactionGenerator.getQuick();
        }

        if (!BLACK_CHINS.contains(Players.getLocal())) {
            BehaviourUtils.stdWalk(BLACK_CHINS);
            return ReactionGenerator.getNormal();
        }

        if (shouldHop() && config.getTrapMap().isEmpty()) {
            Logger.info("Hopping worlds");
            WorldHopper.hopWorld(Worlds.getRandomWorld(
                    w -> !w.isF2P() && w.isNormal() && w.getMinimumLevel() < Skills.getTotalLevel()
            ));
            return ReactionGenerator.getNormal();
        }

        GroundItem fallenTrap = GroundItems.closest(x -> x.getName().equalsIgnoreCase("Box trap") && BLACK_CHINS.contains(x));
        if (fallenTrap != null && fallenTrap.interact()) {
            Sleep.sleepUntil(() -> !fallenTrap.exists(), 5000);
            return ReactionGenerator.getNormal();
        }
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

        if (config.isFailSafe()) {
            Logger.log("sleeping 90 seconds for your traps to be reset.");
            config.setFailSafe(false);
            return 90000;
        }

        if (config.getTrapMap().size() >= getTrapLimit()) {
            // FOR LOOP / MONITOR AND COLLECT TRAPS
            List<GameObject> trapList = GameObjects.all(x -> x.getName().toLowerCase().contains("box"));
            trapList.sort(Comparator.comparingInt(Entity::getID));
            config.setSubStatus("awaiting rodent");
            for (GameObject trap : trapList) {
                if (config.getTrapMap().get(trap.getTile()) != null) {
                    // if this trap is one of ours
                    if (!trap.exists()) {
                        config.trapMapRemove(trap.getTile());
                        break;
                    }
                    if (trap.getID() == 721 || trap.getID() == 9385) {
                        if (trap.interact()) {
                            config.setSubStatus("interaction sleep");
                            Sleep.sleepUntil(() -> !trap.exists(), 3700);
                            config.setSubStatus("");
                            break;
                        }
                    }
                }
            }
            return ReactionGenerator.getNormal();
        }
        // PLACE TRAPS
        if (getBestTile() != null && Players.getLocal().getTile().equals(getBestTile())) {
            if (!shouldHop() && Inventory.contains("box trap") && Inventory.interact("box trap", "lay")) {
                config.setSubStatus("place trap sleep");
                Sleep.sleepUntil(() -> GameObjects.getTopObjectOnTile(Players.getLocal().getTile()) != null
                                && GameObjects.getTopObjectOnTile(Players.getLocal().getTile()).getName().contains("trap"),
                        5000);
                Sleep.sleepUntil(() -> Players.getLocal().isStandingStill(), 1500);
                config.setSubStatus("");
            }
        } else {
            if (Walking.shouldWalk(6)) Walking.walkOnScreen(getBestTile());
            Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(getBestTile()), 1700);
        }
        return ReactionGenerator.getNormal();
    }


    @Override
    public void onGameObjectSpawn(GameObject object) {
        if (object.getName().contains("trap")) {
            if (Players.getLocal().getTile().equals(object.getTile())) {
                // this is to stop boxes failing to catch getting added
                if (object.getID() == 9380) {
                    Logger.log("----------------------------------------------");
                    Logger.log("Ani: " + Players.getLocal().getAnimation());
                    Logger.log("Dist: " + object.distance(Players.getLocal()));
                    Logger.log("----------------------------------------------");
                    if (Players.getLocal().getAnimation() == 5208) {
                        config.trapMapPut(object.getTile(), object);
                    }
                }
            }
        }
    }


    private boolean shouldHop() {
        if (ScriptSettings.getSettingsData().avoidCompetition && BLACK_CHINS.contains(Players.getLocal())) {
            return Players.closest(x -> !x.getName().equals(Players.getLocal().getName()) && x.distance() < 6) != null;
        }
        return false;
    }
}

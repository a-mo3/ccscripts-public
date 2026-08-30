package org.dreambot.behaviour.impl;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.config.Config;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.dreambot.behaviour.BehaviourUtils.*;

public class BlackChinsNode extends Fractal {
    public BlackChinsNode() {
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.BOX_TRAP, 6 - config.getTrapMap().size(), 20)
                .strictIgnore(ItemID.BLACK_CHINCHOMPA)
                .setStrict(true)
        ;

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
        ;
    }

    private final Config config = Config.getConfig();

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        // todo get the black chin area and guard clause here
        if (!BLACK_CHINS.contains(Players.getLocal())) {
            stdWalk(BLACK_CHINS);
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
            if (Inventory.contains("box trap") && Inventory.interact("box trap", "lay")) {
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
}

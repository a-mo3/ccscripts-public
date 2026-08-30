package org.dreambot.behaviour.quests.perilousmoon;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Supplier;

/**
 * you have an infused earth talisman that tells you east or west
 * and an infused water talisman that tells you north or south
 * use them both in the antechamber to locate which room, and then in the room to locate eyat
 */
public class LocateEyatlali extends Fractal implements ChatListener {
    // go to the antechamber and use the talismans, once they are used you what room to go to
    Boolean antechamberNorth = null;
    Boolean antechamberEast = null;

    public static final Area STEAMBOUND = new Area(1473, 9722, 1536, 9665);
    public static final Area EARTHBOUND = new Area(1340, 9738, 1410, 9667);
    public static final Area PRISON = new Area(1344, 9594, 1390, 9538);
    public static final Area REWARDS = new Area(1494, 9598, 1529, 9562);
    public static final Area ANTECHAMBER = new Area(1432, 9640, 1448, 9624, 1);

    // the area eyat is located, determined by checking talismans in the antechamber
    Area eyatLocation;
    Tile searchTile;

    public LocateEyatlali(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Find Eyatlali");
        Client.getInstance().addEventListener(this);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.INFUSED_EARTH_TALISMAN)
//                .setEnabledCondition(() -> OwnedItems.contains(ItemID.INFUSED_EARTH_TALISMAN))
                .addItem(ItemID.INFUSED_WATER_TALISMAN)
//                .setEnabledCondition(() -> OwnedItems.contains(ItemID.INFUSED_WATER_TALISMAN))

//                .addItem(ItemID.ENCHANTED_EARTH_TALISMAN)
//                .setEnabledCondition(() -> OwnedItems.contains(ItemID.ENCHANTED_EARTH_TALISMAN))
//                .addItem(ItemID.ENCHANTED_WATER_TALISMAN)
//                .setEnabledCondition(() -> OwnedItems.contains(ItemID.ENCHANTED_WATER_TALISMAN))
        ;
    }

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            log("Dialogue handle");
            Dialog.solve();
            return ReactionGenerator.getNormal();
        }

        if (antechamberEast == null || antechamberNorth == null) {
            if (!ANTECHAMBER.contains(Players.getLocal())) {
                log("Go to antechamber");
                if (Walking.shouldWalk()) Walking.walk(ANTECHAMBER);
                return ReactionGenerator.getNormal();
            }

            log("Check talismans");
            Inventory.interact("Infused earth talisman");
            Inventory.interact("Infused water talisman");
            return ReactionGenerator.getNormal();
        }

        if (eyatLocation == null) {
            if (antechamberNorth) {
                if (antechamberEast) {
                    // north east is steambound
                    log("Steambound target room");
                    eyatLocation = STEAMBOUND;
                } else {
                    log("Target room earthbound");
                    eyatLocation = EARTHBOUND;
                }
            } else {
                if (antechamberEast) {
                    log("Target room rewards");
                    eyatLocation = REWARDS;
                } else {
                    log("Target room prison");
                    eyatLocation = PRISON;
                }
            }
            return ReactionGenerator.getNormal();
        }

        if (!eyatLocation.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) {
                log("Go to eyat loc");
                Walking.walk(eyatLocation);
            }
            return ReactionGenerator.getNormal();
        }

        if (searchTile == null) {
            searchTile = Players.getLocal().getTile();
            return ReactionGenerator.getNormal();
        }

        if (!searchTile.canReach()) {
            log("Non reachable search tile");
            searchTile = Arrays.stream(searchTile.getArea(4)
                    .getTiles())
                    .filter(Tile::canReach)
                    .max(Comparator.comparingDouble(Tile::distance))
                    .orElse(null);
            return ReactionGenerator.getNormal();
        }

        log("Seach " + searchTile + " " + Players.getLocal().getTile());

        if (searchTile.equals(Players.getLocal().getTile())) {
            log("Check talismans");
            Inventory.interact("Infused earth talisman");
            Inventory.interact("Infused water talisman");
            return ReactionGenerator.getNormal() + 3000;
        } else {
            log("Walk onto search tile");
            if (Walking.shouldWalk()) Walking.walkOnScreen(searchTile);
        }

        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        String txt = message.getMessage();
        if (txt == null) return;
        if (ANTECHAMBER.contains(Players.getLocal())) {
            // check for what room eyat is in
            if (txt.contains("east")) antechamberEast = true;
            if (txt.contains("west")) antechamberEast = false;

            if (txt.contains("north")) antechamberNorth = true;
            if (txt.contains("south")) antechamberNorth = false;
            return;
        }

        if (searchTile == null) return;
        if (txt.contains("east")) searchTile.translate(2, 0);
        if (txt.contains("west")) searchTile.translate(-2, 0);
        if (txt.contains("north")) searchTile.translate(0, 2);
        if (txt.contains("south")) searchTile.translate(0, -2);
    }
}

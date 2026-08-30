package org.dreambot.behaviour.training.farming;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class BuildPlantSpots extends Fractal {
    // comes default near plant
    static String plantSpotName = "Small Plant space 1";
    static final Area RIMMINGTON = new Area(2940, 3231, 2972, 3201);

    public BuildPlantSpots(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        inventoryLoadout = new InventoryLoadout()
                .addItem(ItemVariants.WATERING_CAN) // its not optimal but its just easier to have one
                .addItem(ItemID.BAGGED_PLANT_1, 1, 25)
                .setRefill(820)
                .addItem(ItemID.TELEPORT_TO_HOUSE, 1, 50)
                .setRefill(250)
                .addItem(ItemID.VARROCK_TELEPORT, 1, 50)
                .setRefill(250)
        ;
    }

    @Override
    public int onLoop() {
        // fill watering cans
        if (!Inventory.contains(x -> x.getName().contains("Watering can("))) {

            Logger.info("Fill watering cans");
            if (Client.isDynamicRegion()) {
                Logger.info("Exit house");
                GameObject exit = GameObjects.closest(x -> x.hasAction("Enter"));
                if (exit != null) exit.interact("Enter");
                return ReactionGenerator.getNormal();
            }

            if (!RIMMINGTON.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(RIMMINGTON);
                return ReactionGenerator.getNormal();
            }

            GameObject well = GameObjects.closest("Sink");
            Item wateringCan = Inventory.get(ItemID.WATERING_CAN);

            if (well != null && wateringCan != null) {
                wateringCan.useOn(well);
                Sleep.sleepUntil(() -> !Inventory.contains(ItemID.WATERING_CAN), () -> Players.getLocal().isAnimating(), 6400, 100);
            }
            return ReactionGenerator.getNormal();
        }


        // enter house
        if (!Client.isDynamicRegion()) {
            GameObject housePortal = GameObjects.closest(x -> x.getName().equals("Portal") && x.hasAction("Build mode"));
            if (housePortal != null) {
                Logger.info("Enter house");
                housePortal.interact("Build mode");
                Sleep.sleepUntil(Client::isDynamicRegion, 6400);
                return ReactionGenerator.getNormal();
            }

            if (Widgets.isOpen()) Widgets.closeAll();
            if (Inventory.interact(ItemID.TELEPORT_TO_HOUSE, "Outside")) {
                Sleep.sleepUntil(() -> GameObjects.closest(x -> x.getName().equals("Portal") && x.hasAction("Build mode")) != null,
                        12_000);
            }
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("Yes");
            return ReactionGenerator.getNormal();
        }

        // build plants
        WidgetChild buildJaunt = Widgets.get(458, 4);
        if (buildJaunt != null && buildJaunt.isVisible()) {
            Logger.info("Build plant");
            buildJaunt.interact();
            Sleep.sleepUntil(() -> GameObjects.closest(x -> x.getName().equals("Plant") && x.hasAction("Remove")) != null, 4400);
            return ReactionGenerator.getNormal();
        }

        GameObject plant = GameObjects.closest(x -> x.getName().equals("Plant") && x.hasAction("Remove"));
        if (plant != null) {
            Logger.info("Removing plant");
            plant.interact("Remove");
            Sleep.sleepUntil(Dialogues::inDialogue, 4400);
            return ReactionGenerator.getNormal();
        }

        GameObject plantSpot = GameObjects.closest(plantSpotName);
        if (plantSpot != null) {
            plantSpot.interact("Build");
        }
        return ReactionGenerator.getNormal();
    }
}

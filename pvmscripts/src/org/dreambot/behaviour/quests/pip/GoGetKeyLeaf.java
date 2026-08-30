package org.dreambot.behaviour.quests.pip;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Comparator;
import java.util.List;

public class GoGetKeyLeaf extends Fractal {
    // this will be used to track the monument that had the key on it
    int monumentID = 0;

    @Override
    public boolean isValid() {
        return PaidQuest.PRIEST_IN_PERIL.getConfigValue() == 5;
    }

    @Override
    public int onLoop() {
        //Client.setInteractionMode(InteractionMode.INSTANT);
        if (Inventory.contains(ItemID.MURKY_WATER) && Inventory.contains(ItemID.IRON_KEY)) {
            // exit monument dungeon
            if (Players.getLocal().getY() > 4000) {
                if (!PriestInPerilAreas.EXIT_MONUMENT.contains(Players.getLocal())) {
                    Walking.walk(PriestInPerilAreas.EXIT_MONUMENT.getCenter());
                    return ReactionGenerator.getNormal();
                }
                GameObject ladder = GameObjects.closest("Ladder");
                if (ladder != null && ladder.interact("Climb-up")) {
                    Sleep.sleepUntil(() -> Players.getLocal().getY() < 4000, 5000);
                }
            }
            // goto third floor and free that nigga
            if (PriestInPerilAreas.TEMPLE_THIRD_FLOOR.contains(Players.getLocal())) {
                Item ironKey = Inventory.get(ItemID.IRON_KEY);
                GameObject cellDoor = GameObjects.closest("Cell door");
                //Client.setInteractionMode(InteractionMode.INSTANT);
                if (ironKey != null && cellDoor != null && ironKey.useOn(cellDoor)) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 5000); // stage ends here
                    Dialog.solve();
                    return ReactionGenerator.getNormal();
                }
            } else {
                if (!PriestInPerilAreas.TEMPLE_FIRST_FLOOR.contains(Players.getLocal()) && Players.getLocal().getZ() == 0) {
                    Walking.walk(PriestInPerilAreas.TEMPLE_FIRST_FLOOR.getCenter());
                    return ReactionGenerator.getNormal();
                }
//                Log.info("Climbing...");
                GameObject stairs = GameObjects.closest("Staircase");
                GameObject ladder = GameObjects.closest("Ladder");
                int plane = Players.getLocal().getZ();
                if (stairs != null && stairs.interact("Climb-up")) {
                    Sleep.sleepUntil(() -> plane != Players.getLocal().getZ(), 5000);
                    return ReactionGenerator.getNormal();
                }
//                Log.info("ladder");
                if (ladder != null && ladder.interact("Climb-up")) {
                    Sleep.sleepUntil(() -> plane != Players.getLocal().getZ(), 5000);
                    return ReactionGenerator.getNormal();
                }
            }
            return ReactionGenerator.getNormal();
        }

        // replace gold key with iron key and get water
        if (!PriestInPerilAreas.MONUMENT_ROOM.contains(Players.getLocal())) {
            if (Players.getLocal().getY() < 9000) {
                // if ur not in the dungeon, (walkers entrance node is busted)
                if (!PriestInPerilAreas.IN_FRONT_OF_TRAPDOOR.contains(Players.getLocal())) {
                    Walking.walk(PriestInPerilAreas.IN_FRONT_OF_TRAPDOOR.getCenter());
                    return ReactionGenerator.getNormal();
                }
                GameObject trapDoor = GameObjects.closest("Trapdoor");
                if (trapDoor != null && trapDoor.hasAction("Open") && trapDoor.interact("Open")) {
                    Sleep.sleepUntil(() -> !trapDoor.hasAction("Open"), 5000);
                    return ReactionGenerator.getNormal();
                }
                if (trapDoor != null && trapDoor.interact()) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 5000);
                    if (Dialogues.inDialogue()) {
                        Dialog.solve("Yes");
                    }
                }
            }
            Walking.walk(PriestInPerilAreas.MONUMENT_ROOM.getCenter());
            return ReactionGenerator.getNormal();
        }

        /*
            when you study a monument widget 272, 8 will hold the ID of the item we are looking for
            iron key id is 2945
         */
        if (monumentID == 0 && !Inventory.contains("Iron key")) {
            List<GameObject> monumentList = GameObjects.all(x -> x.getName().equals("Monument"));
            monumentList.sort(Comparator.comparingDouble(GameObject::distance));
            for (GameObject monument : monumentList) {
                if (Widgets.isOpen()) {
                    Widgets.closeAll();
                    // return ReactionGenerator.getNormal();
                }

                if (monument != null && monument.interact("Study")) {
                    Sleep.sleepUntil(() -> Widgets.get(272, 8) != null
                            && Widgets.get(272, 8).isVisible(), 15_000);
                    if (Widgets.get(272, 8).getItem().getId() == 2945) {
                        Log.info("found key, monument: " + monument.getId());
                        monumentID = monument.getId();
                        break;
                    }
                }
                Sleep.sleep(1000, 2000);
            }
        }

        if (Widgets.isOpen()) {
            Widgets.closeAll();
            return ReactionGenerator.getNormal();
        }

        GameObject keyMonument = GameObjects.closest(monumentID);
        Item goldenKey = Inventory.get("Golden key");
        if (goldenKey != null && keyMonument != null) {
            if (goldenKey.useOn(keyMonument)) {
                return 5000;
            }
        }

        Item bucket = Inventory.get("Bucket");
        GameObject well = GameObjects.closest("Well");
        if (bucket != null && well != null && bucket.useOn(well)) {
            Sleep.sleepUntil(() -> Inventory.contains("Murky water"), 5000);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}

package org.dreambot.behaviour.quest;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.GenericEntityInteraction;
import org.dreambot.generics.SleepFractal;
import org.dreambot.loadouts.InventoryLoadoutItem;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.utility.OwnedItems;

import java.util.function.BooleanSupplier;

public class RomeoAndJuliet extends IronFractal {
    public RomeoAndJuliet(BooleanSupplier acceptCondition) {
        super(acceptCondition);
        setSimpleName("Romeo and Juliet");

        InventoryLoadoutItem berries = new InventoryLoadoutItem(ItemID.CADAVA_BERRIES)
                .setRestockMethod(
                        new GenericEntityInteraction(() -> !OwnedItems.contains(ItemID.CADAVA_BERRIES),
                                () -> GameObjects.closest("Cadava bush"))
                                .setEntityLocation(new Area(3262, 3375, 3279, 3361))
                                .setAction("Pick-from")
                                .setSimpleName("Cadava berries")
                );

        InventoryLoadoutItem potion = new InventoryLoadoutItem(ItemID.CADAVA_POTION)
                .setRestockMethod(
                        new GenericEntityInteraction(() -> !OwnedItems.contains(ItemID.CADAVA_POTION),
                                "Apothecary", new Tile(3195, 3405, 0), 4)
                                .setDialogueChoices("Romeo", "something else")
                                .addInventoryItem(berries)
                );


        InventoryLoadoutItem letter = new InventoryLoadoutItem(ItemID.MESSAGE)
                .setRestockMethod(
                        new GenericEntityInteraction(() -> !OwnedItems.contains(ItemID.MESSAGE), "Juliet",
                                new Tile(3158, 3427, 1), 5)
                                .setDoReachCheck(true)
                                .setDialogueChoices("Ok")
                );

        addChildren(
                new SleepFractal(Client::isInCutscene)
                        .setHandleDialogue(true)
                        .setSimpleName("Cutscene"),
                // Start quest
                new GenericEntityInteraction(() -> !FreeQuest.ROMEO_AND_JULIET.isStarted(),
                        "Romeo", new Tile(3211, 3422, 0), 5)
                        .setDialogueChoices("Yes"),

                new GenericEntityInteraction(() -> state() <= 20,
                        "Romeo", new Tile(3211, 3422, 0), 5)
                        .setDialogueChoices("Yes")
                        .addInventoryItem(letter),

                new GenericEntityInteraction(() -> state() == 30,
                        "Father Lawrence", new Tile(3254, 3483, 0), 5)
                        .setDialogueChoices("Ok"),

                new GenericEntityInteraction(() -> state() <= 50,
                        "Juliet",
                        new Tile(3158, 3427, 1), 5)
                        .setDoReachCheck(true)
                        .addInventoryItem(potion),

                new GenericEntityInteraction(() -> state() == 60,
                        "Romeo", new Tile(3211, 3422, 0), 5)
                        .setDialogueChoices("Yes")
        );
    }

    private int state() {
        return FreeQuest.ROMEO_AND_JULIET.getConfigValue();
    }
}

package org.dreambot.behaviour.quest.cooksassistant;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.GenericEntityInteraction;
import org.dreambot.generics.GenericItemUse;
import org.dreambot.loadouts.InventoryLoadoutItem;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.loadouts.data.ItemSpawn;
import org.dreambot.utility.OwnedItems;

import java.util.function.BooleanSupplier;

public class CooksAssistant extends IronFractal {
    public CooksAssistant(BooleanSupplier acceptCondition) {
        super(acceptCondition);
        InventoryLoadoutItem egg = new InventoryLoadoutItem(ItemID.EGG)
                .setRestockMethod(new GenericEntityInteraction(ItemSpawn.LUM_EGG));

        InventoryLoadoutItem bucket = new InventoryLoadoutItem(ItemID.BUCKET)
                .setRestockMethod(new GenericEntityInteraction(ItemSpawn.LUM_BUCkET).setDoReachCheck(true));

        InventoryLoadoutItem bucketOfMilk = new InventoryLoadoutItem(ItemID.BUCKET_OF_MILK)
                .setRestockMethod(
                        new GenericItemUse(() -> !OwnedItems.contains(ItemID.BUCKET_OF_MILK), () -> GameObjects.closest("Dairy cow"), ItemID.BUCKET)
                                .setLocation(new Area(3249, 3284, 3258, 3273))
                                .setSleepCondition(() -> Inventory.contains(ItemID.BUCKET_OF_MILK))
                                .setSleepTime(4000)
                                .addInventoryItem(bucket)
                                .setSimpleName("Milk cow")
                );

        InventoryLoadoutItem potOfFlour = new InventoryLoadoutItem(ItemID.POT_OF_FLOUR)
                .setRestockMethod(new MakeFlour(() -> !OwnedItems.contains(ItemID.POT_OF_FLOUR), 1));

        setSimpleName("Cooks ass.");
        addChildren(
                new GenericEntityInteraction(() -> FreeQuest.COOKS_ASSISTANT.getConfigValue() == 1, "Cook", new Tile(3206, 3214, 0), 4)
                        .setDialogueChoices("wrong", "Yes"),
                new GenericEntityInteraction(() -> true, "Cook", new Tile(3206, 3214, 0), 4)
                        .setDialogueChoices("wrong", "Yes")
                        .addInventoryItem(bucketOfMilk)
                        .addInventoryItem(egg)
                        .addInventoryItem(potOfFlour)
        );
    }
}

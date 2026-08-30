package org.dreambot.behaviour.quest;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.GenericEntityInteraction;
import org.dreambot.loadouts.InventoryLoadout;
import org.dreambot.loadouts.InventoryLoadoutItem;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.loadouts.data.ItemSpawn;
import org.dreambot.utility.OwnedItems;

import java.util.function.BooleanSupplier;

public class SheepShearer extends IronFractal {
    public SheepShearer(BooleanSupplier acceptCondition) {
        super(acceptCondition);

        InventoryLoadoutItem shears = new InventoryLoadoutItem(ItemID.SHEARS)
                .setRestockMethod(
                        new GenericEntityInteraction(ItemSpawn.SHEARS)
                );

        InventoryLoadoutItem twentyWool = new InventoryLoadoutItem(ItemID.WOOL)
                .setInventoryMin(1).setInventoryMax(20)
                .setRestockMethod(
                        new GenericEntityInteraction(() -> OwnedItems.count(ItemID.WOOL) < 20,
                                new Area(3193, 3276, 3212, 3257),
                                () -> NPCs.closest(n -> "Sheep".equals(n.getName()) && n.hasAction("Shear") && !n.hasAction("Talk-to")))
                                .setInventoryLoadout(new InventoryLoadout().addItem(shears)
                                        .setStrictIgnore(x -> x.getId() == ItemID.WOOL)
                                        .setStrict(() -> true)
                                )
                                .setSimpleName("Shear sheep")
                );

        InventoryLoadoutItem twentySpunWool = new InventoryLoadoutItem(ItemID.BALL_OF_WOOL)
                .setInventoryMin(20).setInventoryMax(20)
                .setRestockMethod(
                        new GenericEntityInteraction(() -> OwnedItems.count(ItemID.BALL_OF_WOOL) < 20, "Spinning wheel",
                                new Area(3207, 3217, 3212, 3212, 1))
                                .setProcessingItem(ItemID.BALL_OF_WOOL)
                                .addInventoryItem(twentyWool)
                );

        Area fredHouse = new Area(3184, 3279, 3192, 3270);
        addChildren(
                new GenericEntityInteraction(() -> true, "Fred the farmer", fredHouse)
                        .setDialogueChoices("Yes", "quest")
                        .addInventoryItem(twentySpunWool)
        );
        setSimpleName("Sheep shearer");
    }
}

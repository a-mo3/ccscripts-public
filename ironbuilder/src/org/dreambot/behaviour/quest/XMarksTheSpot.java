package org.dreambot.behaviour.quest;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.GenericEntityInteraction;
import org.dreambot.generics.GenericItemUse;
import org.dreambot.loadouts.InventoryLoadoutItem;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.loadouts.data.ItemSpawn;

import java.util.function.BooleanSupplier;

public class XMarksTheSpot extends IronFractal {
    public XMarksTheSpot(BooleanSupplier acceptCondition) {
        super(acceptCondition);
        setSimpleName("X marks the spot");
        final Tile VEOS_LUM_TILE = new Tile(3228, 3242, 0);
        final Tile OUTSIDE_BOB = new Tile(3230, 3209, 0);
        final Tile CASTLE = new Tile(3203, 3212, 0);
        final Tile DRAYNOR = new Tile(3109, 3264, 0);
        final Tile MARTIN = new Tile(3078, 3259, 0);
        final Tile VEOS_SARIM = new Tile(3054, 3245, 0);
        // 23068
        // 23069
        // 23070
        // chest 23071
        InventoryLoadoutItem spade = new InventoryLoadoutItem(ItemID.SPADE)
                .setRestockMethod(new GenericEntityInteraction(ItemSpawn.SPADE));

        addChildren(
                new GenericEntityInteraction(() -> state() < 2, "Veos", VEOS_LUM_TILE)
                        .addInventoryItem(spade),

                new GenericItemUse(() -> state() == 2, "Spade")
                        .setLocation(OUTSIDE_BOB.getArea(1))
                        .addInventoryItem(spade),

                new GenericItemUse(() -> state() == 3, "Spade")
                        .setLocation(CASTLE.getArea(1))
                        .addInventoryItem(spade),

                new GenericItemUse(() -> state() == 4, "Spade")
                        .setLocation(DRAYNOR.getArea(0))
                        .addInventoryItem(spade),

                new GenericItemUse(() -> state() == 5, "Spade")
                        .setLocation(MARTIN.getArea(0))
                        .addInventoryItem(spade),

                new GenericEntityInteraction(() -> true, "Veos", VEOS_SARIM)
                        .addInventoryItem(spade)
        );
    }

    private int state() {
        return FreeQuest.X_MARKS_THE_SPOT.getConfigValue();
    }
}

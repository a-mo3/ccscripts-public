package org.dreambot.behaviour.training.quests;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GoDoFractal;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

public class XMarksTheSpot extends Fractal {
    final Tile VEOS_LUM_TILE = new Tile(3228, 3242, 0);
    final Tile OUTSIDE_BOB = new Tile(3230, 3209, 0);
    final Tile CASTLE = new Tile(3203, 3212, 0);
    final Tile DRAYNOR = new Tile(3109, 3264, 0);
    final Tile MARTIN = new Tile(3078, 3259, 0);
    final Tile VEOS_SARIM = new Tile(3054, 3245, 0);

    InventoryLoadout spadeLoadout = new InventoryLoadout()
            .addItem(ItemID.SPADE)
            .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995));

    public XMarksTheSpot() {
        this.acceptCondition = () -> !FreeQuest.X_MARKS_THE_SPOT.isFinished();
        this.paintArraySupplier = () -> new String[]{
                "State " + FreeQuest.X_MARKS_THE_SPOT.getState(),
                "Progress " + FreeQuest.X_MARKS_THE_SPOT.getConfigValue(),
        };
        addChildren(
                new TalkToFractal(() -> FreeQuest.X_MARKS_THE_SPOT.getConfigValue() < 2, VEOS_LUM_TILE, () -> NPCs.closest("Veos"))
                        .setDialogueOptions("I'm looking for a quest", "Sounds good, what should I do?", "Can I help?", "Yes.", "Can you take me")
                        .setInventoryLoadout(spadeLoadout).setSimpleName("Start @ Veos"),

                new GoDoFractal(() -> FreeQuest.X_MARKS_THE_SPOT.getConfigValue() == 2, OUTSIDE_BOB, () -> {
                    Inventory.interact(ItemID.SPADE, "Dig");
                    return ReactionGenerator.getLong();
                }).setInventoryLoadout(spadeLoadout).setSimpleName("Dig outside Bobs"),

                new GoDoFractal(() -> FreeQuest.X_MARKS_THE_SPOT.getConfigValue() == 3, CASTLE, () -> {
                    Inventory.interact(ItemID.SPADE, "Dig");
                    return ReactionGenerator.getLong();
                }).setInventoryLoadout(spadeLoadout).setSimpleName("Dig @ castle"),

                new GoDoFractal(() -> FreeQuest.X_MARKS_THE_SPOT.getConfigValue() == 4, DRAYNOR, () -> {
                    Inventory.interact(ItemID.SPADE, "Dig");
                    return ReactionGenerator.getLong();
                }).setInventoryLoadout(spadeLoadout).setSimpleName("Dig @ Draynor"),

                new GoDoFractal(() -> FreeQuest.X_MARKS_THE_SPOT.getConfigValue() == 5, MARTIN, () -> {
                    Inventory.interact(ItemID.SPADE, "Dig");
                    return ReactionGenerator.getLong();
                }).setInventoryLoadout(spadeLoadout).setSimpleName("Dig @ Martin"),

                //60think id can be different if you have / havent gone to kourend before
                new TalkToFractal(() -> FreeQuest.X_MARKS_THE_SPOT.getConfigValue() >= 6, VEOS_SARIM, () -> NPCs.closest("Veos"))
                        .setDialogueOptions("I'm looking for a quest", "Sounds good, what should I do?", "Can I help?", "Yes.", "Can you take me", "where you are")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(23071)) // ancient casket
                        .setInventoryLoadout(spadeLoadout).setSimpleName("Talk to Veos @ Sarim (Fin)")
        );
    }
}

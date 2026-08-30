package org.dreambot.behaviour.quests;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;

public class GoblinDiplomacy extends Fractal {
    public GoblinDiplomacy() {
        super(() -> !FreeQuest.GOBLIN_DIPLOMACY.isFinished());

        InventoryLoadout startLoadout = new InventoryLoadout()
                .addItem(ItemID.GOBLIN_MAIL, 3)
                .addItem(ItemID.ORANGE_DYE)
                .addItem(ItemID.BLUE_DYE);

        this.paintArraySupplier = () -> new String[]{
                "Goblin Diplomacy: " + FreeQuest.GOBLIN_DIPLOMACY.getConfigValue(),
                "Cutscene: " + Client.isInCutscene()
        };

        addChildren(
                new TalkToFractal(() -> !FreeQuest.GOBLIN_DIPLOMACY.isStarted(), new Tile(2958, 3512, 0).getArea(5), () -> NPCs.closest("General Bentnoze"))
                        .setDialogueOptions(
                                "Yes.",
                                "So how is life for the goblins?",
                                "Yes, Wartface looks fat",
                                "Do you want me to pick an armour colour for you?",
                                "What about a different colour?",
                                "I have some orange armour here."
                        )
                        .setInventoryLoadout(startLoadout)
                        .setSimpleName("Start"),
                new UseOnFractal(() -> FreeQuest.GOBLIN_DIPLOMACY.getConfigValue() == 3, () -> Inventory.get(ItemID.ORANGE_GOBLIN_MAIL), () -> NPCs.closest("General Wartface"), true)
                        .setArea(new Tile(2958, 3512, 0).getArea(5))
                        .setDialogueOptions("")
                        .setReturnAfterDialogues(true)
                        .setSleepCondition(Client::isDynamicRegion)
                        .setAppendLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Dialog.solve();
                                return true;
                            }

                            if (Inventory.contains(ItemID.ORANGE_DYE)) {
                                Inventory.combine(ItemID.ORANGE_DYE, ItemID.GOBLIN_MAIL);
                                return true;
                            }

                            if (Inventory.contains(ItemID.BLUE_DYE)) {
                                Inventory.combine(ItemID.BLUE_DYE, ItemID.GOBLIN_MAIL);
                                return true;
                            }

                            return false;
                        }).setSimpleName("Cutscene"),
                new UseOnFractal(() -> FreeQuest.GOBLIN_DIPLOMACY.getConfigValue() == 4, () -> Inventory.get(ItemID.BLUE_GOBLIN_MAIL), () -> NPCs.closest("General Wartface"), true)
                        .setArea(new Tile(2958, 3512, 0).getArea(5))
                        .setDialogueOptions("")
                        .setReturnAfterDialogues(true)
                        .setSleepCondition(Client::isDynamicRegion)
                        .setAppendLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Dialog.solve();
                                return true;
                            }
                            return false;
                        }).setSimpleName("Blue mail"),
                new UseOnFractal(() -> FreeQuest.GOBLIN_DIPLOMACY.getConfigValue() == 5, () -> Inventory.get(ItemID.GOBLIN_MAIL), () -> NPCs.closest("General Wartface"), true)
                        .setArea(new Tile(2958, 3512, 0).getArea(5))
                        .setDialogueOptions("")
                        .setReturnAfterDialogues(true)
                        .setSleepCondition(Client::isDynamicRegion)
                        .setAppendLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Dialog.solve();
                                return true;
                            }
                            return false;
                        }).setSimpleName("Finish")

        );
    }
}

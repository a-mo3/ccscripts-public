package org.dreambot.behaviour.training.quests;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.Arrays;
import java.util.List;

public class MonksFriend extends Fractal {
    public MonksFriend() {
        this.acceptCondition = () -> !PaidQuest.MONKS_FRIEND.isFinished();
        this.paintArraySupplier = () -> new String[]{
                "Monks friend: " + PaidQuest.MONKS_FRIEND.getConfigValue()
        };

        List<Integer> omadStates = Arrays.asList(0, 20);
        List<Integer> cedric = Arrays.asList(30, 40, 50, 60);

        Area stoneCircle = new Area(2560, 3224, 2564, 3219);

        addChildren(
                new TalkToFractal(() -> omadStates.contains(PaidQuest.MONKS_FRIEND.getConfigValue())
                        || (Inventory.contains("Child's blanket") && PaidQuest.MONKS_FRIEND.getConfigValue() == 10),
                        new Tile(2607, 3211, 0).getArea(5),
                        () -> NPCs.closest("Brother Omad"))
                        .setDialogueOptions(
                                "what's wrong?",
                                "help with?",
                                "Yes.",
                                "Brother cedric?",
                                "Where should I look?"
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.JUG_OF_WATER)
                                .addItem(ItemID.LOGS)
                                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY))
                        .setSimpleName("Brother Omad"),

                new TalkToFractal(() -> !Inventory.contains("Child's blanket") && PaidQuest.MONKS_FRIEND.getConfigValue() == 10,
                        new Tile(2570, 9604, 0).getArea(5),
                        () -> GroundItems.closest("Child's blanket"))
                        .setInteraction("Take")
                        .setAppendLogic(() -> {
                            if (Players.getLocal().getY() < 5000) {
                                if (!stoneCircle.contains(Players.getLocal())) {
                                    if (Walking.shouldWalk(8)) Walking.walk(stoneCircle);
                                }

                                GameObject ladder = GameObjects.closest(x -> x.getName().equals("Ladder") && x.getTile().equals(new Tile(2561, 3222, 0)));
                                if (ladder != null && ladder.interact("Climb-down")) {
                                    Sleep.sleepUntil(() -> Players.getLocal().getY() > 5000, 2400);
                                }
                                return true;
                            }

                            return false;
                        })
                        .setSimpleName("Get blanket"),

                new TalkToFractal(() -> PaidQuest.MONKS_FRIEND.getConfigValue() == 70,
                        new Tile(2607, 3211, 0).getArea(5),
                        () -> NPCs.closest("Brother Omad"))
                        .setDialogueOptions("Yes, I'd be happy to!")
                        .setSimpleName("Finish"),

                new TalkToFractal(() -> true,
                        new Tile(2614, 3258, 0).getArea(5),
                        () -> NPCs.closest("Brother Cedric"))
                        .setDialogueOptions("Yes, I'd be happy to!")
                        .setInventoryLoadout(
                                new InventoryLoadout()
                                        .addItem(ItemID.LOGS)
                                        .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995)))
                        .setSimpleName("Brother Cedric")


        );
    }
}

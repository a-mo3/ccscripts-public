package org.dreambot.behaviour.quests.earnestthechicken;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;

public class ErnestTheChicken extends Fractal implements ChatListener {
    final Area FISH_FOOD_SPOT = new Area(3107, 3361, 3110, 3354, 1);
    final Area KITCHEN = new Area(3097, 3366, 3101, 3364, 0);
    boolean killedFish = false;

    private boolean hasKilled() {
        if (Inventory.contains(ItemID.PRESSURE_GAUGE)) killedFish = true;
        return killedFish;
    }

    public ErnestTheChicken() {
        this.acceptCondition = () -> !FreeQuest.ERNEST_THE_CHICKEN.isFinished();

        Client.getInstance().addEventListener(this);
        this.paintArraySupplier = () -> new String[]{
                "Ernest the chicken: " + FreeQuest.ERNEST_THE_CHICKEN.getConfigValue()
        };

        addChildren(
                new TalkToFractal(() -> !FreeQuest.ERNEST_THE_CHICKEN.isStarted(), new Tile(3109, 3329, 0).getArea(3), () -> NPCs.closest("Veronica"))
                        .setDialogueOptions("Yes.")
                        .setInventoryLoadout(new InventoryLoadout().setStrictSupplier(() -> Inventory.getEmptySlots() < 8))
                        .setInventoryLoadout(new InventoryLoadout().setStrict(true))
                        .setSimpleName("Start @ Veronica"),

                new Fractal(() -> !Inventory.containsAll(ItemID.PRESSURE_GAUGE, ItemID.OIL_CAN, ItemID.RUBBER_TUBE))
                        .setSimpleName("Get Items")
                        .addChildren(
                                new TalkToFractal(() -> !Inventory.contains(ItemID.POISON, ItemID.POISONED_FISH_FOOD) && !hasKilled(),
                                        KITCHEN,
                                        () -> GroundItems.closest(ItemID.POISON))
                                        .setInteraction("Take")
                                        .setSimpleName("Get poison"),

                                new TalkToFractal(() -> !Inventory.contains(ItemID.FISH_FOOD, ItemID.POISONED_FISH_FOOD) && !hasKilled(),
                                        FISH_FOOD_SPOT,
                                        () -> GroundItems.closest(ItemID.FISH_FOOD))
                                        .setInteraction("Take")
                                        .setSimpleName("Get fish food"),


                                new TalkToFractal(() -> !Inventory.contains(ItemID.SPADE),
                                        new Tile(3120, 3359, 0).getArea(5),
                                        () -> GroundItems.closest(ItemID.SPADE))
                                        .setInteraction("Take")
                                        .setSimpleName("Get spade"),

                                new TalkToFractal(() -> !Inventory.contains(ItemID.KEY),
                                        new Tile(3085, 3361, 0).getArea(5),
                                        () -> GameObjects.closest("Compost heap"))
                                        .setInteraction("Search")
                                        .setSimpleName("Get spade"),

                                new UseOnFractal(() -> !Inventory.contains(ItemID.PRESSURE_GAUGE),
                                        () -> Inventory.get(ItemID.POISONED_FISH_FOOD),
                                        () -> GameObjects.closest("Fountain"), true)
                                        .setArea(new Tile(3088, 3335, 0).getArea(3))
                                        .setAppendLogic(() -> {
                                            if (hasKilled()) {
                                                if (Dialogues.inDialogue()) {
                                                    Dialog.solve();
                                                    return true;
                                                }

                                                GameObject fountain = GameObjects.closest("Fountain");
                                                if (fountain != null && fountain.interact("Search")) {
                                                    Sleep.sleepUntil(() -> Inventory.contains(ItemID.PRESSURE_GAUGE), 2000);
                                                }
                                                return true;
                                            }

                                            if (Inventory.containsAll(ItemID.FISH_FOOD, ItemID.POISON)) {
                                                Inventory.combine(ItemID.POISON, ItemID.FISH_FOOD);
                                                return true;
                                            }
                                            return false;
                                        })
                                        .setSimpleName("Poison fountain"),

                                new TalkToFractal(() -> !Inventory.contains(ItemID.RUBBER_TUBE),
                                        new Tile(3111, 3367, 0),
                                        () -> GroundItems.closest(ItemID.RUBBER_TUBE))
                                        .setInteraction("Take")
                                        .setSimpleName("Get rubber tube"),

                                new GetOilCan().setSimpleName("Get oil can")
                        ),
//                new Tile(3116, 3364, 2)
                new TalkToFractal(() -> true, new Tile(3116, 3364, 2), () -> NPCs.closest("Professor oddenstein"))
                        .setDialogueOptions("guy called Ernest.", "Change him back this instant!")
                        .setSimpleName("Unchicken Ernest")
                        .setAppendLogic(() -> {
                                    if (Players.getLocal().getY() > 5000) {
                                        GameObject ladder = GameObjects.closest("Ladder");
                                        if (ladder == null || ladder.distance() > 8) {
                                            if (Walking.shouldWalk()) Walking.walk(ladder);
                                            return true;
                                        } else {
                                            ladder.interact("Climb-up");
                                            return true;
                                        }
                                    }

                                    if (GetOilCan.INSIDE_AVAS_ROOM.contains(Players.getLocal())) {
                                        GameObject lever = GameObjects.closest("Lever");
                                        if (lever != null && lever.interact("Pull")) {
                                            Sleep.sleepUntil(() -> !GetOilCan.INSIDE_AVAS_ROOM.contains(Players.getLocal()), 2400);
                                        }
                                        return true;
                                    }

                                    return false;
                                }
                        )
        );
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().contains("then die and float to the")) {
            killedFish = true;
        }
    }
}

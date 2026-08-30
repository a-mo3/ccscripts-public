package org.dreambot.behaviour.quests.pip;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.CombatLoadouts;
import org.dreambot.behaviour.StandardCombat;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;

import java.util.Arrays;
import java.util.List;

public class PriestInPeril extends Fractal {
    public PriestInPeril() {
        this.acceptCondition = () -> PaidQuest.PRIEST_IN_PERIL.getConfigValue() < 61;

        this.paintArraySupplier = () -> new String[]{
                "PIP: " + PaidQuest.PRIEST_IN_PERIL.getConfigValue()
        };

        Area dungeonEntrance = new Area(3403, 3507, 3406, 3503);
        List<Integer> roaldStates = Arrays.asList(0, 3);
        Area nullArea = null;

        addChildren(
                new TalkToFractal(() -> roaldStates.contains(PaidQuest.PRIEST_IN_PERIL.getConfigValue()),
                        new Tile(3222, 3473, 0).getArea(3),
                        () -> NPCs.closest("King Roald"))
                        .setDialogueOptions(
                                "quest!",
                                "job",
                                "Yes."
                        )
                        .setAppendLogic(() -> {
                            if (Client.isDynamicRegion())  {
                                Magic.castSpell(Normal.HOME_TELEPORT);
                                Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 34_000);
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Start @ King Roald"),

                new TalkToFractal(() -> PaidQuest.PRIEST_IN_PERIL.getConfigValue() == 1,
                        new Tile(3408, 3488, 0).getArea(2),
                        () -> GameObjects.closest("Large door"))
                        .setInteraction("Open")
                        .setDialogueOptions(
                                "I'll get going.",
                                "Roald sent me to check on Drezel.",
                                "Sure. I'm a helpful person!")
                        .setEquipmentLoadout(CombatLoadouts.newerLoadout)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.BUCKET)
                                .addItem(ItemID.SHARK, 1, 12))
                        .setSimpleName("Talk to door"),

                new StandardCombat(() -> PaidQuest.PRIEST_IN_PERIL.getConfigValue() == 2,
                        null,
                        () -> NPCs.closest("Temple Guardian"),
                        ItemID.SHARK)
                        .setEquipmentLoadout(CombatLoadouts.newerLoadout)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.BUCKET)
                                .addItem(ItemID.SHARK, 1, 12)
                        )
                        .setAppendLogic(() -> {
                            if (!Client.isDynamicRegion()) {
                                if (Dialogues.inDialogue()) {
                                    Dialog.solve("Yes.");
                                    return true;
                                }

                                if (!dungeonEntrance.contains(Players.getLocal())) {
                                    if (Walking.shouldWalk()) Walking.walk(dungeonEntrance);
                                    return true;
                                }

                                GameObject trapDoor = GameObjects.closest("Trapdoor");
                                if (trapDoor != null) {
                                    if (trapDoor.interact("Climb-down")) {
                                        Sleep.sleepUntil(Client::isDynamicRegion, 1400);
                                        return true;
                                    }

                                    trapDoor.interact("Open");
                                    return true;
                                }
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Kill temple guardian"),

                new StandardCombat(() -> PaidQuest.PRIEST_IN_PERIL.getConfigValue() == 4 && !Inventory.contains(ItemID.GOLDEN_KEY),
                        new Tile(3412, 3488, 0).getArea(5),
                        () -> NPCs.closest(x -> x.getName().equals("Monk of Zamorak") && x.getLevel() == 30),
                        ItemID.SHARK)
                        .setLootFilter(x -> x.getID() == ItemID.GOLDEN_KEY)
                        .setAppendLogic(() -> {
                            GroundItem item = GroundItems.closest(ItemID.GOLDEN_KEY);
                            if (item != null) {
                                item.interact("Take");
                                return true;
                            }

                            return false;
                        })
                        .setSimpleName("Kill monk"),

                new TalkToFractal(
                        () -> PaidQuest.PRIEST_IN_PERIL.getConfigValue() == 4
                                || PaidQuest.PRIEST_IN_PERIL.getConfigValue() == 7
                                || (PaidQuest.PRIEST_IN_PERIL.getConfigValue() == 6 && !Inventory.contains(ItemID.BLESSED_WATER)),
                        new Tile(3418, 3489, 2).getArea(10),
//                        nullArea,
                        () -> NPCs.closest("Drezel"))
                        .setDoReachCheck(false)
                        .setDialogueOptions("So, what now?", "Yes, of course.")
                        .setAppendLogic(() -> {
                            if (Players.getLocal().getZ() < 2) {
                                GameObject stairs = GameObjects.closest(x -> x.getName().equals("Staircase") && x.hasAction("Climb-up"));
                                GameObject ladder = GameObjects.closest(x -> x.getName().equals("Ladder") && x.hasAction("Climb-up"));

                                if (stairs != null && stairs.interact("Climb-up")) {
                                    int z = Players.getLocal().getZ();
                                    Sleep.sleepUntil(() -> Players.getLocal().getZ() > z, 2400);
                                    return true;
                                }

                                if (ladder != null && ladder.interact("Climb-up")) {
                                    int z = Players.getLocal().getY();
                                    Sleep.sleepUntil(() -> Players.getLocal().getZ() > z, 2400);
                                    return true;
                                }

                                return true;
                            }

                            return false;
                        })
                        .setSimpleName("Drezel"),

                new GoGetKeyLeaf().setSimpleName("Get key leaf"),

                new UseOnFractal(
                        () -> PaidQuest.PRIEST_IN_PERIL.getConfigValue() == 6,
                        () -> Inventory.get(ItemID.BLESSED_WATER),
                        () -> GameObjects.closest("Coffin"), true)
                        .setSimpleName("Blessed water on coffin"),

                new TalkToFractal(
                        () -> true,
                        new Area(3437, 9900, 3442, 9891),
                        () -> NPCs.closest("Drezel"))
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.PURE_ESSENCE, 1, 28)
                                .setRefill(51))
                        .setSimpleName("Drezel")

        );
    }
}

package org.dreambot.behaviour.quests.dwarfcannon;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.data.NpcID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.InventoryLoadoutItem;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.quest.VarbitRequirement;

import java.util.function.Supplier;

public class DwarfCannon extends Fractal {
    final Tile START_LAWGOF = new Tile(2567, 3460, 0);
    //Varbits
    VarbitRequirement bar1 = new VarbitRequirement(2240, 1);
    VarbitRequirement bar2 = new VarbitRequirement(2241, 1);
    VarbitRequirement bar3 = new VarbitRequirement(2242, 1);
    VarbitRequirement bar4 = new VarbitRequirement(2243, 1);
    VarbitRequirement bar5 = new VarbitRequirement(2244, 1);
    VarbitRequirement bar6 = new VarbitRequirement(2245, 1);

    final Tile BAR_ONE = new Tile(2555, 3479, 0);
    final Tile BAR_TWO = new Tile(2557, 3468, 0);
    final Tile BAR_THREE = new Tile(2559, 3458, 0);
    final Tile BAR_FOUR = new Tile(2563, 3457, 0);
    final Tile BAR_FIVE = new Tile(2573, 3457, 0);
    final Tile BAR_SIX = new Tile(2577, 3457, 0);

    final Tile REMAINS = new Tile(2568, 3443, 2);
    final Area LOLLK = new Tile(2568, 9850, 0).getArea(5);
    final Tile NULODION_HOUSE = new Tile(3012, 3453, 0);
    final Area DUNGEON_ENTRANCE = new Area(2620, 3392, 2626, 3386);

    public DwarfCannon() {
        this.acceptCondition = () -> !PaidQuest.DWARF_CANNON.isFinished() && Skills.getRealLevel(Skill.HITPOINTS) > 25;
        this.paintArraySupplier = () -> new String[]{
                "Dwarf cannon: " + PaidQuest.DWARF_CANNON.getState()
        };

        Supplier<Boolean> barCondition = () -> {
            if (Players.getLocal().isAnimating()) return true;
            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 4) {
                Inventory.interact(ItemID.SHARK, "Eat");
                return true;
            }
            return false;
        };

        addChildren(
                new TalkToFractal(() -> !PaidQuest.DWARF_CANNON.isStarted() || (PaidQuest.DWARF_CANNON.getConfigValue() == 1 && !Inventory.contains("Railing")), START_LAWGOF, () -> NPCs.closest(NpcID.CAPTAIN_LAWGOF))
                        .setDialogueOptions("Yes.")
                        .setInventoryLoadout(
                                new InventoryLoadout()
                                        .addItem(ItemID.SHARK, 14)
                                        .addItem(ItemID.HAMMER)
                                        .addItem(new InventoryLoadoutItem(ItemVariants.STAMINA_POTION, 1, 3))
                                        .addItem(ItemVariants.SKILLS_NECKLACE)
                                        .addItem(ItemVariants.COMBAT_BRACLET)
                                        .setStrict(true)
                        )
                        .setSimpleName("Start @ Lawgof"),
                new Fractal(() -> PaidQuest.DWARF_CANNON.getConfigValue() == 1).addChildren(
                                new TalkToFractal(() -> !bar1.isComplete(), BAR_ONE, () -> GameObjects.closest(15590))
                                        .setAfterChat(() -> Players.getLocal().isAnimating())
                                        .setInteraction("Inspect").setSimpleName("Bar 1")
                                        .setAppendLogic(barCondition),
                                new TalkToFractal(() -> !bar2.isComplete(), BAR_TWO, () -> GameObjects.closest(15591))
                                        .setAfterChat(() -> Players.getLocal().isAnimating())
                                        .setInteraction("Inspect").setSimpleName("Bar 2")
                                        .setAppendLogic(barCondition),
                                new TalkToFractal(() -> !bar3.isComplete(), BAR_THREE, () -> GameObjects.closest(15592))
                                        .setAfterChat(() -> Players.getLocal().isAnimating())
                                        .setInteraction("Inspect").setSimpleName("Bar 3")
                                        .setAppendLogic(barCondition),
                                new TalkToFractal(() -> !bar4.isComplete(), BAR_FOUR, () -> GameObjects.closest(15593))
                                        .setAfterChat(() -> Players.getLocal().isAnimating())
                                        .setInteraction("Inspect").setSimpleName("Bar 4")
                                        .setAppendLogic(barCondition),
                                new TalkToFractal(() -> !bar5.isComplete(), BAR_FIVE, () -> GameObjects.closest(15594))
                                        .setAfterChat(() -> Players.getLocal().isAnimating())
                                        .setInteraction("Inspect").setSimpleName("Bar 5")
                                        .setAppendLogic(barCondition),
                                new TalkToFractal(() -> !bar6.isComplete(), BAR_SIX, () -> GameObjects.closest(15595))
                                        .setAfterChat(() -> Players.getLocal().isAnimating())
                                        .setInteraction("Inspect").setSimpleName("Bar 6")
                                        .setAppendLogic(barCondition),
                                new TalkToFractal(() -> true, START_LAWGOF, () -> NPCs.closest(NpcID.CAPTAIN_LAWGOF))
                                        .setDialogueOptions("Yes.")
                                        .setSimpleName("Fixed all bars."))
                        .setSimpleName("Fixing bars"),
                new TalkToFractal(() -> PaidQuest.DWARF_CANNON.getConfigValue() <= 3 && !Inventory.contains(0),
                        REMAINS, () -> GameObjects.closest("Dwarf remains"))
                        .setInteraction("Take")
                        .setSimpleName("Getting remains"),
                new TalkToFractal(() -> PaidQuest.DWARF_CANNON.getConfigValue() == 3, START_LAWGOF, () -> NPCs.closest(NpcID.CAPTAIN_LAWGOF))
                        .setSimpleName("Report remains to lawgof"),
                new TalkToFractal(() -> PaidQuest.DWARF_CANNON.getConfigValue() <= 5, LOLLK, () -> GameObjects.closest(1))
                        .setInteraction("Search").setSimpleName("Finding Lollk")
                        .setAppendLogic(() -> {
                            if (Players.getLocal().getY() < 5000) {
                                if (!DUNGEON_ENTRANCE.contains(Players.getLocal())) {
                                    if (Walking.shouldWalk()) Walking.walk(DUNGEON_ENTRANCE);
                                    return true;
                                }

                                GameObject object = GameObjects.closest("Cave Entrance");
                                if (object != null && object.interact("Enter")) {
                                    Sleep.sleepUntil(() -> Players.getLocal().getY() > 5000, 2400);
                                }
                                return true;
                            }
                            return false;
                        }),

                new TalkToFractal(() -> PaidQuest.DWARF_CANNON.getConfigValue() >= 9 && Inventory.count(ItemID.AMMO_MOULD) < 2,
                        NULODION_HOUSE, () -> NPCs.closest(NpcID.NULODION))
                        .setAppendLogic(() -> {
                            if (Inventory.count(ItemID.AMMO_MOULD) == 1) {
                                GroundItem mould = GroundItems.closest(ItemID.AMMO_MOULD);
                                if (mould != null) {
                                    mould.interact("Take");
                                    return true;
                                }
                                Inventory.drop(ItemID.AMMO_MOULD);
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Get ammo moulds"),

                new TalkToFractal(() -> PaidQuest.DWARF_CANNON.getConfigValue() != 7, START_LAWGOF, () -> NPCs.closest(NpcID.CAPTAIN_LAWGOF))
                        .setDialogueOptions("Okay, I'll see what I can do.", "Okay then, just for you!")
                        .setSimpleName("Talking to lawgof"),
                new ToolkitPuzzle(() -> PaidQuest.DWARF_CANNON.getConfigValue() == 7)
        );
    }
}

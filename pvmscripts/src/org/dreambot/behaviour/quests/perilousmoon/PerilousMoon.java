package org.dreambot.behaviour.quests.perilousmoon;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.map.Region;
import org.dreambot.behaviour.method.moonsofperil.MoonsOfPerilBranch;
import org.dreambot.behaviour.training.slayer.SlayerLoadouts;
import org.dreambot.behaviour.training.slayer.behaviour.StandardCombat;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.quest.VarbitRequirement;

import java.util.Arrays;

public class PerilousMoon extends Fractal {
    public PerilousMoon() {
        super(() -> !PaidQuest.PERILOUS_MOONS.isFinished());

        this.paintArraySupplier = () -> new String[]{
                "State " + PaidQuest.PERILOUS_MOONS.getConfigValue(),
        };

        PerilousMoonNodes.init();

        // to fit into the tiny nigga rune store
        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Door", "Squeeze-through"));

        Area nullArea = null;

        VarbitRequirement talkedToAttalaInNey = new VarbitRequirement(9823, 1);
        VarbitRequirement talkedToZumaInNey = new VarbitRequirement(9823, 2);

        VarbitRequirement madePrisonCamp = new VarbitRequirement(9820, 1);
        VarbitRequirement madeEarthCamp = new VarbitRequirement(9821, 1);
        VarbitRequirement madeStreamCamp = new VarbitRequirement(9822, 1);

        addChildren(
                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() < 2, new Tile(1435, 3124), () -> NPCs.closest("Attala"))
                        .setDialogueOptions("Yes.")
                        .setSimpleName("Start @ Attala"),

                new StandardCombat(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() == 2, new Tile(1452, 3136).getArea(5), () -> NPCs.closest("Sulphur nagua"))
                        .setFoodID(Arrays.asList(ItemID.SHARK))
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.SHARK, 1, 5)
                                .addItem(ItemID.PESTLE_AND_MORTAR, 1)
                                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995) || Inventory.emptySlotCount() < 6)
                        )
                        .setSimpleName("Kill sulphur nagua"),


                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() == 4, new Tile(1435, 3124), () -> NPCs.closest("Attala"))
                        .setDialogueOptions("Yes.")
                        .setSimpleName("Return to Attala"),

                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() < 7, new Tile(1441, 9596, 1), () -> NPCs.closest("Jessamine"))
                        .setDialogueOptions("dig?")
                        .setSimpleName("Jessamine"),

                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() < 10 && talkedToAttalaInNey.isNotComplete(), nullArea, () -> NPCs.closest("Attala"))
                        .setDialogueOptions("Goodbye.")
                        .setPrependLogic(() -> {
                            if (!Client.isDynamicRegion()) {
                                GameObject entrance = GameObjects.closest("Entrance");
                                if (entrance != null && entrance.interact("Pass-through")) {
                                    Sleep.sleepUntil(Client::isDynamicRegion, 4000);
                                }
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Talk to Attala in ney"),

                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() < 10, nullArea, () -> NPCs.closest("Zuma"))
                        .setDialogueOptions("Goodbye.")
                        .setPrependLogic(() -> {
                            if (!Client.isDynamicRegion()) {
                                GameObject entrance = GameObjects.closest("Entrance");
                                if (entrance != null && entrance.interact("Pass-through")) {
                                    Sleep.sleepUntil(Client::isDynamicRegion, 4000);
                                }
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Talk to Zuma in ney"),

                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() == 10 && Inventory.contains("Building supplies") && madePrisonCamp.isNotComplete(),
                        nullArea,
                        () -> GameObjects.closest("Camp spot"))
                        .setInteraction("Build")
                        .setPrependLogic(() -> {
                            Tile campTile = new Tile(1352, 9581, 0);
                            Logger.info(Region.toInstance(campTile));
                            if (Region.toInstance(campTile).isEmpty() || Region.toInstance(campTile).get(0).distance() > 15) {
                                InstanceWalking.walk(campTile);
                                return true;
                            }

                            return false;
                        })
                        .setSimpleName("Build prison camp"),

                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() == 10 && Inventory.contains("Building supplies") && madeEarthCamp.isNotComplete(),
                        nullArea,
                        () -> GameObjects.closest("Camp spot"))
                        .setInteraction("Build")
                        .setPrependLogic(() -> {
                            Tile campTile = new Tile(1374, 9710, 0);
                            Logger.info(Region.toInstance(campTile));
                            if (Region.toInstance(campTile).isEmpty() || Region.toInstance(campTile).get(0).distance() > 5) {
                                InstanceWalking.walk(campTile);
                                return true;
                            }

                            return false;
                        })
                        .setSimpleName("Build earth camp"),

                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() == 10 && Inventory.contains("Building supplies") && madeStreamCamp.isNotComplete(),
                        nullArea,
                        () -> GameObjects.closest("Camp spot"))
                        .setInteraction("Build")
                        .setPrependLogic(() -> {
                            Tile campTile = new Tile(1520, 9693, 0);
                            Logger.info(Region.toInstance(campTile));
                            if (Region.toInstance(campTile).isEmpty() || Region.toInstance(campTile).get(0).distance() > 5) {
                                InstanceWalking.walk(campTile);
                                return true;
                            }

                            return false;
                        })
                        .setSimpleName("Build stream camp"),
//                1510, 9693

                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() == 10, nullArea, () -> GameObjects.closest(50857))
                        .setInteraction("Take-from")
                        .setDialogueOptions("Goodbye.")
                        .setPrependLogic(() -> {
                            if (!Client.isDynamicRegion()) {
                                GameObject entrance = GameObjects.closest("Entrance");
                                if (entrance != null && entrance.interact("Pass-through")) {
                                    Sleep.sleepUntil(Client::isDynamicRegion, 4000);
                                }
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Talk to Builder in ney"),


                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() < 15, nullArea, () -> NPCs.closest("Jessamine"))
                        .setDialogueOptions("What does the inscription say?", "What do we do now?")
                        .setSimpleName("Jessamine")

                        .setPrependLogic(() -> {
                            if (Client.isDynamicRegion() && NPCs.closest("Jessamine") == null) {
                                InstanceWalking.walk(new Tile(1441, 9596, 1));
                                return true;
                            }

                            return false;
                        }),

                // leave instance to enchant talismans
                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() == 15, new Tile(1424, 9568, 1), () -> NPCs.closest("Nahta"))
                        .setDialogueOptions("Sent by Attala")
                        .setSimpleName("Nahta"),// todo leave gracefully instead of walkign all the way back
                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() == 16, new Tile(1448, 9584, 1), () -> NPCs.closest("Blacksmith"))
                        .setDialogueOptions("help")
                        .setSimpleName("Blacksmith"),
                // we are no long in instanced ney part of quest and can make moonlight pots
                new LocateEyatlali(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() < 19),

                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() < 23,
                        new Tile(1441, 9642, 1), () -> NPCs.closest("Jessamine"))
                        .setDialogueOptions("dig?")
                        .setSimpleName("Jessamine"),

                new PerilousMoonGetItems(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() == 23
                        && !Inventory.containsAll(ItemID.BREAM_SCALES, ItemID.MOONLIGHT_GRUB_PASTE, ItemID.MOSS_LIZARD_TAIL)),

                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() < 27,
                        new Tile(1441, 9642, 1), () -> NPCs.closest("Eyatlalli"))
                        .setDialogueOptions("dig?")
                        .setSimpleName("Eyat after items"),

                new Fractal(() -> !MoonsOfPerilBranch.isBlueMoonDead() || !MoonsOfPerilBranch.isBloodMoonDead() || !MoonsOfPerilBranch.isEclipseDead())
                        .addChildren(
                                new MoonsOfPerilBranch(() -> true, null)
                        )
                        .setSimpleName("Boss"),

                new TalkToFractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() < 31,
                        new Tile(1441, 9642, 1), () -> NPCs.closest("Zuma"))
                        .setDialogueOptions("dig?")
                        .setSimpleName("Zuma"),

                new TalkToFractal(() -> true,
                        new Tile(1441, 9642, 1), () -> NPCs.closest("Eyatlalli"))
                        .setDialogueOptions("dig?")
                        .setSimpleName("Eyat after items")
        );

    }
}

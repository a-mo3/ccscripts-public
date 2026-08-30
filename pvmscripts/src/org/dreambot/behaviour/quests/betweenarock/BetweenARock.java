package org.dreambot.behaviour.quests.betweenarock;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.WebNodeType;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.behaviour.quests.dwarfcannon.DwarfCannon;
import org.dreambot.behaviour.quests.fishingcontest.FishingContest;
import org.dreambot.behaviour.training.mining.GenericMineLeaf;
import org.dreambot.behaviour.training.mining.MixedMining;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.slayer.SlayerLoadouts;
import org.dreambot.behaviour.training.slayer.behaviour.StandardCombat;
import org.dreambot.behaviour.training.smithing.SmithingBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.BuyFromShopFractal;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.quest.VarbitRequirement;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.webnodes.KeldagrimNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BetweenARock extends Fractal {
    public static VarbitRequirement hasSolvedSchematic = new VarbitRequirement(305, 1);

    public BetweenARock() {
        super(() -> !PaidQuest.BETWEEN_A_ROCK.isFinished());
        KeldagrimNodes.addNodes();
        Area donda = new Area(2827, 10168, 2837, 10164);
        Tile testFerryTile = new Tile(2836, 10149, 0);

        VarbitRequirement hasUsedGoldBar = new VarbitRequirement(301, 1);
        VarbitRequirement shotGoldCannonball = new VarbitRequirement(313, 1);
//        hasCannonball = new Conditions(LogicType.OR, goldCannonball, shotGoldCannonball);

        Area mineCartArea = new Area(
                new Tile(3016, 9823, 0),
                new Tile(3037, 9837, 0),
                new Tile(3025, 9838, 0),
                new Tile(3023, 9846, 0),
                new Tile(3024, 9853, 0),
                new Tile(3016, 9853, 0),
                new Tile(3013, 9831, 0));

        List<GameObject> mineCarts = new ArrayList<>();

        Area clay = new Area(
                new Tile(3054, 9825, 0),
                new Tile(3055, 9816, 0),
                new Tile(3050, 9814, 0),
                new Tile(3048, 9824, 0));

        Area scorpions = new Area(
                new Tile(3043, 9801, 0),
                new Tile(3055, 9781, 0),
                new Tile(3048, 9775, 0),
                new Tile(3041, 9776, 0),
                new Tile(3037, 9781, 0));

        // web nodes for getting into the khorvak place
        WebFinder wf = WebFinder.getWebFinder();

        EntranceWebNode khorvakStairs = new EntranceWebNode(2820, 3484, 0, "Stairs", "Climb-down");
        wf.getNearest(khorvakStairs, 15).addDualConnections(khorvakStairs);
        wf.addWebNode(khorvakStairs);
        EntranceWebNode khorvakStairsExit = new EntranceWebNode(2820, 9883, 0, "Stairs", "Climb-up");

        AbstractWebNode awb = wf.getNearest(khorvakStairsExit);
        Logger.info("Awb " + awb.getType());
        awb.getConnections().stream().filter(w -> w.getType() == WebNodeType.ENTRANCE_NODE).collect(Collectors.toList())
                .forEach(wf::removeNode);

        wf.addWebNode(khorvakStairsExit);
        khorvakStairsExit.addDualConnections(khorvakStairs);
        awb.addDualConnections(khorvakStairsExit);

        this.paintArraySupplier = () -> {
            return new String[]{
                    "State " + PaidQuest.BETWEEN_A_ROCK.getConfigValue()
            };
        };


        setSimpleName("Between a Rock");
        addChildren(
                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.DEFENCE) < 30).setSimpleName("30 def req"),
                new DwarfCannon().setSimpleName("Dwarf cannon"),
                new FishingContest().setSimpleName("Fishing Contest"),
                new MixedMining(() -> Skills.getRealLevel(Skill.MINING) < 40).setSimpleName("40 mining req"),
                new SmithingBranch(() -> Skills.getRealLevel(Skill.SMITHING) < 50).setSimpleName("50 smitihing req"),

                new TalkToFractal(() -> !PaidQuest.BETWEEN_A_ROCK.isStarted(), new Tile(2822, 10167), () -> NPCs.closest("Dondakan the Dwarf"))
                        .setDialogueOptions("cannon at a", "trying to get through", "Sounds interesting!", "Yes.")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 100, 200)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.VARROCK_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.VARROCK_TELEPORT)))
                                .addItem(ItemID.CAMELOT_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.CAMELOT_TELEPORT)))
                                .addItem(ItemID.GOLD_BAR, 3, 5) // dont need at the start but prevents buying them later
                        )
                        .setSimpleName("Start @ Dondakan"),
                new TalkToFractal(() -> PaidQuest.BETWEEN_A_ROCK.getConfigValue() == 10 && (PlayerSettings.getBitValue(571) < 5 || Client.isInCutscene()),
                        new Tile(2842, 10129),
                        () -> NPCs.closest("Dwarven Boatman"))
                        .setDialogueOptions("Yes", "deal", "idea")
                        .setSimpleName("Get access to keldagrim"),
                new TalkToFractal(() -> PaidQuest.BETWEEN_A_ROCK.getConfigValue() == 10, new Tile(2870, 10199), () -> NPCs.closest("Dwarven Engineer"))
                        .setDialogueOptions("later.", "Yes")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 100, 200)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.VARROCK_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.VARROCK_TELEPORT)))
                                .addItem(ItemID.CAMELOT_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.CAMELOT_TELEPORT)))
                        )
                        .setSimpleName("Talk to Engineer"),
                new TalkToFractal(() -> PaidQuest.BETWEEN_A_ROCK.getConfigValue() == 20, new Tile(3022, 3453).getArea(2), () -> NPCs.closest("Rolad"))
                        .setDialogueOptions("later.")
                        .setSimpleName("Talk to Rolad"),

                new Fractal(() -> PaidQuest.BETWEEN_A_ROCK.getConfigValue() == 30)
                        .addChildren(
                                new TalkToFractal(() -> Inventory.contains(ItemID.PAGES_4573) || Inventory.containsAll(ItemID.BOOK_PAGE_1, ItemID.BOOK_PAGE_2, ItemID.BOOK_PAGE_3),
                                        new Tile(3022, 3453).getArea(2), () -> NPCs.closest("Rolad"))
                                        .setDialogueOptions("later.")
                                        .setSimpleName("Talk to Rolad"),
                                // search mine carts
                                new SearchMinecart(() -> !OwnedItems.contains(ItemID.BOOK_PAGE_2)),
//                                new TalkToFractal(() -> !OwnedItems.contains(ItemID.BOOK_PAGE_2), mineCartArea, () -> {
//                                    // get the first minecart we havent yet searched
//                                    GameObject closestCart = GameObjects.closest(x -> x.getName().equals("Mine cart") && !mineCarts.contains(x));
//                                    if (closestCart == null) mineCarts.clear();
//                                    mineCarts.add(closestCart);
//                                    return closestCart;
//                                }).setInteraction("Search")
//                                        .setSleepTimeout(32400)
//                                        .setSimpleName("Search carts for pages"),
                                // fight a scorpion
                                new StandardCombat(() -> !OwnedItems.contains(ItemID.BOOK_PAGE_1), scorpions, () -> NPCs.closest("Scorpion"), ItemID.SALMON)
                                        .setLootStrategy(x -> x.getId() == ItemID.BOOK_PAGE_1)
                                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT
//                                                new EquipmentLoadout()
//                                                .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)

                                        )// this shouldnt rly matter this quest is done at high lvls
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.COINS_995, 100, 200)
                                                .addItem(ItemVariants.COMBAT_BRACLET)
                                                .addItem(ItemID.VARROCK_TELEPORT, 5)
                                                .setEnabledCondition(() -> !Inventory.contains((ItemID.VARROCK_TELEPORT)))
                                                .addItem(ItemID.CAMELOT_TELEPORT, 5)
                                                .setEnabledCondition(() -> !Inventory.contains((ItemID.CAMELOT_TELEPORT)))
                                                .addItem(ItemID.BOOK_PAGE_2)
                                                .addItem(ItemID.SALMON, 1, 6)
                                                .addItem(ItemID.BRONZE_PICKAXE)
                                        )
                                        .setSimpleName("Kill scorpions for page"),

                                // mine clay
                                new GenericMineLeaf(() -> !OwnedItems.contains(ItemID.BOOK_PAGE_3), "Clay Rocks", clay)
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.COINS_995, 100, 200)
                                                .addItem(ItemVariants.COMBAT_BRACLET)
                                                .addItem(ItemID.VARROCK_TELEPORT, 5)
                                                .setEnabledCondition(() -> !Inventory.contains((ItemID.VARROCK_TELEPORT)))
                                                .addItem(ItemID.CAMELOT_TELEPORT, 5)
                                                .setEnabledCondition(() -> !Inventory.contains((ItemID.CAMELOT_TELEPORT)))
                                                .addItem(ItemID.BOOK_PAGE_2)
                                                .addItem(ItemID.BOOK_PAGE_1)
                                                .addItem(ItemID.SALMON, 1, 6)
                                                .addItem(ItemID.BRONZE_PICKAXE)
                                        )
                                        .setSimpleName("Mine clay")
                        ).setSimpleName("get book"),

                new Fractal(() -> PaidQuest.BETWEEN_A_ROCK.getConfigValue() == 40).setSimpleName("Read book")
                        .setPrependLogic(() -> {
                            Inventory.interact(ItemID.DWARVEN_LORE, "Read");
                            return true;
                        }),
                // buy ammo mould
                new BuyFromShopFractal(() -> !OwnedItems.contains(ItemID.AMMO_MOULD),
                        "Nulodion",
                        new Tile(3011, 3453).getArea(2), ItemID.AMMO_MOULD)
                        .setInventoryLoadout(new InventoryLoadout().addItem(ItemID.COINS_995, 5, 1000))
                        .setSimpleName("Buy ammo mould"),

                // go to dondakan with gold bars

                new TalkToFractal(() -> PaidQuest.BETWEEN_A_ROCK.getConfigValue() == 50, new Tile(2823, 10167), () -> NPCs.closest("Dondakan the Dwarf"))
                        .setDialogueOptions("")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 100, 200)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.VARROCK_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.VARROCK_TELEPORT)))
                                .addItem(ItemID.CAMELOT_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.CAMELOT_TELEPORT)))
                                .addItem(ItemID.AMMO_MOULD)
                                .addItem(ItemID.GOLD_BAR, 3, 5)
                        )
                        .setSimpleName("Show gold bars to dondakan"),

                new UseOnFractal(() -> PaidQuest.BETWEEN_A_ROCK.getConfigValue() == 60 && hasUsedGoldBar.isNotComplete(),
                        () -> Inventory.get(ItemID.GOLD_BAR),
                        () -> NPCs.closest("Dondakan the Dwarf"), true)
                        .setArea(new Tile(2822, 10167))
                        .setDialogueOptions("")
                        .setReturnAfterDialogues(true)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 100, 200)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.VARROCK_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.VARROCK_TELEPORT)))
                                .addItem(ItemID.CAMELOT_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.CAMELOT_TELEPORT)))
                                .addItem(ItemID.AMMO_MOULD)
                                .addItem(ItemID.GOLD_BAR, 3, 5)
                        )
                        .setSimpleName("Show gold bars to dondakan"),
                // make gold balls

                new UseOnFractal(() -> PaidQuest.BETWEEN_A_ROCK.getConfigValue() == 60 && !OwnedItems.contains(ItemID.GOLD_CANNON_BALL) && shotGoldCannonball.isNotComplete(),
                        () -> Inventory.get(ItemID.GOLD_BAR),
                        () -> GameObjects.closest("Furnace"),
                        true)
                        .setArea(new Tile(3108, 3498))
                        .setDialogueOptions("Yes.")
                        .setReturnAfterDialogues(true)
                        .setSleepCondition(() -> Inventory.contains(ItemID.GOLD_CANNON_BALL))
                        .setSleepTimeout(2400)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 100, 200)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.VARROCK_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.VARROCK_TELEPORT)))
                                .addItem(ItemID.CAMELOT_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.CAMELOT_TELEPORT)))
                                .addItem(ItemID.AMMO_MOULD)
                                .addItem(ItemID.GOLD_BAR, 3, 5)
                        )
                        .setSimpleName("Make a cball"),

                // shoot gold balls


                new UseOnFractal(() -> PaidQuest.BETWEEN_A_ROCK.getConfigValue() == 60 && shotGoldCannonball.isNotComplete(),
                        () -> Inventory.get(ItemID.GOLD_CANNON_BALL),
                        () -> NPCs.closest("Dondakan the Dwarf"), true)
                        .setArea(new Tile(2822, 10167))
                        .setDialogueOptions("")
                        .setReturnAfterDialogues(true)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 100, 200)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.VARROCK_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.VARROCK_TELEPORT)))
                                .addItem(ItemID.CAMELOT_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.CAMELOT_TELEPORT)))
                                .addItem(ItemID.AMMO_MOULD)
                                .addItem(ItemID.GOLD_CANNON_BALL)
                                .addItem(ItemID.GOLD_BAR, 3, 5)
                        )
                        .setSimpleName("Use g-cball"),


                new TalkToFractal(() -> PaidQuest.BETWEEN_A_ROCK.getConfigValue() <= 70, new Tile(2822, 10167), () -> NPCs.closest("Dondakan the Dwarf"))
                        .setDialogueOptions("fire me into the rock?", "shoot me")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 100, 200)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.VARROCK_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.VARROCK_TELEPORT)))
                                .addItem(ItemID.CAMELOT_TELEPORT, 5)
                                .setEnabledCondition(() -> !Inventory.contains((ItemID.CAMELOT_TELEPORT)))
                                .addItem(ItemID.AMMO_MOULD)
                                .addItem(ItemID.GOLD_BAR, 3, 5)
                        )
                        .setSimpleName("ponda with donda"),

                // get all 3 schematics
                // engineer

                new TalkToFractal(() -> PaidQuest.BETWEEN_A_ROCK.getConfigValue() == 80 && !OwnedItems.contains(ItemID.SCHEMATICS),
                        new Tile(2870, 10199), () -> NPCs.closest("Dwarven Engineer"))
                        .setDialogueOptions("")
                        .setSimpleName("get engineer schematic"),

                new TalkToFractal(() -> PaidQuest.BETWEEN_A_ROCK.getConfigValue() == 80 && !OwnedItems.containsAny(ItemID.SCHEMATICS_4577, ItemID.SCHEMATIC_4578),
                        new Tile(2867, 9876),
                        () -> NPCs.closest("Khorvak, a dwarven engineer"))
                        .setDialogueOptions("No,")
                        .setSimpleName("get khorvak schematic"),

                new SchematicPuzzle(() -> true)

                // solve schematic
                // fuck off because we have the dairy req
        );

        paintArraySupplier = () -> new String[]{
                "Between a rock: " + PaidQuest.BETWEEN_A_ROCK.getConfigValue()
        };
    }
}

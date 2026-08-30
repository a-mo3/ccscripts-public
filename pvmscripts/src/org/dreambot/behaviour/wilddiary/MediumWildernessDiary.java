package org.dreambot.behaviour.wilddiary;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.methods.walking.web.node.impl.TollWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.EarthOrbs;
import org.dreambot.behaviour.method.ents.Ents;
import org.dreambot.behaviour.misc.AdvStandardCombat;
import org.dreambot.behaviour.quests.betweenarock.BetweenARock;
import org.dreambot.behaviour.quests.doricsquest.DoricsQuest;
import org.dreambot.behaviour.quests.dwarfcannon.DwarfCannon;
import org.dreambot.behaviour.quests.fishingcontest.FishingContest;
import org.dreambot.behaviour.quests.fishingcontest.FishingFractal;
import org.dreambot.behaviour.quests.theknightssword.TheKnightsSword;
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.agility.WildernessCourse;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.mining.GenericMineLeaf;
import org.dreambot.behaviour.training.mining.MixedMining;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.smithing.SmithingBranch;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.VarplayerRequirement;

import java.util.Arrays;
import java.util.List;

/**
 * get requirements and do the wilderness medium diary
 */
public class MediumWildernessDiary extends Fractal {
    public static VarplayerRequirement hasntDoneMediumDiary = new VarplayerRequirement(1190, false, 9);
    final Area SHRIMP_AREA = new Area(3240, 3159, 3246, 3141);
    final Area WILDERNESS_MITHRIL_ROCKS = new Area(3084, 3760, 3089, 3752);
    final Area FORGOTTEN_CEM_ANKOUS = new Area(2959, 3757, 2992, 3739);
    final Area BLOODVELD_SAFE_AREA = new Tile(3028, 10126, 0).getArea(8);
    final Area EMBLEM_TRADER = new Tile(3096, 3502, 0).getArea(7);
    final Area MUDDY_CHEST = new Area(3085, 3862, 3091, 3856);
    final Area GREEN_DRAGONS = new Area(2965, 3627, 2989, 3602);

    VarplayerRequirement notMineMith = new VarplayerRequirement(1192, false, 13);
    VarplayerRequirement notEntYew = new VarplayerRequirement(1192, false, 14);
    VarplayerRequirement notWildyGodWars = new VarplayerRequirement(1192, false, 15);
    VarplayerRequirement notWildyAgi = new VarplayerRequirement(1192, false, 16);
    VarplayerRequirement notKillGreenDrag = new VarplayerRequirement(1192, false, 18);
    VarplayerRequirement notKillAnkou = new VarplayerRequirement(1192, false, 19);
    VarplayerRequirement notEarthOrb = new VarplayerRequirement(1192, false, 20);
    VarplayerRequirement notWildyGWBloodveld = new VarplayerRequirement(1192, false, 21);
    VarplayerRequirement notEmblemTrader = new VarplayerRequirement(1192, false, 22);
    VarplayerRequirement notGoldHelm = new VarplayerRequirement(1192, false, 23);
    VarplayerRequirement notMuddyChest = new VarplayerRequirement(1192, false, 24);

    final List<Area> IRON_MINES = Arrays.asList(
            new Area(3399, 3172, 3405, 3167),  // new spot in al kharid
            new Area(2980, 3241, 2987, 3232), // rimmington west side
            new Area(2966, 3244, 2972, 3235) // rimmington east side
    );

    final List<Area> COPPER_MINES = Arrays.asList(
            new Area(3226, 3149, 3232, 3143), // lumbridge swamp
            new Area(3281, 3370, 3284, 3367), // north corner west varrock
            new Area(3285, 3365, 3290, 3360), // south corner west varrock
            new Area(2974, 3250, 2981, 3244) // rimmington copper
    );

    public MediumWildernessDiary() {
        super(hasntDoneMediumDiary::check);

        // add nodes for wilderness god wars dungeon
        WebFinder wf = WebFinder.getWebFinder();
        EntranceWebNode wildernessGWDEntrance = new EntranceWebNode(3016, 3738, 0, "Cave", "Enter");
        EntranceWebNode wildernessGWDExit = new EntranceWebNode(3065, 10160, 3, "Crevice", "Use");
        BasicWebNode gwdExitBasicNode = new BasicWebNode(3065, 10155, 3);

        wildernessGWDEntrance.addDualConnections(wildernessGWDExit);
        wf.addWebNode(gwdExitBasicNode);
        wildernessGWDEntrance.addDualConnections(gwdExitBasicNode);

        Logger.info("Adding wilderness gwd webnodes");
        wf.getNearest(wildernessGWDEntrance, 25)
                .addDualConnections(wildernessGWDEntrance);

        // 60 str req for boulder easier than 60 agil

        Logger.info(NPCs.isIgnoreHealth());
//        Logger.info(NPCs.closest(x -> x.getName().equals("Boulder")));
        Logger.info(NPCs.closest(x -> x.getName().equals("<col=00ffff>Boulder</col>")));
//        Logger.info(NPCs.closest(x -> x.hasAction("Move")));
        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("<col=00ffff>Boulder</col>", "Move"));
//                new Tile(3055, 10165, 3),
//                new Tile(3052, 10165, 3),
//                new Tile(3054, 10166, 3)


        // nodes from cave entrance into the actual gwd
        wf.createAndAddNode(new Tile(3057, 10156, 3));
        wf.createAndAddNode(new Tile(3057, 10164, 3));
        wf.createAndAddNode(new Tile(3057, 10164, 3));
        wf.createAndAddNode(new Tile(3051, 10165, 3));

        // north crevice to avansies,
        EntranceWebNode strGWDEntrance = new EntranceWebNode(3049, 10165, 3, "Crevice", "Use"); // exits next to the avansies, dangerous
        EntranceWebNode strGWDExit = new EntranceWebNode(3035, 10158, 0, "Crevice", "Use"); // exits next to the avansies, dangerous
        BasicWebNode strBasicWebNode = new BasicWebNode(3033, 10158, 0);

        strGWDEntrance.addDualConnections(strGWDExit);
        wf.addWebNode(strBasicWebNode);
        strGWDExit.addDualConnections(strBasicWebNode);


        Logger.info("Adding wilderness gwd webnodes");
        wf.getNearest(strGWDEntrance, 25)
                .addDualConnections(strGWDEntrance);

        // nodes for wilderness resource area
        TollWebNode areaGate = new TollWebNode(3184, 3944, 0);
        areaGate.setEntityName("Gate");
        areaGate.setAction("Open");
        areaGate.setToll(7500);
        wf.addWebNode(areaGate);
        wf.getNearest(areaGate).addDualConnections(areaGate);
        wf.createAndAddNode(new Tile(3184, 3942, 0));

        Area wildernessResourceArea = new Area(3187, 3941, 3191, 3937);

        // node for the wilderness lever, may need a dialogue handle

        int userNameSeed = ShuffleFractal.getLoginValue();
        Area ironMine = IRON_MINES.get(userNameSeed % IRON_MINES.size());
        Area copperMine = COPPER_MINES.get(userNameSeed % COPPER_MINES.size());

        setSimpleName("Wilderness Diary");
        addChildren(
//                new MixedMining(() -> Skills.getRealLevel(Skill.MINING) < 55), // req for mining mithril task

                new DoricsQuest().setSimpleName("Dorics quesst"),
                new GenericMineLeaf(() -> Skills.getRealLevel(Skill.MINING) < 15, "Copper rocks", copperMine)
                        .setHopCondition(() -> copperMine.contains(Players.getLocal()) && Players.all(x -> x.distance() < 4).size() > 3)
                        .setWorldSupplier(() -> Worlds.getRandomWorld(x -> x.isNormal() && x.getWorld() != 401 && (Client.isMembers() != x.isF2P()) && x.getMinimumLevel() < Skills.getTotalLevel()))
                        .setSimpleName("Copper mining"),

                new GenericMineLeaf(() -> Skills.getRealLevel(Skill.MINING) < 55, "Iron rocks", ironMine)
                        .setShouldBank(false)
                        .setWorldSupplier(() -> Worlds.getRandomWorld(x -> x.isNormal() && x.getWorld() != 401 && (Client.isMembers() != x.isF2P()) && x.getMinimumLevel() < Skills.getTotalLevel()))
                        .setHopCondition(() -> Players.all(x -> x.distance() < 4).size() > 3
                                && ironMine.contains(Players.getLocal()))
                        .setSimpleName("Iron mining"),
                new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 61), // req for chopping yew from an ent
                new AgilityBranch(() -> notWildyAgi.check() && Skills.getRealLevel(Skill.AGILITY) < 52), // wilderness agility course req

                // magic req for charge earth orb
                new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 60),
//                new Fractal(() -> Skills.getRealLevel(Skill.MAGIC) < 60)
//                        .addChildren(
//                                new ImpCatcher().setSimpleName("Impcatcher")
//                                        .setPrependLogic(() -> {
//                                            if (Client.isDynamicRegion()) {
//                                                Magic.castSpell(Normal.HOME_TELEPORT);
//                                                Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
//                                            }
//                                            return false;
//                                        }),
//                                new EnchantRecoils().setSimpleName("Enchant Recoils "),
//                                new EnchantDueling()
//                                        .setAcceptCondition(() -> true)
//                                        .setSimpleName("Enchant Duelings ")
//                        )
//                        .setSimpleName("Magic to 60"),

                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS)
                        .setSimpleName("Burn logs need it for slayer"),

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < 60)
                        .setSimpleName("Combat to 60s"),
                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43)
                        .setSimpleName("Prayer Training"),
                new GetOff330(x -> x.isNormal() && x.getWorld() != 401 && x.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel()),

                new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) < 10,
                        SHRIMP_AREA, () -> NPCs.closest(n -> n.hasAction("Net") && SHRIMP_AREA.contains(n)))
                        .setShouldBank(false)
                        .setInteraction("Net")
                        .setSimpleName("Shrimp until lvl 10")
                        .setInventoryLoadout(
                                new InventoryLoadout()
                                        .strictIgnore(ItemID.RAW_SHRIMPS, ItemID.RAW_ANCHOVIES)
                                        .addItem(FishingFractal.SMALL_FISHING_NET, 1)
                                        .setStrict(true)
                        ),
                new FishingContest().setSimpleName("Fishing contest"),
                new DwarfCannon().setSimpleName("Dwarf cannon"),

                // 32 qp for killing a green dragon
//                new MixedCombat(() -> Combat.getCombatLevel() < 25
//                        || Skills.getRealLevel(Skill.HITPOINTS) < 40)
//                        .setSimpleName("Melee training"),
                // todo maybe remove these and just tank the dragonfire
//                new Fractal(() -> Quests.getQuestPoints() < 32).setSimpleName("Questin")
//                        .addChildren(
//                                new XMarksTheSpot().setSimpleName("X marks the spot"),
//                                new ClientOfKourend().setSimpleName("Client of kourend"),
//                                new CooksAssistant().setSimpleName("Cooks assistant"), // 1
//                                new RomeoAndJulietBranch().setSimpleName("Romeo and juliet"), // 5
//                                new ImpCatcher().setSimpleName("Imp catcher"), // 1
//                                new DoricsQuest().setSimpleName("Dorics quest"), // 1
//                                new TheKnightsSword().setSimpleName("Knights sword"), // 1
//                                new RuneMysteries().setSimpleName("Rune mysteries"), // 1
//                                new DwarfCannon().setSimpleName("Dwarf cannon"), // 1
//                                new EnterTheAbyss().setSimpleName("Enter the abyss"),// 0
//                                new GoblinDiplomacy().setSimpleName("Goblin diplomacy"), // 5
//                                new DruidicRitual().setSimpleName("Druidic Ritual"), // 4
//                                new ErnestTheChicken().setSimpleName("Ernest the chicken"),// 4
//                                new VampyreSlayer().setSimpleName("Vampyre Slayer"), // 3
//                                new SheepShearer().setSimpleName("Sheep shearer"), // 1
//                                new MonksFriend().setSimpleName("Monks Friend"), // 1
//                                new RestlessGhost().setSimpleName("Restless Ghost"), // 1
//                                new PriestInPeril().setSimpleName("PIP") // 1
//                        ),
                new MixedMining(() -> Skills.getRealLevel(Skill.MINING) < 10).setSimpleName("10 mining"),
                new TheKnightsSword().setSimpleName("Knights sword"),
                new SmithingBranch(() -> Skills.getRealLevel(Skill.SMITHING) < 50),
                new GenericMineLeaf(() -> notMineMith.check(), "Mithril rocks", WILDERNESS_MITHRIL_ROCKS)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.MITHRIL_PICKAXE)
                                .addItem(ItemID.KNIFE)
                                .setStrict(true))
                        .setSimpleName("Mine a mithril rock"),

                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 50)
                        .setPrependLogic(() -> {
                            if (!Combat.isAutoRetaliateOn()) {
                                if (Widgets.isOpen()) Widgets.closeAll();
                                Combat.toggleAutoRetaliate(true);
                            }

                            return false;
                        })
                        .setSimpleName("Slayer Until 50"),


                new AdvStandardCombat(FORGOTTEN_CEM_ANKOUS, () -> NPCs.closest("Ankou"), ItemID.SHARK)
                        .setAcceptCondition(() -> notKillAnkou.check())
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.SHARK, 8)
                                .setEnabledCondition(() -> !Combat.isInWild())
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR) // todo check for the best weapon you can use
                        ).setSimpleName("Kill an ankou (wilderness)"),

                new AdvStandardCombat(BLOODVELD_SAFE_AREA, () -> NPCs.closest("Bloodveld"), ItemID.SHARK)
                        .setAcceptCondition(() -> notWildyGWBloodveld.check())
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.SHARK, 8)
                                .setEnabledCondition(() -> !Combat.isInWild() || !Inventory.contains(ItemID.SHARK))
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                                .addItem(ItemVariants.BURNING_AMULET)
                                .setEnabledCondition(() -> !Combat.isInWild())
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR) // todo check for the best weapon you can use
                                .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                                .addItem(EquipmentSlot.AMULET, ItemID.UNHOLY_SYMBOL)
                                .addItem(EquipmentSlot.ARROWS, ItemID.WAR_BLESSING)
                                .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_MITRE) // todo make this random between a few items to not spike GE
                        ).setSimpleName("Kill a bloodveld (wilderness GWD)"),

                new TalkToFractal(() -> notEmblemTrader.check(), EMBLEM_TRADER, () -> NPCs.closest("Emblem Trader"))
                        .setDoReachCheck(false)
                        .setDialogueOptions("rewards")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemVariants.RING_OF_DUELING)
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                        ).setSimpleName("Talk to Emblem Trader"),

                new AdvStandardCombat(GREEN_DRAGONS, () -> NPCs.closest("Green dragon"), ItemID.SHARK)
                        .setAcceptCondition(() -> notKillGreenDrag.check())
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.SHARK, 1, 16)
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                                .addItem(ItemVariants.BURNING_AMULET)
                                .setStrict(true)
                                .setEnabledCondition(() -> !Combat.isInWild())
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                        .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR) // todo check for the best weapon you can use
                                        .addItem(EquipmentSlot.CHEST, ItemID.RUNE_CHAINBODY)
                                        .addItem(EquipmentSlot.LEGS, ItemID.RUNE_PLATESKIRT)
//                                .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)
                        ).setSimpleName("Kill a green dragon"),

                new EarthOrbs(() -> notEarthOrb.check()).setSimpleName("Charge an earth orb"),

                new UseOnFractal(() -> notMuddyChest.check(), () -> Inventory.get(ItemID.MUDDY_KEY), () -> GameObjects.closest("Closed chest"), true)
                        .setArea(MUDDY_CHEST)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.MUDDY_KEY)
                                .addItem(ItemVariants.BURNING_AMULET)
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                                .addItem(ItemID.KNIFE)
                                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
                        ).setSimpleName("Open a muddy chest"),

                new WildernessCourse(() -> notWildyAgi.check()).setSimpleName("Wilderness agility")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.KNIFE, 1)
                                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))),

                new Ents(() -> notEntYew.check()).setSimpleName("Chop a yew"),
                // only do until we have the schematic solved so we can make the helmet in jaunt for the task
                new BetweenARock().setAcceptCondition(() -> BetweenARock.hasSolvedSchematic.isNotComplete()),
                new TalkToFractal(() -> notGoldHelm.check(), wildernessResourceArea, () -> GameObjects.closest("Anvil"))
                        .setInteraction("Smith")
                        .setDialogueOptions("helmet", "Yes", "Okay")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.GOLD_HELMET)
                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.GOLD_HELMET))
                                .addItem(ItemVariants.BURNING_AMULET)
                                .addItem(ItemID.GOLD_BAR, 3)
                                .addItem(ItemID.HAMMER)
                                .addItem(ItemID.KNIFE)
                                .addItem(ItemID.COINS_995, 7500)
                                .setEnabledCondition(() -> !Combat.isInWild())
                        )
                        .setPrependLogic(() -> {
                            if (Inventory.contains(ItemID.GOLD_HELMET)) {
                                log("Drop gold helmet");
                                if (Widgets.isOpen()) Widgets.closeAll();
                                Inventory.drop(ItemID.GOLD_HELMET);
                                return true;
                            }

                            if (Widgets.isOpen() && !Bank.isOpen() && !GrandExchange.isOpen()) {
                                log("Close widgets");
                                Widgets.closeAll();
                                return true;
                            }
                            return false;
                        })
                        .setEquipmentLoadout(new EquipmentLoadout().setStrict(true))
                        .setSimpleName("Make gold helmet @ resource area"),

                new TalkToFractal(() -> true, new Tile(3117, 3514).getArea(5), () -> NPCs.closest("Lesser Fanatic"))
                        .setSimpleName("Claim reward")
        );

    }
}

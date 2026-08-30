package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.methods.walking.web.node.impl.teleports.MagicTeleport;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.GameStateListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.method.antipk.AntiPkBranch;
import org.dreambot.behaviour.method.antipk.AntiPkLeaveBosses;
import org.dreambot.behaviour.method.pirates.*;
import org.dreambot.behaviour.method.revs.behaviour.ExitRevs;
import org.dreambot.behaviour.method.spindel.ExitWithLoot;
import org.dreambot.behaviour.method.spindel.RechargeWildyWeapon;
import org.dreambot.behaviour.misc.RefillRosewoodBlowpipe;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.SandCrabs;
import org.dreambot.behaviour.misc.tickcombat.GenericCombatBranch;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.animalmagnetism.util.LeaveAvaRoom;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.combat.F2PMeleeCombats;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.hunter.CustomLoginHandler;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.magic.F2PMagicBranch;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.range.DistributedRangeTraining;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.behaviour.wilddiary.EasyWildernessDiary;
import org.dreambot.behaviour.wilddiary.MediumWildernessDiary;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.discordwebhook.scouter.ScoutFractal;
import org.dreambot.fractals.*;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.EmptyLootingBagEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.*;
import org.dreambot.scriptdata.ZombiePirateSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ZombiePiratesScript extends PseudoScript implements ItemContainerListener, ChatListener, GameStateListener {
    FractalRoot<ZombiePirateSettings> tree = new FractalRoot<>(new ZombiePirateSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    int deathCount = 0;

    // for getting out of wild when your need to recharge whatever wildy weapon
    Supplier<Boolean> rechargedExitDanger = () -> {
        if (Combat.isInWild()) {
            AntiPkLeaveBosses.leaveBosses();
            return true;
        }
        return !SpecialWalker.leaveAvasRoom();
    };
    // set whenever you get the no charges left message
    // todo find inital charge state and manage it well
    boolean needsToRecharge = false;
    boolean hasLootInBag = true;
    boolean usedQs = false;

    @Override
    public void onArgs(String... args) {
        Logger.info("QS argument detected changing setting file name to " + args[0]);
        usedQs = true;
    }

    @Override
    public void init() {
//        new AIAntiban();
        Client.getInstance().addEventListener(this);

        Client.getInstance().getRandomManager().disableSolver(RandomEvent.LOGIN);
        Client.getInstance().getRandomManager().registerSolver(new CustomLoginHandler("CUSTOM_LOGIN"));
        Client.getInstance().getRandomManager().enableSolver("CUSTOM_LOGIN");


        // todo add webnodes for the lms casual floor sploit
        EntranceWebNode bottomLMSStairs = new EntranceWebNode(3138, 3636, 0, "Stairs", "Climb");
        EntranceWebNode topLMSStairs = new EntranceWebNode(3138, 3637, 1, "Stairs", "Climb");
        BasicWebNode casLMSBasic = new BasicWebNode(3141, 3637, 1);

        WebFinder wf = WebFinder.getWebFinder();
        wf.getNearest(bottomLMSStairs, 10).addDualConnections(bottomLMSStairs);
        bottomLMSStairs.addDualConnections(topLMSStairs);
        topLMSStairs.addDualConnections(casLMSBasic);
        wf.addWebNodes(bottomLMSStairs, topLMSStairs, casLMSBasic);


        int[] pirateDropTable = new int[]{
                ItemID.BLOOD_RUNE,
                ItemID.DEATH_RUNE,
                ItemID.CHAOS_RUNE,
//                ItemID.MIND_RUNE,

                ItemID.BATTLESTAFF,
                ItemID.ADAMANT_PLATEBODY,
                ItemID.RUNE_MED_HELM,
                ItemID.RUNE_WARHAMMER,
                ItemID.RUNE_BATTLEAXE,
                ItemID.RUNE_LONGSWORD,
                ItemID.RUNE_SWORD,
                ItemID.RUNE_MACE,
                ItemID.DRAGON_DAGGER,
                ItemID.DRAGON_LONGSWORD,
                ItemID.DRAGON_SCIMITAR,

                ItemID.BLIGHTED_ANCIENT_ICE_SACK,
                ItemID.BLIGHTED_ANGLERFISH,
                ItemID.BLIGHTED_MANTA_RAY,
                ItemID.BLIGHTED_KARAMBWAN,
                ItemID.BLIGHTED_SUPER_RESTORE4,

                ItemID.ZOMBIE_PIRATE_KEY,
                ItemID.ADAMANT_SEEDS,
                ItemID.CANNONBALL,
                ItemID.GOLD_ORE,
                // druid drops
                ItemID.ELDER_CHAOS_TOP,
                ItemID.ELDER_CHAOS_ROBE,
                ItemID.ELDER_CHAOS_HOOD,

                ItemID.GRIMY_GUAM_LEAF,
                ItemID.GRIMY_HARRALANDER,
                ItemID.GRIMY_RANARR_WEED,
                ItemID.GRIMY_IRIT_LEAF,
                ItemID.GRIMY_AVANTOE,
                ItemID.GRIMY_KWUARM,
                ItemID.GRIMY_CADANTINE,
                ItemID.GRIMY_LANTADYME,
                ItemID.GRIMY_DWARF_WEED,
                ItemID.AMETHYST_ARROW,
        };

        WithdrawLoadoutEvent.sellList = pirateDropTable;
//        MuleOff.LOOT = new int[]{};
        MuleOff.LOOT = pirateDropTable;

        Logger.info("Init");
        tree.setSimpleName("cCZombiePirates");


        tree.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new ScoutFractal(),
                new ReactionSettingsFractal(),
                new PutPetAway(),
                new TutorialTree().setSimpleName("Tutorial island"),
                new LampHandler().setSimpleName("Lamp handler"),
                new EmptyDeathsCoffer().setSimpleName("Empty coffer"),

                new F2PMeleeCombats(() -> shouldTrainMelee() && tree.getSettings().ftpMeleeTraining,
                        tree.getSettings().attackTarget,
                        tree.getSettings().strengthTarget,
                        tree.getSettings().defenceTarget,
                        () -> {
                            int atk = Skills.getRealLevel(Skill.ATTACK);
                            int str = Skills.getRealLevel(Skill.STRENGTH);
                            int def = Skills.getRealLevel(Skill.DEFENCE);
                            if (Skills.getRealLevel(Skill.ATTACK) >= tree.getSettings().attackTarget)
                                atk = 100;
                            if (Skills.getRealLevel(Skill.STRENGTH) >= tree.getSettings().strengthTarget)
                                str = 100;
                            if (Skills.getRealLevel(Skill.DEFENCE) >= tree.getSettings().defenceTarget)
                                def = 100;
                            if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                            if (atk <= def) return CombatStyle.ATTACK;
                            return CombatStyle.DEFENCE;
                        }
                ).setSimpleName("F2P Melee Combats"),

                new F2PMagicBranch(() -> tree.getSettings().ftpMagicTraining
                        && Skills.getRealLevel(Skill.MAGIC) < tree.getSettings().magicTarget,
                        tree.getSettings().defenceTarget
                ).setSimpleName("F2P Magic Training"),


                new DistributedRangeTraining(() -> tree.getSettings().ftpRangeTraining
                        && Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget)
                        .setSimpleName("F2P range"),

                new Fractal(() -> tree.getSettings().stopAfterFTP)
                        .setSimpleName("Stop after f2p")
                        .setPrependLogic(() -> {
                            Logger.info("Stop after F2P setting enabled");
                            ScriptManager.getScriptManager().stop();
                            return false;
                        }),

                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),

                new TimedShuffleFractal(Calculations.random(40, 125))
                        .addChildren(
                                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < tree.getSettings().prayerTarget)
                                        .setSimpleName("Prayer training"),

                                new ConfigurableMeleeTraining(this::shouldTrainMelee)
                                        .setStyleSupplier(() -> {
                                            int atk = Skills.getRealLevel(Skill.ATTACK);
                                            int str = Skills.getRealLevel(Skill.STRENGTH);
                                            int def = Skills.getRealLevel(Skill.DEFENCE);
                                            if (Skills.getRealLevel(Skill.ATTACK) >= tree.getSettings().attackTarget)
                                                atk = 100;
                                            if (Skills.getRealLevel(Skill.STRENGTH) >= tree.getSettings().strengthTarget)
                                                str = 100;
                                            if (Skills.getRealLevel(Skill.DEFENCE) >= tree.getSettings().defenceTarget)
                                                def = 100;
                                            if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                                            if (atk <= def) return CombatStyle.ATTACK;
                                            return CombatStyle.DEFENCE;
                                        })
                                        .setPrependLogic(() -> {
                                            if (Client.isDynamicRegion()) {
                                                Magic.castSpell(Normal.HOME_TELEPORT);
                                                Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                                            }
                                            return false;
                                        })
                                        .setSimpleName("Melee training"),

                                // at 50 we to go get avas
                                SandCrabs.getRange(() -> Skills.getRealLevel(Skill.RANGED) < Math.min(50, tree.getSettings().rangeTarget))
                                        .setDefenceTarget(tree.getSettings().defenceTarget)
                                        .setSimpleName("Range training")
                                        .setPrependLogic(() -> {
                                            if (Client.isDynamicRegion()) {
                                                Magic.castSpell(Normal.HOME_TELEPORT);
                                                Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                                            }
                                            return false;
                                        })

                        ),


                new Fractal(() -> tree.getSettings().doDiaries
                        && MediumWildernessDiary.hasntDoneMediumDiary.check()).setSimpleName("Diaries ").addChildren(
                        new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Burn logs need it for slayer"),
                        new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                        new EasyWildernessDiary()
                                .setPrependLogic(() -> !SpecialWalker.leaveAvasRoom())
                                .setSimpleName("Easy diary"),
                        new RestlessGhost().setSimpleName("Restless ghost"),
                        new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                        new PriestInPeril().setSimpleName("Priest in peril"),
                        new MediumWildernessDiary().setSimpleName("Medium diary")
                ),

                new Fractal(() -> tree.getSettings().useAvas && !PaidQuest.ANIMAL_MAGNETISM.isFinished())
                        .setSimpleName("Animal magnetism")
                        .addChildren(
                                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Burn logs need it for slayer"),
                                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                                new RestlessGhost().setSimpleName("Restless ghost"),
                                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                                new PriestInPeril().setSimpleName("Priest in peril"),
                                new CraftingBranch(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                                new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                                new AnimalMagnetismBranch().setSimpleName("Animal Magnetism")
                        ),

                new Fractal(() -> !Combat.isInWild() && Bank.isCached()
                        && !OwnedItems.containsAny(ItemID.AVAS_ASSEMBLER, ItemID.AVAS_ACCUMULATOR, ItemID.AVAS_ATTRACTOR)
                        && tree.getSettings().useAvas)
                        .setSimpleName("Get Avas").addChildren(
                                new GetMoreAvas().setSimpleName("More avas")
                        ),

                new LeaveAvaRoom().setSimpleName("Leave avas"),

                // this is the range training after getting avas
                SandCrabs.getRange(() -> Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget)
                        .setDefenceTarget(tree.getSettings().defenceTarget)
                        .setSimpleName("Range training")
                        .setPrependLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Magic.castSpell(Normal.HOME_TELEPORT);
                                Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                            }
                            return false;
                        }),

                new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < tree.getSettings().magicTarget)
                        .setSimpleName("Magic training"),

                new EmptyDeathsCoffer().setSimpleName("Empty death coffer"),
                new MuleOff()
                        .setSimpleName("Mule Off"),

                // todo might need to replace with something for just pirates
                new AntiPkBranch().setSimpleName("Anti PK"),

                new RechargeWildyWeapon(ItemID.URSINE_CHAINMACE_U, ItemID.URSINE_CHAINMACE, () -> false,
                        350).setSimpleName("Recharge Ursine")
                        .setAcceptCondition(() -> tree.getSettings().pirateEquipmentLoadout.name().contains("URSINE")
                                && (OwnedItems.contains(ItemID.URSINE_CHAINMACE_U) || needsToRecharge)),

                new RechargeWildyWeapon(ItemID.VIGGORAS_CHAINMACE_U, ItemID.VIGGORAS_CHAINMACE, () -> false,
                        350).setSimpleName("Recharge Viggora")
                        .setAcceptCondition(() -> tree.getSettings().pirateEquipmentLoadout.name().contains("VIGGORA")
                                && (OwnedItems.contains(ItemID.VIGGORAS_CHAINMACE_U) || needsToRecharge)),

                new UseAnchorSpell().setSimpleName("Anchor spell"),
                new LeavePirates(() ->
                        Combat.isInWild()
                                && (ExitWithLoot.inventoryValue() > tree.getSettings().exitLootValue
                                || Equipment.isSlotEmpty(EquipmentSlot.WEAPON)
                                || (tree.getSettings().pirateEquipmentLoadout == PirateEquipmentLoadout.MSB_ADDY && Equipment.isSlotEmpty(EquipmentSlot.ARROWS))
                                || !Inventory.contains(ItemID.JUG_OF_WINE, ItemID.LOBSTER, ItemID.BLIGHTED_MANTA_RAY))
                ).setSimpleName("Leave pirates (loot)"),
                new RefillRosewoodBlowpipe().setSimpleName("Rosewood refill"),
                new RechargeAtFerox().setSimpleName("Refresh prayer"),
                new KillPirates(() -> !tree.getSettings().flick, tree.getSettings())
                        .setLoadoutCondition(() -> !Combat.isInWild() || !Inventory.contains(ItemID.JUG_OF_WINE, ItemID.LOBSTER, ItemID.BLIGHTED_MANTA_RAY))
                        .setEquipmentLoadout(tree.getSettings().pirateEquipmentLoadout.loadout)
                        .setInventoryLoadout(tree.getSettings().pirateInventoryLoadout.loadout.setStrict(true))
                        .setSimpleName("Pirates"),

                // generic combat for tick flicking
                GenericCombatBranch.builder()
                        .dropSupplier(() -> Inventory.get(ItemID.JUG_OF_WINE, ItemID.JUG, ItemID.LOBSTER, ItemID.BLIGHTED_MANTA_RAY))
                        .area(KillPirates.CHAOS_TEMPLE)
                        .mobFilter(x -> KillPirates.CHAOS_TEMPLE.contains(x)
                                && x.getName().equals("Zombie pirate")
                                && x.getHealthPercent() > 0
                                && (!x.isInCombat() || x.isInteracting(Players.getLocal())))

                        .prayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MAGIC, tree.getSettings().pirateEquipmentLoadout.mode == Skill.RANGED ? PVMUtil.getBestRangePray() : PVMUtil.getBestMeleePray()})
                        .flickPrayers(true)

                        .addPotion(ItemVariants.RANGE_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3)
                        .addPotion(ItemVariants.PRAYER_POTION, () -> Skill.PRAYER.getBoostedLevel() < 3)
                        .addPotion(ItemVariants.STRENGTH_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 5)

                        .lootFilter(
                                x -> KillPirates.CHAOS_TEMPLE.contains(x)
                                        && (x.getItem().getLivePrice() * x.getAmount() > Math.max(PVMUtil.getCheapest().getLivePrice(), 1000) || x.getItem().isNoted())
                        )

                        .build()
                        .init()
                        .setLoadoutCondition(() -> !Combat.isInWild() || !Inventory.contains(ItemID.JUG_OF_WINE, ItemID.LOBSTER, ItemID.BLIGHTED_MANTA_RAY))
                        .setEquipmentLoadout(tree.getSettings().pirateEquipmentLoadout.loadout)
                        .setInventoryLoadout(tree.getSettings().pirateInventoryLoadout.loadout.setStrict(true))
                        .setSimpleName("Pirate (flick)")
                        .setAcceptCondition(() -> true)

        );
//        new AIAntiban();

        Area mageBankEntrance = new Area(3086, 3961, 3099, 3955);
        List<AbstractWebNode> dragonNodes = wf.getAll().stream().filter(x -> mageBankEntrance.contains(x.getTile())).collect(Collectors.toList());
        dragonNodes.forEach(wf::removeNode);
        // remove nodes that make it path through green dragons
        Area feroxThroughDragons = new Area(3116, 3718, 3148, 3644);
        List<AbstractWebNode> badNodes = wf.getAll().stream().filter(x -> feroxThroughDragons.contains(x.getTile())).collect(Collectors.toList());
        badNodes.forEach(wf::removeNode);

        // ernest the chicken webnode
        // WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));

        // AbstractResponseEvent.addGlobalExitCondition(new EventExitCondition(() -> !lastWorldHop.finished(), "RECENT_WORLD_HOP"));
    }

    // training like naguas & slayer requires prayer when not in wild, only disable in this region
    Area ACCEPTED_NO_PRAYER_AREA = new Area(2949, 3921, 3357, 3130);

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (!Combat.isInWild() && ACCEPTED_NO_PRAYER_AREA.contains(Players.getLocal()))
            PrayerUtils.disable(Prayer.PROTECT_FROM_MELEE, Prayer.PROTECT_FROM_MISSILES, Prayer.PROTECT_FROM_MAGIC);
//        Item bag = ItemVariants.LOOTING_BAG.getItem();
//        if (bag != null) {
//            Logger.info("Bag " + Arrays.toString(bag.getActions()));
//            Logger.info("Bag " + bag.hasAction("View"));
//
//            WidgetChild view = Widgets.get(x -> x.getParentID() == 15 && x.hasAction("View"));
//            if (view == null) {
//                Logger.info(" no View action");
//            } else {
//                Logger.info("View action");
//            }
//            return 1000;
//        }
        if (Combat.isInWild()) {
            WebFinder.getWebFinder().disableTeleport(MagicTeleport.LUMBRIDGE_HOME_TELEPORT);
        } else {
            WebFinder.getWebFinder().enableTeleport(MagicTeleport.LUMBRIDGE_HOME_TELEPORT);
        }

        NPC boulderObstacle = (NPCs.closest(x -> x.getName().equals("<col=00ffff>Boulder</col>")));
        if (boulderObstacle != null && boulderObstacle.distance() < 5 && boulderObstacle.getX() < Players.getLocal().getX()) {
            Logger.info("Moving boulder");
            boulderObstacle.interact("Move");
            Antiban.sleepUntil(() -> boulderObstacle.getX() > Players.getLocal().getX(), 4400);
            return ReactionGenerator.getNormal();
        }

        Player threat = Players.closest(x -> x.isInteracting(Players.getLocal())
                && CombatUtil.canAttackMe(x)
        );
        if (Combat.isInWild() && threat != null) {
            Logger.info("Set threat " + threat.getName());
            AntiPkBranch.setAttackerName(threat.getName());
        }

//        if (!Combat.isInWild() && !Client.isDynamicRegion() && !Bank.isCached()) {
//            if (!SpecialWalker.leaveAvasRoom()) return ReactionGenerator.getNormal();
//            if (Bank.isOpen()) Bank.close();
//            if (Walking.shouldWalk()) Bank.open();
//            return ReactionGenerator.getNormal();
//        }

        if (!ChangeAlchWarning.setHighAlchWarning(500_000)) {
            Logger.info("Setting alch warning price");
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.LOOTING_BAG_CLOSED)) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Opening looting bag");
            Inventory.interact(ItemID.LOOTING_BAG_CLOSED, "Open");
            Antiban.sleepUntil(() -> !Inventory.contains(ItemID.LOOTING_BAG_OPENED), 1800);
            return ReactionGenerator.getNormal();
        }

//        if (ZombiePirateLocations.WHOLE_REV_CAVES.contains(Players.getLocal())) hasLootInBag = true;

        // dont run the tree while hopping worlds because equipment state will make you do loadouts you shouldnt
        if (Client.getGameStateID() == 45) return ReactionGenerator.getQuick();
        if (!Client.isLoggedIn()) return ReactionGenerator.getQuick();
        if (!Combat.isInWild() && ItemVariants.LOOTING_BAG.getItem() != null
                && (hasLootInBag || LootingBag.value() != 0)) {
            if (!Bank.isOpen()) Bank.open();

            if (Bank.isOpen() && ItemVariants.LOOTING_BAG.getItem() != null) {
                Logger.info("Emptying looting bags");
                new EmptyLootingBagEvent().executed();
                hasLootInBag = false;
            }
            return ReactionGenerator.getNormal();
        }

        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

    Timer cacheTime = new Timer(5 * 1000);
    int invValue = -1;

    @Override
    public String[] getPaintInfo() {
        String muleOff = "-";
        if (MuleOff.timer != null) muleOff = formatTime(MuleOff.timer.remaining());
        Player local = Players.getLocal();
        String target = "";
        if (local != null) {
            Character tgt = local.getInteractingCharacter();
            if (tgt != null) target = tgt.getName();
        }

        // todo something here for melee mode
        if (cacheTime.finished()) {
            cacheTime.reset();
            invValue = ExitWithLoot.inventoryValue();
        }

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "target: " + target,
                "Game state " + Client.getGameStateID(),
                String.format("Inv loot value %s (%s) / %s",
                        df.format(invValue),
                        df.format(LootingBag.value()),
                        df.format(tree.getSettings().exitLootValue)
                ),
                "Deaths " + deathCount + "/" + AntiPkBranch.getPkEventCounter(),
                String.format("Teleblocked: %b Seconds left: %d",
                        CombatUtil.get().isTeleblocked(),
                        CombatUtil.get().msLeftOnTB() / 1000
                ),
                String.format("InCombat: %b Seconds left: %d",
                        CombatUtil.get().isInCombat(),
                        CombatUtil.get().msLeftOnLogout() / 1000
                ),
        };
    }

    @Override
    public String getScriptName() {
        return "cCZombiePirates";
    }

    @Override
    public int getMoneyMade() {
        return grossGp;
    }

    @Override
    public Timer getRuntime() {
        return runtime;
    }

    @Override
    public long getMuleOffTime() {
        return MuleOff.timer == null ? 0 : MuleOff.timer.remaining();
    }

    @Override
    public Fractal getFractal() {
        return tree;
    }

    @Override
    public void onScriptPaint(Graphics g) {

    }

    private String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        DecimalFormat format = new DecimalFormat("00");
        return String.format("%s:%s:%s",
                format.format(hours),
                format.format(minutes),
                format.format(seconds));
    }


    public void onInventoryItemAdded(Item item) {
        Logger.info("item added");
        if (!Combat.isInWild()) return;
        if (Bank.isOpen()) return;
        if (ExitWithLoot.ignoredIds.contains(item.getId())) return;
//        if (.SPINDEL_CHASM.contains(Players.getLocal())) return;
        ;
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info("item changed");
        if (Bank.isOpen()) return;
        if (!Combat.isInWild()) return;
        if (ExitWithLoot.ignoredIds.contains(incoming.getId())) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        Logger.info("item swapped");
        if (!Combat.isInWild()) return;
        if (Bank.isOpen()) return;
        if (ExitWithLoot.ignoredIds.contains(incoming.getId())) return;
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message.getMessage().toLowerCase().contains("no ammo left")) {
            ExitRevs.setForceLeave(true);
        }

        if (message.getMessage().toLowerCase().contains("you are dead")) {
            deathCount++;
        }

        if (message.getMessage().toLowerCase().contains("not enough revenant ether")) {
            needsToRecharge = true;
        }

        if (message.getMessage().toLowerCase().contains("has run out of revenant")) {
            needsToRecharge = true;
        }

        if (message.getMessage().toLowerCase().contains("chainmace is out of charges")) {
            needsToRecharge = true;
        }
        if (message.getMessage().toLowerCase().contains("giving it a total of")) {
            needsToRecharge = false;
        }
    }

    static Timer lastWorldHop = new Timer(3000);
    boolean wasHopping = false;

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added " + item.getName() + " * " + item.getAmount());
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onGameStateChange(GameState gameState) {
        if (gameState == GameState.HOPPING) {
            Logger.info("World hop");
            wasHopping = true;
            return;
        }

        if (wasHopping && gameState == GameState.LOGGED_IN) {
            Logger.info("Reset last world hop timer");
            lastWorldHop.reset();
            wasHopping = false;
        }
    }

    private boolean shouldTrainMelee() {
        ZombiePirateSettings settings = tree.getSettings();
        if (Skills.getRealLevel(Skill.ATTACK) < settings.attackTarget) return true;
        if (Skills.getRealLevel(Skill.STRENGTH) < settings.strengthTarget) return true;
        // if we meet atk and str reqs we can go onto range training for defence
        return false;
    }
}

package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.CondHelper;
import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.gwd.*;
import org.dreambot.behaviour.method.gwd.bandos.GetBandosKC;
import org.dreambot.behaviour.method.gwd.bandos.KillBandos;
import org.dreambot.behaviour.method.gwd.bandos.tickbandosfight.TickKillBandosBranch;
import org.dreambot.behaviour.method.gwd.kree.GetKreeKC;
import org.dreambot.behaviour.misc.*;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.animalmagnetism.util.LeaveAvaRoom;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.behaviour.quests.deathplateau.DeathPlateau;
import org.dreambot.behaviour.quests.druidicritual.DruidicRitual;
import org.dreambot.behaviour.quests.eadgarsruse.EadgarsRuse;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.quests.trollstronghold.TrollStronghold;
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.herblore.HerbloreBranch;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.nmz.CollectHerbBoxes;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.nmz.MakeTrollHeimTabs;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.range.ConfigurableRangeTraining;
import org.dreambot.behaviour.training.range.DistributedRangeTraining;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.behaviour.wilddiary.EasyWildernessDiary;
import org.dreambot.behaviour.wilddiary.MediumWildernessDiary;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllEquipmentEvent;
import org.dreambot.fractals.events.EmptyLootingBagEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.fractals.util.RatConfigureQuickPrayers;
import org.dreambot.scriptdata.BandosSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;
import org.dreambot.webnodes.GWDNodes;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class BandosScript extends PseudoScript implements ItemContainerListener, SpawnListener {
    FractalRoot<BandosSettings> tree = new FractalRoot<>(new BandosSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    Area MONEY_ZONE = new Area(1652, 3784, 1697, 3751);

    @Override
    public void onArgs(String... args) {
    }

    InventoryLoadout aviansiesLoadout = new InventoryLoadout()
            .addItem(ItemID.SWORDFISH, 20)
            .setEnabledCondition(() -> !Combat.isInWild() || !Inventory.contains(ItemID.SWORDFISH))
            .setRefill(1_000)
            .addItem(ItemVariants.LOOTING_BAG)
            .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.LOOTING_BAG))
            .addItem(ItemVariants.BURNING_AMULET)
            .setEnabledCondition(() -> !Combat.isInWild())
            .setRefill(45)
            .addItem(ItemVariants.RANGE_POTION, 1, 1)
            .setRefill(45)
            .setEnabledCondition(() -> !Combat.isInWild())
            .setStrictSupplier(() -> !Combat.isInWild());

    EquipmentLoadout aviansiesEquipmentLoadout = new EquipmentLoadout()
            .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 70)
            .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_FULL_HELM)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 70)
            .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)

            .addItem(EquipmentSlot.LEGS, ItemID.LEATHER_CHAPS)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40)
            .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 50, 40) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
            .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
            .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
            .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)

            .addItem(EquipmentSlot.CHEST, ItemID.LEATHER_BODY).setRefill(5)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 50)
            .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 60, 50) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
            .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 70, 60) && Skills.getRealLevel(Skill.DEFENCE) >= 40)
            .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY).setRefill(5)
            .setEnabledCondition(() -> CondHelper.skillBetween(Skill.RANGED, 100, 70) && Skills.getRealLevel(Skill.DEFENCE) >= 40)

            .addItem(EquipmentSlot.SHIELD, ItemID.BLACK_DHIDE_SHIELD)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 70)
            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
            .setRefill(10)
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, new EquipmentLoadoutItem(ItemID.RUNE_DART, 1, 100))
            .setRefill(5_000)
//            .addItem(EquipmentSlot.WEAPON, new EquipmentLoadoutItem(ItemID.RUNE_DART, 1, 100))
//            .setEnabledCondition(() -> AvianSettings.getData().useAdamantDarts)
//            .setRefill(5_000)

            // for zammy item so you arent smacked by mages and such
            .addItem(EquipmentSlot.ARROWS, ItemID.UNHOLY_BLESSING)
            .setStrict(true);

    final Area AVIANS = new Area(
            new Tile(3022, 10143, 0),
            new Tile(3033, 10149, 0),
            new Tile(3040, 10158, 0),
            new Tile(3032, 10166, 0),
            new Tile(3022, 10168, 0),
            new Tile(3008, 10157, 0));

    public static boolean shouldUseBonesToPeaches;

    @Override
    public void init() {
        // ernest the chicken webnode
        // WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));
        GWDNodes.init();
        Client.getInstance().addEventListener(this);

        // set bones to peaches based on if you own it, used in bandosloadouts
        shouldUseBonesToPeaches = Client.getInstance().getScriptManager().hasSDNScript(2029)
                && tree.getSettings().useBonesToPeaches;
        Logger.info("Bones to peaches " + shouldUseBonesToPeaches);

        // blacklist tile where rocks are
        LocalPathFinder lp = LocalPathFinder.getLocalPathFinder();
        lp.addBlacklistedTile(new Tile(2901, 3680, 0));
        lp.addBlacklistedTile(new Tile(2902, 3680, 0));
        lp.addBlacklistedTile(new Tile(2908, 3682, 0));
        lp.addBlacklistedTile(new Tile(2909, 3683, 0));
        lp.addBlacklistedTile(new Tile(2871, 3671, 0));
        lp.addBlacklistedTile(new Tile(2870, 3671, 0));

        MuleOff.LOOT = new int[]{
                ItemID.OBSIDIAN_PLATEBODY,
                ItemID.OBSIDIAN_PLATELEGS,
                ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD,
                ItemID.DRAGON_BOOTS,
                ItemID.OBSIDIAN_CAPE,
                ItemID.EMERALD_RING,
                ItemID.SUPER_RESTORE3,
                ItemID.SUPER_RESTORE2,
                ItemID.SUPER_RESTORE1,
                ItemID.SARADOMIN_BREW1,
                ItemID.SARADOMIN_BREW2,
                ItemID.SARADOMIN_BREW3,
                ItemID.RUNE_PICKAXE,
                ItemID.LOOP_HALF_OF_KEY,
                ItemID.TOOTH_HALF_OF_KEY,
                // training items
                ItemID.RING_OF_DUELING8,
                ItemID.EMERALD_RING,
                ItemID.RUNE_ARROW,
                ItemID.DRAGON_BONES,
                ItemID.OBSIDIAN_CAPE,
                ItemID.LIMPWURT_ROOT,
                ItemID.GUAM_POTION_UNF,
                ItemID.TARROMIN_POTION_UNF,

                ItemID.CRUSHED_NEST,
                ItemID.ARMADYL_FULL_HELM,
                ItemID.ARMADYL_CHESTPLATE,
                ItemID.ARMADYL_CHAINSKIRT,
                ItemID.ARMADYL_HILT,
                ItemID.GODSWORD_SHARD_1,
                ItemID.GODSWORD_SHARD_2,
                ItemID.GODSWORD_SHARD_3,
                ItemID.BLACK_DHIDE_BODY,
                ItemID.RUNE_CROSSBOW,

                ItemID.MAGIC_LOGS,
                ItemID.CHILLI_POTATO,
                ItemID.RUNE_SQ_SHIELD,
                ItemID.ADAMANTITE_ORE,
                ItemID.COAL,
                ItemID.RUNE_LONGSWORD,
                ItemID.RUNE_2H_SWORD,
                ItemID.TOOTH_HALF_OF_KEY,
                ItemID.RUNE_BATTLEAXE,
                ItemID.SNAPDRAGON_SEED,
                ItemID.RUNE_PLATEBODY,
                ItemID.GRIMY_SNAPDRAGON,

                ItemID.BANDOS_BOOTS,
                ItemID.BANDOS_CHESTPLATE,
                ItemID.BANDOS_TASSETS,
                ItemID.BANDOS_HILT,
                ItemID.GODSWORD_SHARD_2,
                ItemID.GODSWORD_SHARD_1,
                ItemID.GODSWORD_SHARD_3,

                ItemID.MIND_RUNE,
                ItemID.RUNITE_BOLTS,
                ItemID.RUNE_ARROW,
                ItemID.RANGING_POTION3,
                ItemID.SUPER_DEFENCE3,
                ItemID.GRIMY_DWARF_WEED,
                ItemID.DWARF_WEED_SEED,
                ItemID.CRYSTAL_KEY,
                ItemID.YEW_SEED,

                ItemID.GRIMY_RANARR_WEED,
                ItemID.GRIMY_IRIT_LEAF,
                ItemID.GRIMY_KWUARM,
                ItemID.GRIMY_LANTADYME,
                ItemID.GRIMY_DWARF_WEED,
                ItemID.GRIMY_AVANTOE,
                ItemID.GRIMY_CADANTINE,
                ItemID.GRIMY_GUAM_LEAF,
                ItemID.GRIMY_HARRALANDER,
                ItemID.BLOOD_RUNE,
                ItemID.CHAOS_RUNE,
                ItemID.RUNE_DAGGER_PP,
                ItemID.SILVER_ORE,
                ItemID.AVIAN_HEAD,
                ItemID.ADAMANTITE_BAR,
                ItemID.BLACK_MASK,
                ItemID.AMULET_OF_GLORY,
                ItemID.RING_OF_WEALTH,
                ItemID.SHARK,
                ItemID.LOBSTER,
                ItemID.RUNE_CHAINBODY,
                ItemID.RUNE_PLATESKIRT,
                ItemID.ANTI_POSION_3,
                ItemID.DRAGON_BONES,
                ItemID.PRAYER_POTION4,
                ItemID.RUNITE_LIMBS,
                ItemID.NATURE_RUNE,
                ItemID.MITHRIL_PLATESKIRT,
                ItemID.MITHRIL_PLATEBODY,
                ItemID.ADAMANT_PLATEBODY,
                ItemID.ADAMANT_PLATELEGS,
                ItemID.ADAMANT_PLATESKIRT,
                ItemID.LAW_RUNE,
                ItemID.UNCUT_SAPPHIRE,
                ItemID.UNCUT_RUBY,
                ItemID.UNCUT_EMERALD,
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        BandosSettings settings = tree.getSettings();
        RingPreference.ringPreference = settings.ringPreference;
        GWDBoltPreference.boltPreference = tree.getSettings().boltPreference;
        int rangeTarget = settings.rangeTarget;
        int defTarget = settings.defenceTarget;
        tree.setSimpleName("cCBandos");

        // we are doing this to maintain 1 instance of the boss fight that maintains the state, while not having to eval the whole tree
        // mostly because theres some race condition causing skils and equipment to be returned incorrectly
        Fractal bossFight;
        if (settings.useNewFight) {
            bossFight = new TickKillBandosBranch(() -> KillBandos.BANDOS_ROOM.contains(Players.getLocal())
//                 4000 y check because KC isnt reset on teleport out
                    || (Players.getLocal().getY() > 4000 && GetBandosKC.getBandosKillcount() >= 40)
//                         < 8000 y is a check for not in the wilderness gwd
                    || (Inventory.contains(ItemID.ECUMENICAL_KEY) && Players.getLocal().getY() > 4000 && Players.getLocal().getY() < 8000), settings);
        } else {
        bossFight = new KillBandos(() -> KillBandos.BANDOS_ROOM.contains(Players.getLocal())
//                 4000 y check because KC isnt reset on teleport out
                || (Players.getLocal().getY() > 4000 && GetBandosKC.getBandosKillcount() >= 40)
//                         < 8000 y is a check for not in the wilderness gwd
                || (Inventory.contains(ItemID.ECUMENICAL_KEY) && Players.getLocal().getY() > 4000 && Players.getLocal().getY() < 8000), settings);
        }
        bossFight.setSimpleName("Kill Bandos");

        tree.addChildren(
                new EmptyDeathsCoffer().setSimpleName("Death coffer"),

                new Fractal(() -> KillBandos.BANDOS_ROOM.contains(Players.getLocal()))
                        .setSimpleName("Boss fight")
                        .addChildren(
                                new FixBarrows().setSimpleName("Fix barrows"),
                                new ConfigureRunepouch().setSimpleName("Configure Rune Pouch"),
                                new RechargeBlowpipe().setSimpleName("Recharge blowpipe"),
                                bossFight
                        ),

                new DistributedRangeTraining(() -> Skills.getRealLevel(Skill.RANGED) < settings.f2pRangeTarget)
                        .setDefenceTarget(settings.defenceTarget)
                        .setSimpleName("FTP range"),

                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),

                new CollectHerbBoxes().setSimpleName("Herb boxes"),
                new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 61)
                        .setSimpleName("61 Magic for TP & Alch"),

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < 70).setSimpleName("70 Str for big door"),
                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < Math.max(43, settings.prayerTarget))
                        .setSimpleName("Prayer (43 min)"),
                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Get off 330"),


                SandCrabs.getRange(() -> Skills.getRealLevel(Skill.RANGED) < 35)
                        .setSimpleName("Range training (Pre avas)"),
                new Fractal(() -> !PaidQuest.ANIMAL_MAGNETISM.isFinished())
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

                new GetMoreAvas().setSimpleName("Get more avas"),
                new LeaveAvaRoom().setSimpleName("Leave ava"),
                new ConfigurableRangeTraining(() -> Skills.getRealLevel(Skill.RANGED) < rangeTarget || Skills.getRealLevel(Skill.DEFENCE) < defTarget, settings.defenceTarget)
//                        .setDefenceTarget(settings.defenceTarget)
                        .setSimpleName("Range training"),
                new AgilityBranch(() -> Skills.getRealLevel(Skill.AGILITY) < 15).setSimpleName("Need 15 agility for some rocks"),

                new DruidicRitual().setSimpleName("Drudic ritual"),
                new DeathPlateau().setSimpleName("Death plateau"),
                new TrollStronghold().setSimpleName("Troll stronghold"),
                new HerbloreBranch(() -> Skills.getRealLevel(Skill.HERBLORE) < 31, false),
                new EadgarsRuse().setSimpleName("Eadgars ruse"),


                new PlaceRopes(false).setSimpleName("Place GWD rope"),
                new GWDRechargeAtFerox().setSimpleName("Use Ferox pool"),

                new Fractal(() -> !GetKreeKC.ARMADYL_EYRiE.contains(Players.getLocal())
                        && settings.ecuKeyStrategy == EcuKeyStrategy.AVIANSIES
                        && (!OwnedItems.contains(ItemID.ECUMENICAL_KEY) || (!keyLock && settings.threeKeysPerTrip)))
                        .addChildren(
                                new AdvStandardCombat(() -> true,
                                        AVIANS,
                                        () -> NPCs.closest(x -> x.getName().toLowerCase().contains("avian")
                                                && AVIANS.contains(x)
                                                && x.getLevel() <= 92), ItemID.SWORDFISH)
                                        .setReAggroCheck(x -> x.getName().toLowerCase().contains("avian"))
                                        .setFlickBoostTiming(600, Prayer.EAGLE_EYE)
                                        .setStyleSupplier(() -> Skills.getRealLevel(Skill.DEFENCE) >= 80 ? CombatStyle.RANGED_RAPID : CombatStyle.RANGED_DEFENCE)
                                        .setLootFilter(x -> ItemVariants.LOOTING_BAG.contains(x.getId())
                                                || x.getId() == ItemID.ECUMENICAL_KEY
                                                || AVIANS.contains(x) && x.getId() != ItemID.BONES
                                                && (x.getItem().getLivePrice() * x.getAmount()) > 500)
                                        .setInventoryLoadout(aviansiesLoadout)
                                        .setEquipmentLoadout(aviansiesEquipmentLoadout)
                                        .setPrependLogic(() -> {
                                            if (Equipment.count(ItemID.RUNE_DART) > 120)
                                                new BankAllEquipmentEvent().execute();
                                            if (Equipment.count(ItemID.ADAMANT_DART) > 120)
                                                new BankAllEquipmentEvent().execute();

                                            if (!SpecialWalker.leaveAvasRoom()) return true;

                                            if (AVIANS.contains(Players.getLocal()) && Skills.getBoostedLevel(Skill.RANGED) <= Skills.getRealLevel(Skill.RANGED) + 5) {
                                                Item pot = ItemVariants.RANGE_POTION.getItem();
                                                if (pot != null && pot.interact("Drink")) {
                                                    Antiban.sleepUntil(() -> Skills.getBoostedLevel(Skill.RANGED) > Skills.getRealLevel(Skill.RANGED) + 5, 1400);
                                                    return true;
                                                }

                                            }
                                            return false;
                                        })
                                        .setSimpleName("Avians")
                        ).setSimpleName("Get Ecu-key"),

                new MakeTrollHeimTabs(tree.getSettings().useTrollheimTabs),
                new FixBarrows().setSimpleName("Fix barrows"),
                new ConfigureRunepouch().setSimpleName("Configure Rune Pouch"),
                new RechargeBlowpipe().setSimpleName("Recharge blowpipe"),

                new RatConfigureQuickPrayers(() -> new Prayer[]{PVMUtil.getBestRangePray(), Prayer.PROTECT_FROM_MISSILES}).setSimpleName("Configure qp"),
                // dont mule off while in gwd, it wastes time
                new Fractal(() -> Players.getLocal().getY() < 4000 && !Combat.isInWild() && (MuleOff.timer == null || MuleOff.timer.finished()))
                        .addChildren(
                                new MuleOff().setSimpleName("Mule Off")
                        )
                        .setSimpleName("Safe mule off"),
                bossFight,
                new GetBandosKC(() -> true)
                        .setSimpleName("Get KC")
        );
//        new AIAntiban();
        new EasyWildernessDiary();
        new MediumWildernessDiary(); // create this to add all the needed webnodes
    }

    boolean hasLootInBag = true;
    boolean nodesCut;
    // set true when you own 3 keys, set false when you are out of keys (& low Y axis), false will force avians
    boolean keyLock = false;

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (Client.getGameStateID() == 45) return ReactionGenerator.getQuick();
        if (!Client.isLoggedIn()) return ReactionGenerator.getQuick();

        if (MyVarps.getTutVarp() > 1000) {

            if (Bank.isCached()) {
                if (OwnedItems.count(ItemID.ECUMENICAL_KEY) == 3) {
                    keyLock = true;
                }

                // y check is important or you would use it on the door and then teleport out
                if (!OwnedItems.contains(ItemID.ECUMENICAL_KEY) && Players.getLocal().getY() < 3500) {
                    keyLock = false;
                }
            }

            if (!nodesCut && PaidQuest.EADGARS_RUSE.isFinished()) {
                nodesCut = true;
                Logger.info("Cutting nodes after eadgars ruse ");
                Area cutNodesArea = new Area(2790, 3652, 2936, 3599);
                WebFinder wf = WebFinder.getWebFinder();
                List<AbstractWebNode> dragonNodes = wf.getAll().stream().filter(x -> cutNodesArea.contains(x.getTile())).collect(Collectors.toList());
                dragonNodes.forEach(wf::removeNode);
            }

            if (AVIANS.contains(Players.getLocal())) hasLootInBag = true;

            if (hasLootInBag) {
                if (Bank.isOpen() && ItemVariants.LOOTING_BAG.getItem() != null) {
                    Logger.info("Emptying looting bags");
                    new EmptyLootingBagEvent().executed();
                    hasLootInBag = false;
                }
            }

            if (ClientSettings.areItemPilesOnDeathEnabled()) {
                if (Bank.isOpen()) Bank.close();
                Logger.info("Disabling item piles on death");
                ClientSettings.toggleItemPilesOnDeath(false);
                return ReactionGenerator.getNormal();
            }

            if (!ChangeAlchWarning.setHighAlchWarning(500_000)) {
                Logger.info("Setting alch warning price");
                return ReactionGenerator.getNormal();
            }


        }

        // wildy gwd boulder thats a npc for some reason
        NPC boulderObstacle = (NPCs.closest(x -> x.getName().equals("<col=00ffff>Boulder</col>")));
        if (Skills.getBoostedLevel(Skill.STRENGTH) >= 60
                && boulderObstacle != null
                && boulderObstacle.distance() < 5
                && boulderObstacle.getX() < Players.getLocal().getX()) {
            Logger.info("Moving boulder");
            boulderObstacle.interact("Move");
            Antiban.sleepUntil(() -> boulderObstacle.getX() > Players.getLocal().getX(), 4400);
            return ReactionGenerator.getNormal();
        }
        return tree.run();
    }

    Timer runtime = new Timer();
    int avianGP = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

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

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "target: " + target,
                "Avian GP: " + avianGP,
//                "Kree GP (Actual) " + KillBandos.BandosGP,
//                "Kree GP (EV) " + KillBandos.killCount * 300_000,
//                "Kree kills " + KillBandos.killCount
        };
    }

    @Override
    public void onScriptPaint(Graphics g) {
//        NPCs.all(KillBandos.KREE_BOSS_ROOM::contains)
//                .forEach(x -> {
//                    g.setColor(Color.BLUE);
//                    g.drawPolygon(x.getTrueTile().getPolygon());
//                    g.setColor(Color.WHITE);
//                    g.drawPolygon(x.getTile().getPolygon());
//                    g.setColor(Color.YELLOW);
//                    g.drawPolygon(x.getServerTile().getPolygon());
//                });
    }

    @Override
    public String getScriptName() {
        return "cCBandosFarm";
    }

    public static int grossGp = 0;

    @Override
    public int getMoneyMade() {
        return avianGP + KillBandos.bandosGP + grossGp;
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
        if (!Combat.isInWild()) return;
        avianGP += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!Combat.isInWild()) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }

        avianGP += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        if (!Combat.isInWild()) return;
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;
        avianGP += (incoming.getLivePrice() + 1) * quantity;
    }
}

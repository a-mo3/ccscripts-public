package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.teleports.MagicTeleport;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.GameStateListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.antipk.AntiPkBranch;
import org.dreambot.behaviour.method.antipk.AntiPkLeaveBosses;
import org.dreambot.behaviour.method.calvarion.GetMoneyForFees;
import org.dreambot.behaviour.method.revs.ChargeBracelet;
import org.dreambot.behaviour.method.revs.behaviour.*;
import org.dreambot.behaviour.method.revs.data.RevenantLocations;
import org.dreambot.behaviour.method.spindel.ExitWithLoot;
import org.dreambot.behaviour.method.spindel.RechargeWildyWeapon;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.MuleOffItem;
import org.dreambot.behaviour.misc.TurnInLootKeys;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.animalmagnetism.util.LeaveAvaRoom;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.combat.F2PMeleeCombats;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.magic.F2PMagicBranch;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.range.ConfigurableRangeTraining;
import org.dreambot.behaviour.training.range.DistributedRangeTraining;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.behaviour.wilddiary.HardWildernessDiary;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.TimedShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.events.EmptyLootingBagEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.*;
import org.dreambot.scriptdata.RevenantSettings;
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

public class RevenantsScript extends PseudoScript implements ItemContainerListener, ChatListener, GameStateListener {
    FractalRoot<RevenantSettings> tree = new FractalRoot<>(new RevenantSettings(), getScriptName());
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
        Client.getInstance().addEventListener(this);

        AbstractResponseEvent.addGlobalExitCondition(() -> Inventory.contains("Loot key") && !Combat.isInWild(), "LOOT_KEY");


        WithdrawLoadoutEvent.sellList = LootRevs.LOOT;
//        MuleOff.LOOT = new int[]{};
        MuleOff.LOOT = LootRevs.LOOT;
        MuleOff.muleOffItems = new MuleOffItem[]{
                new MuleOffItem(ItemID.CRAWS_BOW_U,
                        () -> !tree.getSettings().revenantEquipmentLoadout.name().contains("CRAWS"),
                        0
                ),
                new MuleOffItem(ItemID.URSINE_CHAINMACE_U,
                        () -> !tree.getSettings().revenantEquipmentLoadout.name().contains("URSINE"),
                        0
                ),
                new MuleOffItem(ItemID.WEBWEAVER_BOW_U,
                        () -> !tree.getSettings().revenantEquipmentLoadout.name().contains("WEBWEAVER"),
                        0
                ),
                new MuleOffItem(ItemID.VIGGORAS_CHAINMACE_U,
                        () -> !tree.getSettings().revenantEquipmentLoadout.name().contains("VIGGAORA"),
                        0
                ),
                new MuleOffItem(ItemID.ACCURSED_SCEPTRE_U,
                        () -> !tree.getSettings().revenantEquipmentLoadout.name().contains("ACCIRSED"),
                        0
                ),
                new MuleOffItem(ItemID.THAMMARONS_SCEPTRE_U,
                        () -> !tree.getSettings().revenantEquipmentLoadout.name().contains("THAMMARON"),
                        0
                ),
        };


        Logger.info("Init");
        tree.setSimpleName("cCRevenants");


        tree.addChildren(
                new EmptyDeathsCoffer().setSimpleName("Empty grave"),

                new TimedShuffleFractal(40, 1180)
                        .addChildren(
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
                                        .setSimpleName("F2P range")
                        ),


                new Fractal(() -> tree.getSettings().stopAfterFTP)
                        .setSimpleName("Stop after f2p")
                        .setPrependLogic(() -> {
                            Logger.info("Stop after F2P setting enabled");
                            ScriptManager.getScriptManager().stop();
                            return false;
                        }),

                new GetMembershipBranch().setSimpleName("Get Membership"),


                new TimedShuffleFractal(49, 190)
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

                                new ConfigurableRangeTraining(() -> Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget, tree.getSettings().defenceTarget)
//                        .setDefenceTarget(tree.getSettings().defenceTarget)
                                        .setSimpleName("Range training")
                                        .setPrependLogic(() -> {
                                            if (Client.isDynamicRegion()) {
                                                Magic.castSpell(Normal.HOME_TELEPORT);
                                                Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                                            }
                                            return false;
                                        }),
                                new CraftingBranch(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                                new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS)
                                        .setSimpleName("Burn logs need it for slayer")
                        ),

                new Fractal(() -> tree.getSettings().rangeTarget > 1 && !PaidQuest.ANIMAL_MAGNETISM.isFinished())
                        .addChildren(
                                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                                new RestlessGhost().setSimpleName("Restless ghost"),
                                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                                new PriestInPeril().setSimpleName("Priest in peril"),
                                new AnimalMagnetismBranch().setSimpleName("Animal Magnetism")
                        ),

                new Fractal(() -> !Combat.isInWild() && Bank.isCached()
                        && !OwnedItems.containsAny(ItemID.AVAS_ASSEMBLER, ItemID.AVAS_ATTRACTOR, ItemID.AVAS_ACCUMULATOR)
                        && tree.getSettings().rangeTarget > 1)
                        .setSimpleName("Get Avas").addChildren(
                                new GetMoreAvas().setSimpleName("More avas")
                        ),
                new LeaveAvaRoom().setSimpleName("Leave avas"),

                new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < tree.getSettings().magicTarget)
                        .setSimpleName("Magic training"),

                new MuleOff()
                        .setSimpleName("Mule Off"),

                new AntiPkBranch().setSimpleName("Anti PK"),
                new TurnInLootKeys().setSimpleName("Turn in loot keys"),
                new HardWildernessDiary(() -> tree.getSettings().doHardWildernessDiary).setSimpleName("Hard diary"),

                new RechargeWildyWeapon(ItemID.CRAWS_BOW_U, ItemID.CRAWS_BOW, rechargedExitDanger,
                        tree.getSettings().etherRechargeQuantity).setSimpleName("Recharge Craws")
                        .setAcceptCondition(() -> tree.getSettings().revenantEquipmentLoadout.name().contains("CRAW")
                                && (OwnedItems.contains(ItemID.CRAWS_BOW_U) || needsToRecharge)),

                new RechargeWildyWeapon(ItemID.WEBWEAVER_BOW_U, ItemID.WEBWEAVER_BOW, rechargedExitDanger,
                        tree.getSettings().etherRechargeQuantity).setSimpleName("Recharge Webweaver")
                        .setAcceptCondition(() -> tree.getSettings().revenantEquipmentLoadout.name().contains("WEBWEAVER")
                                && (OwnedItems.contains(ItemID.WEBWEAVER_BOW_U) || needsToRecharge)),

                new RechargeWildyWeapon(ItemID.URSINE_CHAINMACE_U, ItemID.URSINE_CHAINMACE, rechargedExitDanger,
                        tree.getSettings().etherRechargeQuantity).setSimpleName("Recharge Ursine")
                        .setAcceptCondition(() -> tree.getSettings().revenantEquipmentLoadout.name().contains("URSINE")
                                && (OwnedItems.contains(ItemID.URSINE_CHAINMACE_U) || needsToRecharge)),

                new RechargeWildyWeapon(ItemID.VIGGORAS_CHAINMACE_U, ItemID.VIGGORAS_CHAINMACE, rechargedExitDanger,
                        tree.getSettings().etherRechargeQuantity).setSimpleName("Recharge Viggora")
                        .setAcceptCondition(() -> tree.getSettings().revenantEquipmentLoadout.name().contains("VIGGORA")
                                && (OwnedItems.contains(ItemID.VIGGORAS_CHAINMACE_U) || needsToRecharge)),

                new RechargeWildyWeapon(ItemID.ACCURSED_SCEPTRE_U, ItemID.ACCURSED_SCEPTRE, rechargedExitDanger,
                        tree.getSettings().etherRechargeQuantity).setSimpleName("Recharge Accursed")
                        .setAcceptCondition(() -> tree.getSettings().revenantEquipmentLoadout.name().contains("ACCU")
                                && (OwnedItems.contains(ItemID.ACCURSED_SCEPTRE_U) || needsToRecharge)),

                new RechargeWildyWeapon(ItemID.THAMMARONS_SCEPTRE_U, ItemID.THAMMARONS_SCEPTRE, rechargedExitDanger,
                        tree.getSettings().etherRechargeQuantity).setSimpleName("Recharge Thammarons")
                        .setAcceptCondition(() -> tree.getSettings().revenantEquipmentLoadout.name().contains("THAM")
                                && (OwnedItems.contains(ItemID.THAMMARONS_SCEPTRE_U) || needsToRecharge)),

                new GetPkSkull(() -> tree.getSettings().skullUp && !Players.getLocal().isSkulled() && !Combat.isInWild())
                        .setSimpleName("get PK Skull"),

                new ChargeBracelet().setSimpleName("Charge ether bracelet"),

                new ExitRevs(tree.getSettings().exitLootValue, 1).setSimpleName("Exit w/ loot"),
                new GetMoneyForFees(100_000).setSimpleName("Get entrance fee"),
                new GoToRevs(() -> !RevenantLocations.WHOLE_REV_CAVES.contains(Players.getLocal()))
                        .setSimpleName("Go to revs"),
                new LootRevs().setSimpleName("Loot"),
                new FightRevs().setSimpleName("Fight revs")
        );
//        new AIAntiban();

        WebFinder wf = WebFinder.getWebFinder();
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

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (!Combat.isInWild())
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


        Player threat = Players.closest(x -> x.isInteracting(Players.getLocal())
                && CombatUtil.canAttackMe(x)
        );
        if (Combat.isInWild() && threat != null) {
            Logger.info("Set threat " + threat.getName());
            AntiPkBranch.setAttackerName(threat.getName());
        }

        if (!Combat.isInWild() && !Bank.isCached()) {
            if (!SpecialWalker.leaveAvasRoom()) return ReactionGenerator.getNormal();
            if (Bank.isOpen()) Bank.close();
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isSkullPreventionActive()) {
            Logger.info("Turn off skull prevention");
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleSkullPrevention(false);
            return ReactionGenerator.getNormal();
        }

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

        if (RevenantLocations.WHOLE_REV_CAVES.contains(Players.getLocal())) hasLootInBag = true;

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
                "Deaths " + deathCount + "/" + AntiPkBranch.getPkEventCounter()
        };
    }

    @Override
    public String getScriptName() {
        return "cCRevenantFarm";
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
        if (!RevenantLocations.WHOLE_REV_CAVES.contains(Players.getLocal())) return;
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
        if (!RevenantLocations.WHOLE_REV_CAVES.contains(Players.getLocal())) return;
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
        if (!RevenantLocations.WHOLE_REV_CAVES.contains(Players.getLocal())) return;
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

        if (message.getMessage().toLowerCase().contains("is out of charges")) {
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
        Logger.info("Loot bag added");
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
        RevenantSettings settings = tree.getSettings();
        if (Skills.getRealLevel(Skill.ATTACK) < settings.attackTarget) return true;
        if (Skills.getRealLevel(Skill.DEFENCE) < settings.defenceTarget) return true;
        return Skills.getRealLevel(Skill.STRENGTH) < settings.strengthTarget;
    }
}

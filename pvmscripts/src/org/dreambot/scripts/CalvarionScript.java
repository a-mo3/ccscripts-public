package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.antiban.Antiban;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.widget.Widgets;
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
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.antipk.AntiPkBranch;
import org.dreambot.behaviour.method.antipk.AntiPkLeaveBosses;
import org.dreambot.behaviour.method.calvarion.*;
import org.dreambot.behaviour.method.calvarion.tickcalv.TickCalvarionBranch;
import org.dreambot.behaviour.method.spindel.AntiCrashWildyBosses;
import org.dreambot.behaviour.method.spindel.ExitWithLoot;
import org.dreambot.behaviour.method.spindel.RechargeWildyWeapon;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.SandCrabs;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.behaviour.wilddiary.EasyWildernessDiary;
import org.dreambot.behaviour.wilddiary.HardWildernessDiary;
import org.dreambot.behaviour.wilddiary.MediumWildernessDiary;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.discordwebhook.scouter.ScoutFractal;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.DecantPotionEvent;
import org.dreambot.fractals.events.EmptyLootingBagEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.CalvarionSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CalvarionScript extends PseudoScript implements ItemContainerListener, ChatListener, GameStateListener {
    FractalRoot<CalvarionSettings> tree = new FractalRoot<>(new CalvarionSettings(), getScriptName());
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

    @Override
    public void onArgs(String... args) {
        for (String arg : args) {
            Arrays.stream(CalvarionLoadout.values())
                    .filter(x -> x.name().toLowerCase().contains(arg))
                    .findFirst()
                    .ifPresent(x -> {
                        Logger.info("Set loadout to " + x);
                        tree.getSettings().loadout = x;
                    });
        }
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        MuleOff.LOOT = new int[]{
                ItemID.DRAGON_2H_SWORD,
                ItemID.DRAGON_PICKAXE,
                ItemID.SKULL_OF_VETION,
                ItemID.RING_OF_THE_GODS,
                ItemID.VOIDWAKER_BLADE,

                ItemID.DARK_CRAB,
                ItemID.SUPER_RESTORE4,

                ItemID.RUNE_PICKAXE,
                ItemID.ANCIENT_STAFF,
                ItemID.RUNE_2H_SWORD,

                ItemID.CHAOS_RUNE,
                ItemID.DEATH_RUNE,
                ItemID.BLOOD_RUNE,
                ItemID.CANNONBALL,

                ItemID.GOLD_ORE,
                ItemID.LIMPWURT_ROOT,
                ItemID.MAGIC_LOGS,
                ItemID.OAK_PLANK,
                ItemID.WINE_OF_ZAMORAK,
                ItemID.UNCUT_RUBY,
                ItemID.UNCUT_DIAMOND,
                ItemID.DRAGON_BONES,
                ItemID.UNCUT_DRAGONSTONE,
                ItemID.MORT_MYRE_FUNGUS,
                ItemID.GRIMY_RANARR_WEED,

                ItemID.SANFEW_SERUM4,
                ItemID.SUPERCOMPOST,
                ItemID.YEW_SEED,
                ItemID.MAGIC_SEED,
                ItemID.PALM_TREE_SEED,
                ItemID.BLIGHTED_ANGLERFISH,
                ItemID.BLIGHTED_KARAMBWAN,
                ItemID.SUPER_COMBAT_POTION1,
                ItemID.SUPER_COMBAT_POTION2,
                ItemID.SUPER_COMBAT_POTION3,
                ItemID.AMULET_OF_GLORY_UNCHARGED,
                ItemID.RING_OF_WEALTH
        };

        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        Logger.info("Init");
        tree.setSimpleName("cCCalvarion");

        tree.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new ScoutFractal(),
                new ReactionSettingsFractal(),
                new PutPetAway(),
                new TutorialTree().setSimpleName("Tutorial island"),
                new LampHandler().setSimpleName("Lamp handler"),
                new GetMembershipBranch().setSimpleName("Get Membership"),
                new EmptyDeathsCoffer(tree.getSettings().emptyCoffer).setSimpleName("Empty coffer"),

                // - 100k so we dont trigger this again by like, picking up 10 coins
//                new MuleOff(500, tree.getSettings().maxGP - 100_000)
//                        .setAcceptCondition(() -> !Combat.isInWild()
//                                && tree.getSettings().enforceMaxGP
//                                && OwnedItems.count(ItemID.COINS_995) > tree.getSettings().maxGP)
//                        .setSimpleName("More gp than max, muling off"),

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < 60)
                        .setStyleSupplier(() -> {
                            int atk = Skills.getRealLevel(Skill.ATTACK);
                            int str = Skills.getRealLevel(Skill.STRENGTH);
                            int def = Skills.getRealLevel(Skill.DEFENCE);
                            if (Skills.getRealLevel(Skill.ATTACK) >= tree.getSettings().atkTarget)
                                atk = 100;
                            if (Skills.getRealLevel(Skill.STRENGTH) >= tree.getSettings().strTarget)
                                str = 100;
                            if (Skills.getRealLevel(Skill.DEFENCE) >= tree.getSettings().defTarget)
                                def = 100;
                            if (atk >= 30 && def >= 30) return CombatStyle.STRENGTH;
                            if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                            if (atk <= def) return CombatStyle.ATTACK;
                            return CombatStyle.DEFENCE;
                        })
                        .setSimpleName("Melee training - 60 Str min"),


                new ConfigurableMeleeTraining(() -> {
                    CalvarionSettings settings = tree.getSettings();
                    if (Skills.getRealLevel(Skill.ATTACK) < settings.atkTarget) return true;
                    if (Skills.getRealLevel(Skill.DEFENCE) < settings.defTarget) return true;
                    return Skills.getRealLevel(Skill.STRENGTH) < settings.strTarget;

                })
                        .setStyleSupplier(() -> {
                            int atk = Skills.getRealLevel(Skill.ATTACK);
                            int str = Skills.getRealLevel(Skill.STRENGTH);
                            int def = Skills.getRealLevel(Skill.DEFENCE);
                            if (Skills.getRealLevel(Skill.ATTACK) >= tree.getSettings().atkTarget)
                                atk = 100;
                            if (Skills.getRealLevel(Skill.STRENGTH) >= tree.getSettings().strTarget)
                                str = 100;
                            if (Skills.getRealLevel(Skill.DEFENCE) >= tree.getSettings().defTarget)
                                def = 100;
                            if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                            if (atk <= def) return CombatStyle.ATTACK;
                            return CombatStyle.DEFENCE;
                        })
                        .setSimpleName("Melee training - Based on setting"),

                SandCrabs.getRange(() -> Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget)
//                SandCrabs.getRange(() -> true)
                        .setDefenceTarget(40)
                        .setSimpleName("Range training")
                        .setPrependLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Magic.castSpell(Normal.HOME_TELEPORT);
                                Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                            }
                            return false;
                        }),

                // todo put crafting in a branch so we dont have to eval so much per loop
                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < tree.getSettings().prayerTarget)
                        .setCleanAfterAccomplished(true)
                        .setSimpleName("Prayer Training"),
                new GetOff330(x -> x.isNormal() && x.getWorld() != 401 && x.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel()),
                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Burn logs need it for slayer"),
                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                new EasyWildernessDiary()
                        .setPrependLogic(() -> !SpecialWalker.leaveAvasRoom())
                        .setSimpleName("Easy diary"),
                new RestlessGhost().setSimpleName("Restless ghost"),
                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                new PriestInPeril().setSimpleName("Priest in peril"),
                new MediumWildernessDiary().setSimpleName("Medium diary"),
                new HardWildernessDiary().setSimpleName("Hard diary"),
                new RechargeWildyWeapon(ItemID.URSINE_CHAINMACE_U, ItemID.URSINE_CHAINMACE, rechargedExitDanger,
                        tree.getSettings().etherRechargeQuantity).setSimpleName("Recharge Ursine")
                        .setAcceptCondition(() -> tree.getSettings().loadout.name().contains("URSINE")
                                && (OwnedItems.contains(ItemID.URSINE_CHAINMACE_U) || needsToRecharge)),

                new RechargeWildyWeapon(ItemID.VIGGORAS_CHAINMACE_U, ItemID.VIGGORAS_CHAINMACE, rechargedExitDanger,
                        tree.getSettings().etherRechargeQuantity).setSimpleName("Recharge Viggora")
                        .setAcceptCondition(() -> tree.getSettings().loadout.name().contains("VIGGORA")
                                && (OwnedItems.contains(ItemID.VIGGORAS_CHAINMACE_U) || needsToRecharge)),

                new MuleOff().setSimpleName("Mule off")
                        .setPrependLogic(() -> {
                            if (!SpecialWalker.leaveAvasRoom()) return true;
                            new DecantPotionEvent("blighted super", "prayer potion", "super combat").executed();
                            return false;
                        }),
                new AntiPkBranch().setSimpleName("Experimental AntiPk"),
                new ExitCalvarionWithLoot(tree.getSettings().exitLootValue, tree.getSettings().maxKillsPerRun)
                        .setSimpleName("Leave cus we're rich")
                        .setPrependLogic(() -> !SpecialWalker.leaveAvasRoom()),
                new GetMoneyForFees().setSimpleName("Get money for fees"),
                // should be the same prayers as moons
                new CalvarionConfigurePrayerFlicks().setSimpleName("Set up quick prayers"),
                new GoToCalvarion()
                        .setPrependLogic(() -> !SpecialWalker.leaveAvasRoom())
                        .setSimpleName("Gear up"),

                new AntiCrashWildyBosses().setSimpleName("Leave, anti crash"),
                new TickCalvarionBranch(() -> true, tree.getSettings()).setSimpleName("Fight calvarion")
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

        Area wildernesChaosTempleNodes = new Area(3218, 3637, 3259, 3586);
        badNodes = wf.getAll().stream().filter(x -> wildernesChaosTempleNodes.contains(x.getTile())).collect(Collectors.toList());
        badNodes.forEach(wf::removeNode);
        // ernest the chicken webnode
        // WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));

        // AbstractResponseEvent.addGlobalExitCondition(new EventExitCondition(() -> !lastWorldHop.finished(), "RECENT_WORLD_HOP"));
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        NPC boulderObstacle = (NPCs.closest(x -> x.getName().equals("<col=00ffff>Boulder</col>")));
        if (boulderObstacle != null && boulderObstacle.distance() < 5 && boulderObstacle.getX() < Players.getLocal().getX()) {
            Logger.info("Moving boulder");
            boulderObstacle.interact("Move");
            Antiban.sleepUntil(() -> boulderObstacle.getX() > Players.getLocal().getX(), 4400);
            return ReactionGenerator.getNormal();
        }

        Player threat = CombatUtil.getThreat();
        if (Combat.isInWild() && threat != null) {
            Logger.info("Set threat " + threat.getName());
            AntiPkBranch.setAttackerName(threat.getName());
        }

        if (Skills.getRealLevel(Skill.AGILITY) >= 40 && AgilityBranch.onDraynorCourse()) {
            Logger.info("Getting off draynor course");
            if (Walking.shouldWalk()) Walking.walk(BankLocation.LUMBRIDGE);
            return ReactionGenerator.getNormal();
        }

        if (Skills.getRealLevel(Skill.AGILITY) >= 52 && AgilityBranch.onCanifisCourse()) {
            Logger.info("Getting off canifis course");
            if (Walking.shouldWalk()) Walking.walk(BankLocation.LUMBRIDGE);
            return ReactionGenerator.getNormal();
        }

        if (!Combat.isInWild() && !Bank.isCached()) {
            if (!SpecialWalker.leaveAvasRoom()) return ReactionGenerator.getNormal();
            if (Bank.isOpen()) Bank.close();
            if (Walking.shouldWalk()) Bank.open();
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

        if (GoToCalvarion.CALVARION_ARENA.contains(Players.getLocal())) hasLootInBag = true;

        if (hasLootInBag) {
            if (Bank.isOpen() && ItemVariants.LOOTING_BAG.getItem() != null) {
                Logger.info("Emptying looting bags");
                new EmptyLootingBagEvent().executed();
                hasLootInBag = false;
            }
        }

        // dont run the tree while hopping worlds because equipment state will make you do loadouts you shouldnt
        if (!Client.isLoggedIn()) return ReactionGenerator.getNormal();
        if (Client.getGameStateID() == 45) return ReactionGenerator.getNormal();
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
                String.format("Inv loot value %s / %s", df.format(invValue + LootingBag.value()), df.format(tree.getSettings().exitLootValue)),
                "Loot bag value " + df.format(LootingBag.value()),
                "Deaths " + deathCount,
                "Leave: " + AntiCrashWildyBosses.hasToLeave
        };
    }

    @Override
    public String getScriptName() {
        return "cCCalvarionScript";
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
        if (!AntiPkLeaveBosses.CALVARION_ARENA.contains(Players.getLocal())) return;
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
        if (!AntiPkLeaveBosses.CALVARION_ARENA.contains(Players.getLocal())) return;
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
        if (!AntiPkLeaveBosses.CALVARION_ARENA.contains(Players.getLocal())) return;
        if (Bank.isOpen()) return;
        if (ExitWithLoot.ignoredIds.contains(incoming.getId())) return;
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
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
}

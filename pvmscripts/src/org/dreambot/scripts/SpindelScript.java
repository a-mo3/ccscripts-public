package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
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
import org.dreambot.api.methods.quest.book.PaidQuest;
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
import org.dreambot.behaviour.method.calvarion.GetMoneyForFees;
import org.dreambot.behaviour.method.spindel.*;
import org.dreambot.behaviour.method.spindel.range.RangeSpindelBranch;
import org.dreambot.behaviour.method.spindel.tickspindel.SpindelState;
import org.dreambot.behaviour.method.spindel.tickspindel.TickSpindelBranch;
import org.dreambot.behaviour.method.spindel.tickspindel.TickSpindelWebDodge;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.range.ConfigurableRangeTraining;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
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
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.SpindelSettings;
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

public class SpindelScript extends PseudoScript implements ItemContainerListener, ChatListener, GameStateListener {
    FractalRoot<SpindelSettings> tree = new FractalRoot<>(new SpindelSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();

    // for getting out of wild when your need to recharge whatever wildy weapon
    Supplier<Boolean> rechargedExitDanger = () -> {
        if (Combat.isInWild()) {
            SpindelAntiPk.leaveSpindel();
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
            Arrays.stream(SpindelLoadout.values())
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


//        WithdrawLoadoutEvent.sellList = LootSpindel.LOOT;
//        MuleOff.LOOT = LootSpindel.LOOT;


        Logger.info("Init");
        tree.setSimpleName("cCSpindel");

        Fractal spindelMethod = tree.getSettings().loadout.isRange ? new RangeSpindelBranch(() -> true) : new TickSpindelBranch(() -> true);

        tree.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new ScoutFractal(),
                new ReactionSettingsFractal(),
                new PutPetAway(),
                new TutorialTree().setSimpleName("Tutorial island"),
                new LampHandler().setSimpleName("Lamp handler"),
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Empty death coffer"),

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < 60)
                        .setStyleSupplier(() -> {
                            int atk = Skills.getRealLevel(Skill.ATTACK);
                            int str = Skills.getRealLevel(Skill.STRENGTH);
                            int def = Skills.getRealLevel(Skill.DEFENCE);
                            if (Skills.getRealLevel(Skill.ATTACK) >= tree.getSettings().atkTarget) atk = 100;
                            if (Skills.getRealLevel(Skill.STRENGTH) >= tree.getSettings().strTarget) str = 100;
                            if (Skills.getRealLevel(Skill.DEFENCE) >= tree.getSettings().defTarget) def = 100;
                            if (atk >= 30 && def >= 30) return CombatStyle.STRENGTH;
                            if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                            if (atk <= def) return CombatStyle.ATTACK;
                            return CombatStyle.DEFENCE;
                        })
                        .setSimpleName("Melee training - 60 Str min"),


                new ConfigurableMeleeTraining(() -> {
                    SpindelSettings settings = tree.getSettings();
                    if (Skills.getRealLevel(Skill.ATTACK) < settings.atkTarget) return true;
                    if (Skills.getRealLevel(Skill.DEFENCE) < settings.defTarget) return true;
                    return Skills.getRealLevel(Skill.STRENGTH) < settings.strTarget;

                })
                        .setStyleSupplier(() -> {
                            int atk = Skills.getRealLevel(Skill.ATTACK);
                            int str = Skills.getRealLevel(Skill.STRENGTH);
                            int def = Skills.getRealLevel(Skill.DEFENCE);
                            if (Skills.getRealLevel(Skill.ATTACK) >= tree.getSettings().atkTarget) atk = 100;
                            if (Skills.getRealLevel(Skill.STRENGTH) >= tree.getSettings().strTarget) str = 100;
                            if (Skills.getRealLevel(Skill.DEFENCE) >= tree.getSettings().defTarget) def = 100;
                            if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                            if (atk <= def) return CombatStyle.ATTACK;
                            return CombatStyle.DEFENCE;
                        })
                        .setSimpleName("Melee training - Based on setting"),

                // todo put crafting in a branch so we dont have to eval so much per loop
                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < Math.max(43, tree.getSettings().prayerTarget)).setSimpleName("Prayer Training"),
                new GetOff330(x -> x.isNormal() && x.getWorld() != 401 && x.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel()),
                new ConfigurableRangeTraining(() -> Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget, 40)
                        .setSimpleName("Range training")
                        .setPrependLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Magic.castSpell(Normal.HOME_TELEPORT);
                                Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                            }
                            return false;
                        }),


                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Burn logs need it for slayer"),
                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                // only for range
                new RestlessGhost().setSimpleName("Restless ghost"),
                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                new PriestInPeril().setSimpleName("Priest in peril"),
                new Fractal(() -> !PaidQuest.ANIMAL_MAGNETISM.isFinished() && tree.getSettings().loadout.isRange).addChildren(
                        new CraftingBranch(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                        new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                        new AnimalMagnetismBranch().setSimpleName("Animal Magnetism")
                ).setSimpleName("Range training"),


                new EasyWildernessDiary()
                        .setPrependLogic(() -> !SpecialWalker.leaveAvasRoom())
                        .setSimpleName("Easy diary"),
                new MediumWildernessDiary().setSimpleName("Medium diary"),
                new HardWildernessDiary().setSimpleName("Hard diary"),

                new Fractal(() -> !OwnedItems.containsAny(ItemID.AVAS_ACCUMULATOR, ItemID.AVAS_ACCUMULATOR) && tree.getSettings().loadout.isRange)
                        .setSimpleName("Get Avas").addChildren(
                                new GetMoreAvas().setSimpleName("More avas")
                        ),
                new RechargeWildyWeapon(ItemID.CRAWS_BOW_U, ItemID.CRAWS_BOW, rechargedExitDanger,
                        tree.getSettings().etherRechargeQuantity).setSimpleName("Recharge Craws")
                        .setAcceptCondition(() -> tree.getSettings().loadout == SpindelLoadout.CRAWS_DHIDE
                                && (OwnedItems.contains(ItemID.CRAWS_BOW_U) || needsToRecharge)),

                new RechargeWildyWeapon(ItemID.WEBWEAVER_BOW_U, ItemID.WEBWEAVER_BOW, rechargedExitDanger,
                        tree.getSettings().etherRechargeQuantity).setSimpleName("Recharge Webweaver")
                        .setAcceptCondition(() -> tree.getSettings().loadout == SpindelLoadout.WEBWEAVER_DHIDE
                                && (OwnedItems.contains(ItemID.WEBWEAVER_BOW_U) || needsToRecharge)),

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

                new AntiPkBranch().setSimpleName("New AntiPk"),
                new ExitWithLoot(tree.getSettings().exitLootValue, tree.getSettings().maxKillsPerRun).setSimpleName("Leave cus we're rich")
                        .setPrependLogic(() -> !SpecialWalker.leaveAvasRoom()),
                new GetMoneyForFees().setSimpleName("Get money for fees"),
                new GoToSpindel()
                        .setPrependLogic(() -> !SpecialWalker.leaveAvasRoom())
                        .setSimpleName("Gear up"),


//                new RangeSpindelBranch(() -> true)
                new AntiCrashWildyBosses().setSimpleName("Leave, anti crash"),
                spindelMethod
        );
//        new AIAntiban();

        // remove web nodes to enter mage bank because they have no knife checks
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

        if (GoToSpindel.SPINDEL_CHASM.contains(Players.getLocal())) hasLootInBag = true;

        if (hasLootInBag) {
            if (Bank.isOpen() && ItemVariants.LOOTING_BAG.getItem() != null) {
                Logger.info("Emptying looting bags");
                new EmptyLootingBagEvent().executed();
                hasLootInBag = false;
            }
        }

        // dont run the tree while hopping worlds because equipment state will make you do loadouts you shouldnt
        if (Client.getGameStateID() == 45) return ReactionGenerator.getQuick();
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
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

        // todo something here for melee mode
        // 109k average drop price
        int projectedGp = RangeSpindelBranch.totalKills * 109_000;

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                String.format("Projected gp %s (%s / hr))", df.format(projectedGp), df.format(runtime.getHourlyRate(projectedGp))),
                String.format("Kills %d (%d) This trip %d", RangeSpindelBranch.totalKills, runtime.getHourlyRate(RangeSpindelBranch.totalKills), RangeSpindelBranch.killsThisTrip),
                "target: " + target,
                "Game state " + Client.getGameStateID(),
                "Spindel state " + SpindelState.getCurrentPhase() + " " + SpindelState.getCounter(),
                "Timer " + TickSpindelWebDodge.webExpiry.remaining()
        };
    }

    @Override
    public String getScriptName() {
        return "cCSpindel";
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
        if (TickSpindelWebDodge.lastWebCenter != null) {
            Arrays.stream(TickSpindelWebDodge.lastWebCenter.getArea(3).getTiles())
                    .forEach(x -> g.drawPolygon(x.getPolygon()));
        }

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
        if (Bank.isOpen()) return;
        if (ExitWithLoot.ignoredIds.contains(item.getId())) return;
        if (!SpindelAntiPk.SPINDEL_CHASM.contains(Players.getLocal())) return;
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info("item changed");
        if (Bank.isOpen()) return;
        if (!SpindelAntiPk.SPINDEL_CHASM.contains(Players.getLocal())) return;
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
        if (!SpindelAntiPk.SPINDEL_CHASM.contains(Players.getLocal())) return;
        if (Bank.isOpen()) return;
        if (ExitWithLoot.ignoredIds.contains(incoming.getId())) return;
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;

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

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added");
        grossGp += item.getLivePrice() * item.getAmount();
    }
}

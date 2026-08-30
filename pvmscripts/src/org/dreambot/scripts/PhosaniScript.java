package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GraphicsObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.GameStateListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.graphics.GraphicsObject;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.method.antipk.AntiPkLeaveBosses;
import org.dreambot.behaviour.method.nightmare.PhosaniBranch;
import org.dreambot.behaviour.method.spindel.ExitWithLoot;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.PhosaniSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.fractalsettings.SettingsRepository;
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

public class PhosaniScript extends PseudoScript implements ItemContainerListener, ChatListener, GameStateListener {
    FractalRoot tree = new FractalRoot(new PhosaniSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();

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
//        for (String arg : args) {
//            Arrays.stream(CalvarionLoadout.values())
//                    .filter(x -> x.name().toLowerCase().contains(arg))
//                    .findFirst()
//                    .ifPresent(x -> {
//                        Logger.info("Set loadout to " + x);
//                        SettingsRepository.findInstanceOf(new CalvarionSettings()).loadout = x;
//                    });
//        }
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
//        WithdrawLoadoutEvent.sellList = LootSpindel.LOOT;
//        MuleOff.LOOT = LootSpindel.LOOT;


        Logger.info("Init");
        tree.setSimpleName("cCPhosani");

        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < 60)
                        .setStyleSupplier(() -> {
                            int atk = Skills.getRealLevel(Skill.ATTACK);
                            int str = Skills.getRealLevel(Skill.STRENGTH);
                            int def = Skills.getRealLevel(Skill.DEFENCE);
                            if (atk >= 30 && def >= 30) return CombatStyle.STRENGTH;
                            if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                            if (atk <= def) return CombatStyle.ATTACK;
                            return CombatStyle.DEFENCE;
                        })
                        .setSimpleName("Melee training - 60 Str min"),


                new ConfigurableMeleeTraining(() -> {
                    PhosaniSettings settings = SettingsRepository.findInstanceOf(new PhosaniSettings());
                    if (Skills.getRealLevel(Skill.ATTACK) < settings.atkTarget) return true;
                    if (Skills.getRealLevel(Skill.DEFENCE) < settings.defTarget) return true;
                    return Skills.getRealLevel(Skill.STRENGTH) < settings.strTarget;

                })
                        .setSimpleName("Melee training - Based on setting"),

                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < SettingsRepository.findInstanceOf(new PhosaniSettings()).prayerTarget).setSimpleName("Prayer Training"),
                new PhosaniBranch(() -> true).setSimpleName("Phosani")
        );
//        new AIAntiban();

        WebFinder wf = WebFinder.getWebFinder();
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
//        if (!Client.isDynamicRegion() && !Bank.isCached()) {
//            if (!SpecialWalker.leaveAvasRoom()) return ReactionGenerator.getNormal();
//            if (Bank.isOpen()) Bank.close();
//            if (Walking.shouldWalk()) Bank.open();
//            return ReactionGenerator.getNormal();
//        }

        // dont run the tree while hopping worlds because equipment state will make you do loadouts you shouldnt
        if (Client.getGameStateID() == 45) return ReactionGenerator.getQuick();
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
                "cCPhosani: " + runtime.formatTime(),
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "Time Until Mule off: " + muleOff,
//                String.format("Earned gp %s (%s / hr))", df.format(grossGp), df.format(runtime.getHourlyRate(grossGp))),
//                String.format("Projected gp %s (%s / hr))", df.format(projectedGp), df.format(runtime.getHourlyRate(projectedGp))),
                "target: " + target,
                "Game state " + Client.getGameStateID(),
//                "History " + AbstractResponseEvent.history
        };
    }

    @Override
    public String getScriptName() {
        return "";
    }

    @Override
    public int getMoneyMade() {
        return 0;
    }

    @Override
    public Timer getRuntime() {
        return null;
    }

    @Override
    public long getMuleOffTime() {
        return 0;
    }

    @Override
    public Fractal getFractal() {
        return tree;
    }

    public static Area lastCornerArea = null;

    @Override
    public void onScriptPaint(Graphics g) {
        NPC nightmare = NPCs.closest("Phosani's Nightmare");
        if (nightmare != null) Arrays.stream(PVMUtil.attackableTiles(nightmare, 4))
                .forEach(t -> g.drawPolygon(t.getPolygon()));

        g.setColor(Color.BLACK);
        List<GraphicsObject> blackHoles = GraphicsObjects.all(x -> x.getId() == PhosaniBranch.NIGHTMARE_SHADOW_GRAPHIC_OBJ);
        blackHoles.forEach(x -> g.drawPolygon(x.getTile().getPolygon()));

        // draw last safe corner area
//        g.setColor(Color.GREEN);
//        if (lastCornerArea != null)
//            Arrays.stream(lastCornerArea.getTiles()).forEach(x -> g.drawPolygon(x.getPolygon()));
//
//        g.setColor(Color.BLUE);
//        if (nightmare != null) Arrays.stream(new Area(nightmare.getTrueTile(), nightmare.getTrueTile().translate(4, 4))
//                        .getTiles())
//                .forEach(x -> g.drawPolygon(x.getPolygon()));


        g.setColor(Color.WHITE);
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
//        if (.SPINDEL_CHASM.contains(Players.getLocal())) return;
        ;
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info("item changed");
        if (Bank.isOpen()) return;
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
}

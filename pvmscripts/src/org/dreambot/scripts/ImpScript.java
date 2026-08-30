package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.settings.Varcs;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.TotalLevel;
import org.dreambot.behaviour.method.KillImp;
import org.dreambot.behaviour.misc.MixedCombat;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.*;
import org.dreambot.behaviour.quests.doricsquest.DoricsQuest;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.quests.runemysteries.RuneMysteries;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.ImpSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class ImpScript extends PseudoScript implements PaintInfo, HumanMouseListener, ChatListener, ItemContainerListener, SpawnListener {
    Timer runtime = new Timer();
    FractalRoot<ImpSettings> tree = new FractalRoot<>(new ImpSettings(), getScriptName());

    public static int deathCount = 0;
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");
    final static int PLAY_TIME_VARCINT = 526;

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    @Override
    public void onStart(String... params) {
        super.onStart(params);
        init();
    }

    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();



    public void init() {
        tree.setSimpleName("cCImpsFarm")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),
                        new AutoProggy().setSimpleName("Auto proggy"),
                        new AntibanFractal().setSimpleName("Antiban"),
                        new LampHandler().setSimpleName("lamp handler"),
//                        new TotalLevel(() -> Skills.getTotalLevel() < 100 && tree.getSettings().skillsBeforeCombat)
//                                .setSimpleName("Get 100 total"),
                        new MixedCombat(() -> Combat.getCombatLevel() < 29
                                || Skills.getRealLevel(Skill.HITPOINTS) < 17)
                                .setSimpleName("Melee training for quests"),
                        new ShuffleFractal(() -> Quests.getQuestPoints() < 10).setSimpleName("Questin")
                                .addChildren(
                                        new CooksAssistant().setSimpleName("Cooks assistant"), // 1
                                        new RomeoAndJulietBranch().setSimpleName("Romeo and juliet"), // 5
                                        new ImpCatcher().setSimpleName("Imp catcher"), // 1
                                        new DoricsQuest().setSimpleName("Dorics quest"), // 1
                                        new RuneMysteries().setSimpleName("Rune mysteries"), // 1
                                        new GoblinDiplomacy().setSimpleName("Goblin diplomacy"), // 5
                                        new ErnestTheChicken().setSimpleName("Ernest the chicken"),// 4
                                        new VampyreSlayer().setSimpleName("Vampyre Slayer"), // 3
                                        new SheepShearer().setSimpleName("Sheep shearer"), // 1
                                        new RestlessGhost().setSimpleName("Restless Ghost") // 1
                                ),
                        new TotalLevel(() -> Skills.getTotalLevel() < 100).setSimpleName("Get 100 total"),
                        new MuleOff().setSimpleName("Mule Off"),
                        new KillImp(() -> true).setSimpleName("Kill imps")
                );
//        new AIAntiban();

        // ernest the chicken webnode
        WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));
    }


    public static boolean hasLootInBag;
    private long loopSpd;
    private long lastTimestamp;
    private final Timer playTimer = new Timer(60 * 60 * 1000 * 20);
    private final Timer checkPlayTime = new Timer(30 * 60 * 1000);
    private boolean checkedPlayTime;

    @Override
    public int onLoop() {
        if (isUnrestricted() && !OwnedItems.containsAny(MuleOff.LOOT) && tree.getSettings().stage) {
            Logger.info("Stopping script - finish on unrestricted mode enabled and you have muled off.");
            ScriptManager.getScriptManager().stop();
        }

        loopSpd = System.currentTimeMillis() - lastTimestamp;
        lastTimestamp = System.currentTimeMillis();
        if (ClientSettings.isAcceptAidEnabled()) {
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
        }

        if (checkPlayTime.finished() && MyVarps.getTutVarp() >= 1000) {
            if (Tabs.open(Tab.QUEST)) {
                checkPlayTime.reset();
                WidgetChild wc = Widgets.get(629, x -> x.hasAction("Character Summary"));
                if (wc != null && wc.interact()) {
                    return ReactionGenerator.getLong();
                }
            }
        }

        if (!checkedPlayTime && MyVarps.getTutVarp() >= 1000) {
            Logger.info("Checking play time");
            if (Tabs.open(Tab.QUEST)) {
                int playedMins = Varcs.getInt(PLAY_TIME_VARCINT);
                if (playedMins > 1) {
                    playTimer.setRunTime((20 * 60 * 60 * 1000) - (long) playedMins * 60 * 1000);
                    checkedPlayTime = true;
                } else {
                    WidgetChild wc = Widgets.get(629, x -> x.hasAction("Character Summary"));
                    if (wc != null && wc.interact()) {
                        return ReactionGenerator.getLong();
                    }
                }
            }
        }

        Player attackingMe = Players.closest(x -> x.isSkulled() && x.isInteracting(Players.getLocal()));
        if (Combat.isInWild() && Players.getLocal().isInCombat() && attackingMe != null) {
            Logger.info("Being attack by " + attackingMe.getName());
            Logger.info("Level: " + attackingMe.getLevel());
            attackingMe.getEquipment().forEach(x -> Logger.info("Equipment " + x.getID() + " " + x.getName()));
        }

        if (MyVarps.getTutVarp() >= 1000) {
            if (ClientSettings.isWorldHopConfirmationEnabled()) {
                if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
                Logger.info("Disabling hop confirmations");
                ClientSettings.toggleWorldHopConfirmation(false);
                return ReactionGenerator.getQuick();
            }

            if (ClientSettings.isWorldHopConfirmationEnabled()) {
                if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
                ClientSettings.toggleWorldHopConfirmation(false);
                return ReactionGenerator.getNormal();
            }

//
            if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 30) {
                Walking.toggleRun();
            }

            if (ClientSettings.isSellPriceWarningEnabled()) {
                if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
                ClientSettings.toggleSellPriceWarning(false);
                return ReactionGenerator.getNormal();
            }

            if (ClientSettings.isBuyPriceWarningEnabled()) {
                if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
                ClientSettings.toggleBuyPriceWarning(false);
                return ReactionGenerator.getNormal();
            }
        }
        return tree.run();
    }

    @Override
    public void onExit() {
        AnalyticsReporter.stop();
    }

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
                "cCImps: " + runtime.formatTime(),
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "Time Until Mule off: " + muleOff,
                String.format("Earned %s (%s / hr))", df.format(grossGp), df.format(runtime.getHourlyRate(grossGp))),
                "Remaining Trade Unrestrict time: " + (20 - (Varcs.getInt(PLAY_TIME_VARCINT) / 60)),
                "Played mins: " + Varcs.getInt(PLAY_TIME_VARCINT),
                "Unrestricted: " + isUnrestricted()

        };
    }

    @Override
    public String getScriptName() {
        return "impFarm";
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

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            deathCount++;
        }
    }

    public static boolean isUnrestricted() {
        return Skills.getTotalLevel() >= 100 && Quests.getQuestPoints() >= 10 && Varcs.getInt(PLAY_TIME_VARCINT) >= 1200;
    }

    @Override
    public void onInventoryItemAdded(Item item) {
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
    }
}

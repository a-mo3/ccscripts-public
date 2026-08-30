package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
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
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
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
import org.dreambot.behaviour.KillImp;
import org.dreambot.behaviour.MixedCombat;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.quests.*;
import org.dreambot.behaviour.quests.doricsquest.DoricsQuest;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.quests.runemysteries.RuneMysteries;
import org.dreambot.behaviour.training.TotalLevel;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.BondSettings;
import org.dreambot.settings.SettingsLoader;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.script.SettingsData;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettings;
import org.dreambot.settings.ui.Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCImpFarm", author = "camalCase", version = 0.0)
public class ImpFarm extends AbstractScript implements PaintInfo, HumanMouseListener, ChatListener, ItemContainerListener, SpawnListener {
    Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    WebhookListener webhookListener = new WebhookListener();
    public static int deathCount = 0;
    boolean shouldHop = false;
    public static final Area MID_DRAGON_AREA = new Area(3197, 3839, 3214, 3830);
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

    @Override
    public boolean onSolverStart(RandomSolver solver) {
        if (solver instanceof BreakSolver) isBreaking.set(true);
        return super.onSolverStart(solver);
    }

    @Override
    public void onSolverEnd(RandomSolver solver) {
        if (solver instanceof BreakSolver) isBreaking.set(false);
        super.onSolverEnd(solver);
    }

    private void init() {
        SettingsLoader<SettingsData> settingsLoader = new SettingsLoader<>(SettingsData.class);
        SettingsData settings = settingsLoader.loadFile("settings.json", ScriptSettings.getSettingsData());
        SettingsLoader<BondSettings> bondLoader = new SettingsLoader<>(BondSettings.class);
        bondLoader.loadFile("bondSettings.json", new BondSettings());
        ScriptSettings.setSettingsData(settings);
        new WebhookListener();

        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));

        tree.setSimpleName("cCImpsFarm")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),
                        new LampHandler().setSimpleName("lamp handler"),
                        new TotalLevel(() -> Skills.getTotalLevel() < 100 && ScriptSettings.getSettingsData().skillsBeforeCombat)
                                .setSimpleName("Get 100 total"),
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
        if (isUnrestricted() && !OwnedItems.containsAny(MuleOff.LOOT) && ScriptSettings.getSettingsData().stage) {
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
        Client.getInstance().removeEventListener(webhookListener);
        AnalyticsReporter.stop();
    }

    Tile safe = new Tile(2970, 3604);

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (Gui.wasDiscordButtonClicked(e.getPoint())) {
            try {
                Desktop.getDesktop().browse(new URI(""));
            } catch (IOException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (Gui.wasButtonClicked(e.getPoint())) {
            SwingUtilities.invokeLater(Gui::new);
        }
    }

    @Override
    public void onPaint(Graphics graphics) {
        Alerts.renderList(graphics);
        Gui.paintDiscordButton(graphics);
        Gui.paintButton(graphics);
        scriptPaint.paint(graphics);
        fractalPaint.paint(graphics);

//        if (SafespotReds.lootTile != null) {
//            graphics.drawPolygon(SafespotReds.lootTile.getPolygon());
//        }

//
//        List<NPC> dragons = NPCs.all(x -> x.getName().equals("Red dragon"));
//        for (NPC drag : dragons) {
//            if (drag.canAttack() && drag.canReach()) {
//                graphics.drawPolygon(drag.getTile().getPolygon());
//            }

//            Point p = drag.getClickablePoint();
//            graphics.drawString(String.valueOf(drag.getRealID()),
//                    p.x,
//                    p.y
//            );
//        }

//        Character interacting = Players.getLocal().getInteractingCharacter();
//        if (interacting != null) {
//            graphics.drawString(String.valueOf(interacting.getAnimation()),
//                    200,
//                    200
//            );
//        }
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
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "Time Until Mule off: " + muleOff,
                String.format("Earned %s (%s / hr))", df.format(grossGp), df.format(runtime.getHourlyRate(grossGp))),
                "Remaining Trade Unrestrict time: " + (20 - (Varcs.getInt(PLAY_TIME_VARCINT) / 60)),
                "Played mins: " + Varcs.getInt(PLAY_TIME_VARCINT),
                "Unrestricted: " + isUnrestricted()

        };
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

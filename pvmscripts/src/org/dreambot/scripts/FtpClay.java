package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.GameObjects;
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
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.TotalLevel;
import org.dreambot.behaviour.misc.MixedCombat;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.*;
import org.dreambot.behaviour.quests.doricsquest.DoricsQuest;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.quests.runemysteries.RuneMysteries;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.mining.GenericMineLeaf;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.ClaySettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;

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

public class FtpClay extends PseudoScript implements ItemContainerListener {
    FractalRoot<ClaySettings> tree = new FractalRoot<>(new ClaySettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    final static int PLAY_TIME_VARCINT = 526;
    private long lastTimestamp;
    private final Timer playTimer = new Timer(60 * 60 * 1000 * 20);
    private final Timer checkPlayTime = new Timer(30 * 60 * 1000);
    private boolean checkedPlayTime;

    @Override
    public void onStart(String... args) {
        init();
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);

        WithdrawLoadoutEvent.sellList = new int[]{
                ItemID.CLAY,
        };

//        MuleRequestEvent.moneyMakeMode = FtpClaySettings.getData().noMuleMode;

        MuleOff.LOOT = new int[]{
                ItemID.CLAY
        };

        Area[] clayAreas = new Area[]{
                new Area(3179, 3378, 3184, 3369),
                new Area(2984, 3240, 2988, 3238)
        };

        if (tree.getSettings().allAreas) {
            clayAreas = new Area[]{
                    // edge
                    new Area(3179, 3378, 3184, 3369),
                    new Area(2984, 3240, 2988, 3238),
                    // rimmington
                    new Area(2985, 3240, 2988, 3238),
                    // dwarven mine, east
                    new Area(3052, 9819, 3054, 9817),
                    // dwarven mine, west
                    new Area(3027, 9811, 3031, 9807)
            };
        }

        if (tree.getSettings().craftingGuild) {
            // crafting guild mine
            clayAreas = new Area[]{
                    new Area(
                            new Tile(2939, 3291, 0),
                            new Tile(2944, 3291, 0),
                            new Tile(2944, 3277, 0),
                            new Tile(2935, 3280, 0))
            };

        }

        Area clayArea = clayAreas[ShuffleFractal.getLoginValue() % clayAreas.length];

        Tile clayTile = new Tile[]{
                new Tile(3183, 3377, 0),
                new Tile(3180, 3372, 0),
                new Tile(3179, 3371, 0),
                new Tile(3054, 9819, 0),
                new Tile(3053, 9818, 0),
                new Tile(3028, 9808, 0),
                new Tile(3030, 9809, 0),
                new Tile(3030, 9810, 0)
        }[Calculations.random(7)];


        tree.setSimpleName("cCClayFarm");
        tree.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new TutorialTree().setSimpleName("Tutorial island"),
                new LampHandler().setSimpleName("lamp handler"),
//                new GetMoneyBranch(() -> FtpClaySettings.getData().noMuleMode).setSimpleName("Get money"),

                new CraftingBranch(() -> tree.getSettings().craftingGuild && Skills.getRealLevel(Skill.CRAFTING) < 40)
                        .setSimpleName("Crafting to 40"),

                new MixedCombat(() -> (Combat.getCombatLevel() < 20 || Skills.getRealLevel(Skill.HITPOINTS) < 25)
                        && Quests.getQuestPoints() < 10)
                        .setSimpleName("Melee training for quests"),
                new ShuffleFractal(() -> Quests.getQuestPoints() < 10)
                        .setSimpleName("Questin")
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

                // play timer should be the only thing that matters here, we'd have 10qp and 100 ttl from above
                new Fractal(() -> (MuleOff.timer == null || MuleOff.timer.finished()) && playTimer.finished())
                        .addChildren(
                                new MuleOff().setSimpleName("Mule Off")
                        ),

                new GenericMineLeaf(() -> tree.getSettings().singleRock,
                        () -> GameObjects.closest(x -> x.getName().equals("Clay rocks") && x.getTile().equals(clayTile)),
                        clayTile.getArea(2))
                        .setShouldBank(true)
                        .setSimpleName("Mine clay (Single rock)"),

                new GenericMineLeaf(() -> true, "Clay rocks", clayArea)
                        .setShouldBank(true)
                        .setHopCondition(() -> Players.all(x -> x.distance() < 4).size() > tree.getSettings().compLimit)
                        .setSimpleName("Mine clay")
        );
//        new AIAntiban();
    }

    @Override
    public int onLoop() {
        ClaySettings settings = tree.getSettings();
        if (isUnrestricted() && !OwnedItems.containsAny(MuleOff.LOOT) && settings.stage
                && OwnedItems.count(ItemID.COINS_995) <= 50_000) {
            Logger.info("Stopping script - finish on unrestricted mode enabled and you have muled off.");
            ScriptManager.getScriptManager().stop();
        }

        if (settings.ignorePlaytime && Skills.getTotalLevel() >= 100 && Quests.getQuestPoints() >= 10) {
            Logger.info("Stopping script - ignore time on.");
            ScriptManager.getScriptManager().stop();
        }

        if (ClientSettings.isAcceptAidEnabled() && MyVarps.getTutVarp() >= 1000) {
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
        }

        if (checkPlayTime.finished() && MyVarps.getTutVarp() >= 1000) {
            if (Tabs.open(Tab.QUEST)) {
                checkPlayTime.reset();
                WidgetChild wc = Widgets.get(629, x -> x.hasAction("Character summary"));
                if (wc != null && wc.interact()) {
                    Logger.info("Check play");
                    return ReactionGenerator.getLong();
                }
            }
        }

        if (!checkedPlayTime && MyVarps.getTutVarp() >= 1000) {
            Logger.info("Checking play time");
            if (Tabs.open(Tab.QUEST)) {
                int playedMins = Varcs.getInt(PLAY_TIME_VARCINT);
                if (playedMins > 1) {
                    Logger.info("Setting playtime mins " + playedMins);
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
                Logger.info("Disabling hop confirmations");
                if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
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

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "cCClayFarm: " + runtime.formatTime(),
                "Time Until Mule off: " + muleOff,
                "Play timer " + playTimer.elapsed() + " " + playTimer.finished() + " " + playTimer.formatRemainingTime() + " " + playTimer.remaining(),
                String.format("Earned %s (%s / hr))", df.format(grossGp), df.format(runtime.getHourlyRate(grossGp))),
                "target: " + target,
        };
    }

    @Override
    public String getScriptName() {
        return "cCClayFarm";
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

    public static boolean isUnrestricted() {
        return Skills.getTotalLevel() >= 100 && Quests.getQuestPoints() >= 10 && Varcs.getInt(PLAY_TIME_VARCINT) >= 1200;
    }

    @Override
    public void onInventoryItemAdded(Item item) {
        if (Bank.isOpen()) return;
        if (item.getId() == ItemID.CLAY) grossGp += item.getLivePrice();
    }
}

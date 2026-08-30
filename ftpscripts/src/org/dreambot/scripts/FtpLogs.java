package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
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
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.MixedCombat;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.quests.*;
import org.dreambot.behaviour.quests.doricsquest.DoricsQuest;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.quests.runemysteries.RuneMysteries;
import org.dreambot.behaviour.training.TotalLevel;
import org.dreambot.behaviour.training.woodcutting.AllOfTheLogs;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.getmoney.GetMoneyBranch;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.scriptdata.FtpLogSettings;
import org.dreambot.settings.SettingsLoader;
import org.dreambot.settings.timing.ReactionGenerator;
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

public class FtpLogs extends PseudoScript implements ItemContainerListener {
    Fractal tree = new Fractal();
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    final static int PLAY_TIME_VARCINT = 526;
    private long lastTimestamp;
    private final Timer playTimer = new Timer(60 * 60 * 1000 * 20);
    private final Timer checkPlayTime = new Timer(30 * 60 * 1000);
    private boolean checkedPlayTime;

    @Override
    public void init() {
        SettingsLoader.changePath("cCLogFarm");
        Client.getInstance().addEventListener(this);


        MuleRequestEvent.moneyMakeMode = FtpLogSettings.getData().noMuleMode;

        WithdrawLoadoutEvent.sellList = new int[]{
                ItemID.AMULET_OF_GLORY
        };
        tree.setSimpleName("cCLogFarm");
        tree.addChildren(
                new TutorialTree().setSimpleName("Tutorial island"),
                new LampHandler().setSimpleName("lamp handler"),
                new GetMoneyBranch(() -> FtpLogSettings.getData().noMuleMode).setSimpleName("Get money"),
                new MixedCombat(() -> (Combat.getCombatLevel() < 20 || Skills.getRealLevel(Skill.HITPOINTS) < 25)
                        && Quests.getQuestPoints() < 10)
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
                new MuleOff(FtpLogSettings.getData().hoursUntilMuleOff, FtpLogSettings.getData().moneyLeftAfterMuling).setSimpleName("Mule Off"),
                new AllOfTheLogs(() -> true).setSimpleName("All da logs")
        );
//        new AIAntiban();
    }

    @Override
    public int onLoop() {
        if (isUnrestricted() && !OwnedItems.containsAny(MuleOff.LOOT) && FtpLogSettings.getData().stage
                && OwnedItems.count(ItemID.COINS_995) <= FtpLogSettings.getData().moneyLeftAfterMuling) {
            Logger.info("Stopping script - finish on unrestricted mode enabled and you have muled off.");
            ScriptManager.getScriptManager().stop();
        }

        if (FtpLogSettings.getData().ignorePlaytime && Skills.getTotalLevel() >= 100 && Quests.getQuestPoints() >= 10) {
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
                    Logger.info("A");
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
                "cCF2PLogFarm: " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "Time Until Mule off: " + muleOff,
                String.format("Earned %s (%s / hr))", df.format(grossGp), df.format(runtime.getHourlyRate(grossGp))),
                "target: " + target,
                String.format("GP target: %d /%d", OwnedItems.value(ItemID.POTATO) + OwnedItems.count(ItemID.COINS_995),
                        GetMoneyBranch.gpTarget)
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

    public static boolean isUnrestricted() {
        return Skills.getTotalLevel() >= 100 && Quests.getQuestPoints() >= 10 && Varcs.getInt(PLAY_TIME_VARCINT) >= 1200;
    }

    public void onInventoryItemAdded(Item item) {
        Logger.info("item added");
        if (Bank.isOpen()) return;
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info("item changed");
        if (Bank.isOpen()) return;
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
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

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
            SwingUtilities.invokeLater(() -> new Gui(FtpLogSettings.getData()));
        }
    }
}

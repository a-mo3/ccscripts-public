package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.behaviour.FletchLogs;
import org.dreambot.behaviour.HeadlessArrows;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.StringBows;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.generic.GetMembership;
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

@ScriptManifest(category = Category.MONEYMAKING, name = "cCFletcher", author = "camalCase", version = 0.0)
public class FletchFarm extends AbstractScript implements PaintInfo, HumanMouseListener {
    Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    public static final Area SHRIMP_AREA = new Area(3240, 3159, 3246, 3141);
    public static final Area BARB_VILLAGE_FISHING = new Area(3101, 3422, 3111, 3435);
    WebhookListener webhookListener = new WebhookListener();

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    @Override
    public void onStart(String... params) {
        super.onStart(params);
        init();
        for (String p : params) {
            if (p.contains("makeMagics")) {
                ScriptSettings.getSettingsData().setStringMagics(true);
            }
            if (p.contains("fromLogs")) {
                ScriptSettings.getSettingsData().setFletchFromLogs(true);
            }
        }
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
//        new AIAntiban();

        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings().setLongHigh(939)));
        tree.setSimpleName("cCFletcher")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                        new MuleOff().setSimpleName("Muling"),
                        new HeadlessArrows(() -> Skills.getRealLevel(Skill.FLETCHING) < 20)
                                .setSimpleName("Make headless arrows"),
                        new FletchLogs(ItemID.OAK_LOGS, ItemID.OAK_SHORTBOW_U, 200, 25),
                        new StringBows(() -> Skills.getRealLevel(Skill.FLETCHING) < 25, ItemID.OAK_SHORTBOW_U, 600, ItemID.OAK_SHORTBOW)
                                .setSimpleName("Oak shortbow"),
                        new FletchLogs(ItemID.OAK_LOGS, ItemID.OAK_LONGBOW_U, 700, 40)
                                .setSimpleName("Fletch oaks"),
                        new StringBows(() -> Skills.getRealLevel(Skill.FLETCHING) < 40, ItemID.OAK_LONGBOW_U, 700, ItemID.OAK_LONGBOW)
                                .setSimpleName("Oak longbow"),
                        new FletchLogs(ItemID.WILLOW_LOGS, ItemID.WILLOW_LONGBOW_U, 200, 55)
                                .setSimpleName("Fletch logs"),
                        new StringBows(() -> Skills.getRealLevel(Skill.FLETCHING) < 55, ItemID.WILLOW_LONGBOW_U, 200, ItemID.WILLOW_LONGBOW)
                                .setSimpleName("Willow longbow"),
                        new FletchLogs(ItemID.MAPLE_LOGS, ItemID.MAPLE_LONGBOW_U, 2600, 70),
                        new StringBows(() -> Skills.getRealLevel(Skill.FLETCHING) < 70, ItemID.MAPLE_LONGBOW_U, 2600, ItemID.MAPLE_LONGBOW)
                                .setSimpleName("Maple longbow"),
                        new FletchLogs(ItemID.MAGIC_LOGS, ItemID.MAGIC_LONGBOW_U, 2600, 100, true)
                                .setSimpleName("Fletch magics"),
                        new StringBows(() -> Skills.getRealLevel(Skill.FLETCHING) >= 85 && ScriptSettings.makeMagics(),
                                ItemID.MAGIC_LONGBOW_U, 2600, ItemID.MAGIC_LONGBOW)
                                .setSimpleName("Magic longbow"),
                        new FletchLogs(ItemID.YEW_LOGS, ItemID.YEW_LONGBOW_U, 2600, 100)
                                .setSimpleName("Fletch yews"),
                        new StringBows(() -> true, ItemID.YEW_LONGBOW_U, 2600, ItemID.YEW_LONGBOW)
                                .setSimpleName("Yew longbow")
                );
    }


    @Override
    public int onLoop() {
        if (Equipment.contains(MuleOff.itemsToMule)) {
            if (Inventory.isFull()) new BankAllInventoryEvent().execute();

            Equipment.unequip(x -> Arrays.stream(MuleOff.itemsToMule).anyMatch(i -> i == x.getID()));
        }

        if (Bank.getLastBankHistoryCacheTime() <= 1) {
            Logger.info("Get bank cache");
            if (Bank.isOpen()) Bank.close();
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getLong();
        }

        if (ScriptSettings.forceWorld() > 0 && Worlds.getCurrentWorld() != ScriptSettings.forceWorld() && Client.isMembers()) {
            Logger.info("Hopping worlds");
            if (Bank.isOpen()) Bank.close();
            WorldHopper.hopWorld(ScriptSettings.forceWorld());
            Sleep.sleepUntil(() -> Worlds.getCurrentWorld() == ScriptSettings.forceWorld(), 30_000);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isAcceptAidEnabled()) {
            Logger.info("Toggling off accept aid");
            if (Bank.isOpen()) Bank.close();
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
        }

        if (!Client.isLoggedIn()) {
            Logger.info("Waiting for login.");
            return ReactionGenerator.getNormal();
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
        return tree.run();
    }

    @Override
    public void onExit() {
        Client.getInstance().removeEventListener(webhookListener);
        AnalyticsReporter.stop();
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
    }

    final DecimalFormat df = new DecimalFormat("###,###,###");

    @Override
    public String[] getPaintInfo() {
        int coinCount = 0;
        String muleOff = "-";
        if (MuleOff.timer != null) muleOff = formatTime(MuleOff.timer.remaining());
        long lastCacheTime = Bank.getLastBankHistoryCacheTime();
        if (lastCacheTime > 1) coinCount = OwnedItems.count(ItemID.COINS_995);

        return new String[]{
                "cCFletcher " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "Fletching lvl: " + Skills.getRealLevel(Skill.FLETCHING),
                "String magics? " + ScriptSettings.makeMagics(),
                String.format("Bank cache status %d %d", Bank.getBankHistoryCache().size(), lastCacheTime),
                "Mule off time: " + muleOff
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
}

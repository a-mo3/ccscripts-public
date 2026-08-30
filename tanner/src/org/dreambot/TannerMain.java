package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
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
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.Tanning;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.generic.GetMembership;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCTannerFarm", author = "camalCase", version = 0)
public class TannerMain extends AbstractScript implements PaintInfo, ItemContainerListener, HumanMouseListener {
    static Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    WebhookListener webhookListener = new WebhookListener();

    boolean shouldHop = false;

    @Override
    public void onStart() {
        init();
    }

    List<String> validColors = Arrays.asList("green", "blue", "black", "red");

    @Override
    public void onStart(String... params) {
        init(params);

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

    private void init(String... args) {
        SettingsLoader<SettingsData> settingsLoader = new SettingsLoader<>(SettingsData.class);
        SettingsData settings = settingsLoader.loadFile("settings.json", ScriptSettings.getSettingsData());
        SettingsLoader<BondSettings> bondLoader = new SettingsLoader<>(BondSettings.class);
        bondLoader.loadFile("bondSettings.json", new BondSettings());
        ScriptSettings.setSettingsData(settings);
        new WebhookListener();
        
        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));
        Walking.setRunThreshold(20);

        if (args != null) {
            for (String s : args) {
                if (s == null || s.isEmpty()) continue;
                if (validColors.contains(s.toLowerCase())) ScriptSettings.getSettingsData().hideColor = s.toLowerCase();
            }
        }
        tree.setSimpleName("cCTanner")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),
                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                        new MuleOff().setSimpleName("MuleOff"),
                        new Tanning().setSimpleName("Tanning")
                );
//        new AIAntiban();
    }


    public static boolean hasLootInBag;
    boolean hopAfterMuling;

    @Override
    public int onLoop() {
        if (MuleOff.timer != null) {
            if (MuleOff.timer.finished()) hopAfterMuling = true;
            if (hopAfterMuling && !MuleOff.timer.finished()) {
                WorldHopper.hopWorld(
                        Worlds.getRandomWorld(x -> x.isNormal() && x.isMembers() == Client.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel())
                );
                hopAfterMuling = false;
            }
        }

        if (ClientSettings.isAcceptAidEnabled()) {
            ClientSettings.toggleAcceptAid(false);
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

    @Override
    public String[] getPaintInfo() {
        String muleOff = "-";
        if (MuleOff.timer != null) muleOff = formatTime(MuleOff.timer.remaining());
        return new String[]{
                "cCTannerFarm " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy),
                "Mule timer " + (MuleOff.timer == null ? "-" : formatTime(MuleOff.timer.remaining())),
                "Hide " + ScriptSettings.getSettingsData().hideColor
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

package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.behaviour.LampHandler;
import org.dreambot.behaviour.Minnows;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.fishing.FishingFractal;
import org.dreambot.behaviour.fishingcontest.FishingContest;
import org.dreambot.behaviour.trawler.Trawler;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.data.ItemID;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.generic.GetMembership;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.scout.Panopticon;
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
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCMinnowsAIOFARM", author = "camalCase", version = 0.0)
public class MinnowsFarm extends AbstractScript implements PaintInfo, HumanMouseListener {
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
            if (p.contains("stopAt82")) {
                ScriptSettings.getSettingsData().setStopAfterFishing(true);
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
        new WebhookListener();


        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));
        tree.setSimpleName("cCMinnows")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new LampHandler().setSimpleName("Lamp handler"),
                        new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) < 20,
                                SHRIMP_AREA, () -> NPCs.closest(n -> n.hasAction("Net") && SHRIMP_AREA.contains(n)))
                                .setShouldBank(false)
                                .setInteraction("Net")
                                .setSimpleName("Shrimp until lvl 20")
                                .setInventoryLoadout(
                                        new InventoryLoadout()
                                                .strictIgnore(ItemID.RAW_SHRIMPS, ItemID.RAW_ANCHOVIES)
                                                .addItem(FishingFractal.SMALL_FISHING_NET, 1)
                                                .setStrict(true)
                                ),
                        new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) < 82,
                                BARB_VILLAGE_FISHING, () -> NPCs.closest(n -> n.hasAction("Lure") && BARB_VILLAGE_FISHING.contains(n)))
                                .setShouldBank(false)
                                .setInteraction("Lure")
                                .setSimpleName("Salmon/Trout until 82")
                                .setInventoryLoadout(new InventoryLoadout()
                                        .setStrict(true)
                                        .strictIgnore(ItemID.RAW_SALMON, ItemID.RAW_TROUT)
                                        .addItem(FishingFractal.FLY_FISHING_ROD, 1)
                                        .addItem(FishingFractal.FEATHER, 1, 5000)
                                        .setMuleRequestAmount(30_000)
                                ),
                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Get membership")
                                .setAppendLogic(() -> {
                                            if (ScriptSettings.stopAfterFishing()) {
                                                System.exit(0);
                                                return true;
                                            }
                                            return false;
                                        }
                                ),
                        new FishingContest().setSimpleName("Fishing contest"),
                        new Trawler().setSimpleName("Trawl"),
                        new MuleOff().setSimpleName("Mule"),
                        new Minnows()
                );
//        new AIAntiban();
    }


    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
//        if (!Client.isLoggedIn()) {
//            webhookListener.loginResponse(LoginUtility.login());
//        }

        if (ClientSettings.isAcceptAidEnabled()) {
            Logger.info("Toggling off accept aid");
            if (Bank.isOpen()) Bank.close();
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
        return new String[]{
                "cCMinnows " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "Fishing lvl: " + Skills.getRealLevel(Skill.FISHING),
        };
    }
}

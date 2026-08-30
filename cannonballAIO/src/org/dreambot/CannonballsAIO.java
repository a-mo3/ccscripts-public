package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.CannonBalls;
import org.dreambot.behaviour.ItemMuleOff;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.foundry.FoundryBranch;
import org.dreambot.behaviour.training.doricsquest.DoricsQuest;
import org.dreambot.behaviour.training.dwarfcannon.DwarfCannon;
import org.dreambot.behaviour.training.sleepinggiants.SleepingGiants;
import org.dreambot.behaviour.training.theknightssword.TheKnightsSword;
import org.dreambot.behaviour.training.training.TrainNode;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
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
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCCannonballAIO", author = "camalCase", version = 0.01)
public class CannonballsAIO extends AbstractScript implements PaintInfo, HumanMouseListener {
    Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
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
        ScriptSettings.setSettingsData(settings);
        SettingsLoader<BondSettings> bondLoader = new SettingsLoader<>(BondSettings.class);
        bondLoader.loadFile("bondSettings.json", new BondSettings());
        new WebhookListener();

        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));
        tree.setSimpleName("cCCannonballs")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                        new DoricsQuest().setSimpleName("Dorics Quest"),
                        new TheKnightsSword().setSimpleName("Knights Sword"),
                        new DwarfCannon().setSimpleName("Dwarf cannon"),
                        new TrainNode().setSimpleName("Train node"),
                        new Fractal(() -> Bank.isCached() && ScriptSettings.getSettingsData().getDoubleMould && !OwnedItems.contains(ItemID.DOUBLE_AMMO_MOULD)).addChildren(
                                new SleepingGiants().setSimpleName("Sleeping Giants"),
                                new FoundryBranch(() -> true).setSimpleName("Getting double mould")
                        ).setSimpleName("Double mould"),
                        // muleless cannonball restocking / sell cballs on account
                        ScriptSettings.getSettingsData().muleOffItems ? new ItemMuleOff().setSimpleName("Item Mule off") : new MuleOff().setSimpleName("Mule off"),
                        new CannonBalls(() -> true).setSimpleName("Make cballs")
                );
//        new AIAntiban();
    }


    Timer failSafeMouldInterface = new Timer(60 * 1000 * 2);

    @Override
    public int onLoop() {
        if (OwnedItems.contains(ItemID.DOUBLE_AMMO_MOULD) && ScriptSettings.getSettingsData().stopAfterFoundry) {
            Logger.info("You have stop after foundry enabled and you have a double c ball mould so we are stopping.");
            ScriptManager.getScriptManager().stop();
            return ReactionGenerator.getQuick();
        }


        WidgetChild screen = Widgets.get(718, 9);
        if (screen == null || !screen.isVisible()) {
            failSafeMouldInterface.reset();
        }

        if (failSafeMouldInterface.finished()) {
            failSafeMouldInterface.reset();
            Widgets.closeAll();
        }

        if (Inventory.contains("Coin pouch")) {
            Inventory.interact("Coin pouch", "Open");
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
        return new String[]{
                "cCCannonballs " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy),
                "Event " + AbstractResponseEvent.lastRan,
                "isMember: " + Client.isMembers()
        };
    }
}

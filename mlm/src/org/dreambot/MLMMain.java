package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.DestructableObstacle;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.method.EnterMLM;
import org.dreambot.behaviour.method.GetProspector;
import org.dreambot.behaviour.method.MLMMining;
import org.dreambot.behaviour.method.MLMTopFloor;
import org.dreambot.behaviour.training.MixedMining;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
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

@ScriptManifest(category = Category.MISC, name = "cCMLMFarm", author = "camalCase", version = 0.02)
public class MLMMain extends AbstractScript implements PaintInfo, HumanMouseListener {
    FluffeesPaint paint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_CHATBOX, new MLMPaint());
    Timer timer = new Timer();
    Fractal tree;
    WebhookListener webhookListener = new WebhookListener();
    DecimalFormat df = new DecimalFormat("###,###,###");

    @Override
    public void onStart(String... params) {
        initScript();
    }

    @Override
    public void onStart() {
        initScript();
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

    private void initScript() {
                // load script org.dreambot.settings
        SettingsLoader<SettingsData> settingsLoader = new SettingsLoader<>(SettingsData.class);
        SettingsLoader<BondSettings> bondLoader = new SettingsLoader<>(BondSettings.class);
        bondLoader.loadFile("bondSettings.json", new BondSettings());
        SettingsData settings = settingsLoader.loadFile("settings.json", ScriptSettings.getSettingsData());
        ScriptSettings.setSettingsData(settings);

        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));

//        new AIAntiban();
        LocalPathFinder.getLocalPathFinder().addObstacle(new DestructableObstacle("Rockfall", "Mine"));

        tree = new Fractal().setSimpleName("MotherlodeFarm")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new MixedMining(() -> Skills.getRealLevel(Skill.MINING) < ScriptSettings.getSettingsData().getMiningTarget()).setSimpleName("Mining training"),
                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Getting membership.")
                                .setAppendLogic(() -> {
                                    if (Inventory.contains(ItemID.PAYDIRT)) {
                                        Inventory.dropAll(ItemID.PAYDIRT);
                                        return true;
                                    }
                                    return false;
                                }),
                        new MuleOff().setSimpleName("Mule off")
                                .setAppendLogic(() -> {
                                    if (Inventory.contains(ItemID.PAYDIRT)) {
                                        Inventory.dropAll(ItemID.PAYDIRT);
                                        return true;
                                    }
                                    return false;
                                }),
                        new GetProspector().setSimpleName("Get prospector"),
                        new MLMTopFloor().setSimpleName("Top floor mining"),
                        new MLMMining().setSimpleName("Mining"),
                        new EnterMLM().setSimpleName("MLM")
                );
    }



    @Override
    public int onLoop() {
//        if (Bank.getLastBankHistoryCacheTime() <= 1) {
//            Logger.info("Get bank cache");
//            if (Bank.isOpen()) Bank.close();
//            if (Walking.shouldWalk()) Bank.open();
//            return ReactionGenerator.getLong();
//        }

        WidgetChild enterWildy = Widgets.get(x -> x.getParentID() == 475 && x.hasAction("Enter Wilderness"));
        if (enterWildy != null && enterWildy.isVisible()) {
            log("enter wildy widget");
            enterWildy.interact("Enter Wilderness");
            return 600;
        }

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
        if (EnterMLM.lastPath != null) {
            try {
                for (Tile t : EnterMLM.lastPath) {
                    boolean hasDoor = Arrays.stream(GameObjects.getObjectsOnTile(t)).anyMatch(x -> x.hasAction("Open"));
                    if (hasDoor) {
                        graphics.setColor(Color.YELLOW);
                    } else {
                        graphics.setColor(Color.white);
                    }
                    if (t != null) graphics.drawPolygon(t.getPolygon());
                }
            } catch (Exception ignored) {
            }
        }


        paint.paint(graphics);
        fractalPaint.paint(graphics);
    }

    @Override
    public void onExit() {
        Client.getInstance().removeEventListener(webhookListener);
    }

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                "cCMLM FARM - " + timer.formatTime(),
                Arrays.toString(FractalAPI.hierarchy),
                "Bank cached: " + (Bank.getLastBankHistoryCacheTime() > 1),
                "Owned GP: " + df.format(OwnedItems.count(995)),
                "Mule off timer: " + formatTime(MuleOff.timer == null ? 0 : MuleOff.timer.remaining()),
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

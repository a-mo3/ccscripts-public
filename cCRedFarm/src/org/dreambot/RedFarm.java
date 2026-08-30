package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.eaglespeak.EaglesPeak;
import org.dreambot.behaviour.method.BoxTrapState;
import org.dreambot.behaviour.method.NewRedChins;
import org.dreambot.behaviour.training.HunterBranch;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GetMembership;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.muling.Log;
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

@ScriptManifest(category = Category.MONEYMAKING, name = "cCRedFarm", author = "camalCase", version = 0.2)
public class RedFarm extends AbstractScript implements PaintInfo, HumanMouseListener {
    Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();
    WebhookListener webhookListener = new WebhookListener();
    DecimalFormat df = new DecimalFormat("###,###,###");

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
        tree.setSimpleName("cCRedFarm")
                .addChildren(
                        new TutorialTree().setSimpleName("Tutorial island"),

                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                        new EaglesPeak(() -> Skills.getRealLevel(Skill.HUNTER) >= 27 && !PaidQuest.EAGLES_PEAK.isFinished()).setSimpleName("Eagles Peak"),
                        new Fractal(() -> Skills.getRealLevel(Skill.HUNTER) < 63).setSimpleName("Training").addChildren(
                                new HunterBranch().setSimpleName("Hunter training")
                        ),
                        new MuleOff().setSimpleName("Mule"),
//                        new RedChinchompas(() -> true).setSimpleName("Red chins")
                        new NewRedChins(() -> true).setSimpleName("New red chins")
                );

//        new AIAntiban();

        WebFinder.getWebFinder().createAndAddNode(new Tile(2357, 3465, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2349, 3470, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2337, 3473, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2326, 3477, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2320, 3485, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2320, 3490, 0));

        Area eaglesPeakForest = new Area(
                new Tile(2314, 3489, 0),
                new Tile(2322, 3499, 0),
                new Tile(2332, 3505, 0),
                new Tile(2347, 3500, 0),
                new Tile(2353, 3490, 0),
                new Tile(2361, 3492, 0),
                new Tile(2357, 3505, 0),
                new Tile(2345, 3512, 0),
                new Tile(2331, 3513, 0),
                new Tile(2317, 3501, 0));

        LocalPathFinder l = LocalPathFinder.getLocalPathFinder();
        for (Tile t : eaglesPeakForest.getTiles()) {
            l.addBlacklistedTile(t);
        }
    }


    @Override
    public int onLoop() {
//        if (!Client.isLoggedIn()) {
//            webhookListener.loginResponse(LoginUtility.login());
//        }


        if (Combat.isAutoRetaliateOn()) {
            Log.info("Disable auto retaliate");
            if (Bank.isOpen()) Bank.close();
            Combat.toggleAutoRetaliate(false);
            return ReactionGenerator.getNormal();
        }


        Character femi = NPCs.closest("Femi");
        if (femi != null && femi.distance() < 5 && Dialogues.inDialogue()) {
            Dialog.solve("bit busy");
            return ReactionGenerator.getNormal();
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
    public void onExit() {
        Client.getInstance().removeEventListener(webhookListener);
        AnalyticsReporter.stop();
        tree.deregisterListeners();
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
        try {

            for (BoxTrapState s : NewRedChins.boxTrapStates) {
                graphics.setColor(s.isMine() ? Color.GREEN : Color.YELLOW);
                if (s.getOwner() == BoxTrapState.Owner.SOMEONE_ELSE) graphics.setColor(Color.RED);
                graphics.drawPolygon(s.getTile().getPolygon());
//                graphics.drawString(s.getTrap().getAnimationID() + " ", s.getTrap().getBoundingBox().x, s.getTrap().getBoundingBox().y);
            }
        } catch (Exception ignored) {
        }

        graphics.setColor(Color.white);
        scriptPaint.paint(graphics);
        fractalPaint.paint(graphics);
    }

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                "cCRedFarm " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
                "Hunter  lvl: " + Skills.getRealLevel(Skill.HUNTER),
                "Mule timer " + (MuleOff.timer == null ? "Still training" : formatTime(MuleOff.timer.remaining())),
                String.format("Chins caught %s (%s)",
                        df.format(NewRedChins.chinsCaught),
                        df.format(runtime.getHourlyRate(NewRedChins.chinsCaught))
                ),
                String.format("Money earnt %s (%s)",
                        df.format((long) NewRedChins.chinsCaught * LivePrices.get(ItemID.RED_CHINCHOMPA_10034)),
                        df.format(runtime.getHourlyRate(NewRedChins.chinsCaught * LivePrices.get(ItemID.RED_CHINCHOMPA_10034)))
                )
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

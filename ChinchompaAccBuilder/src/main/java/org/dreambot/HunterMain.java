package org.dreambot;

import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
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
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.behaviour.eaglespeak.EaglesPeak;
import org.dreambot.behaviour.eaglespeak.GoldPuzzleSolver;
import org.dreambot.behaviour.training.CatchBirdsFractal;
import org.dreambot.behaviour.training.HunterBranch;
import org.dreambot.behaviour.training.method.CopperLongtails;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.settings.SettingsLoader;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.script.SettingsData;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettings;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.HUNTING, name = "cCChinchompaAccountBuilder", author = "camalCase", version = 1.0)
public class HunterMain extends AbstractScript implements PaintInfo, HumanMouseListener, SpawnListener {
    FluffeesPaint paint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_RIGHT_CHATBOX, this);
    Timer runtime = new Timer();
    Fractal tree = new Fractal().addChildren(
            new EaglesPeak(() -> Skills.getRealLevel(Skill.HUNTER) >= 27 && !PaidQuest.EAGLES_PEAK.isFinished()).setSimpleName("Eagles Peak"),
            new HunterBranch().setSimpleName("Hunter training")
    ).setSimpleName("ChinBuilder");

    @Override
    public void onStart(String... params) {
        init();
    }

    @Override
    public void onStart() {
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

        new WebhookListener();
//        tree.registerListeners(); didnt work.
        // load script org.dreambot.settings
        SettingsLoader<SettingsData> settingsLoader = new SettingsLoader<>(SettingsData.class);
        SettingsData settings = settingsLoader.loadFile("chinBuilderSettings.json", ScriptSettings.getSettingsData());
        ScriptSettings.setSettingsData(settings);
        Logger.info(String.format("Burying bones: %b Training until 73: %b", ScriptSettings.shouldBuryBones(), ScriptSettings.trainTo73()));

        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));

        // todo add mouse and shit
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
    public void onExit() {
        tree.deregisterListeners();
    }

    @Override
    public int onLoop() {
        CatchBirdsFractal.buryBones();

        if (ClientSettings.isAcceptAidEnabled()) {
            Logger.info("Toggling off accept aid");
            if (Bank.isOpen()) Bank.close();
            ClientSettings.toggleAcceptAid(false);
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
    public void onPaint(Graphics2D g) {
        paint.paint(g);
    }

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                "cCChinchompaAccountBuilder: " + runtime.formatTime(),
                String.format("Bury bones: %b Train to 73: %b",
                        ScriptSettings.shouldBuryBones(),
                        ScriptSettings.trainTo73()),
                Arrays.toString(FractalAPI.hierarchy),
                String.format("Eagles peak: %b %d Gold: %d", PaidQuest.EAGLES_PEAK.isFinished(),
                        PaidQuest.EAGLES_PEAK.getConfigValue(),
                        PlayerSettings.getBitValue(GoldPuzzleSolver.PUZZLE_STATE_VARBIT)),
                "Hunter lvl: " + Skills.getRealLevel(Skill.HUNTER),
                "Snares: " + CopperLongtails.listSize
        };
    }

    @Override
    public void onGameObjectSpawn(GameObject object) {
        dispatchEvent(tree, object);
    }

    private static void dispatchEvent(Fractal f, GameObject object) {
        if (f instanceof SpawnListener) {
            Logger.info("Found a spawn listener, dispatching event");
            ((SpawnListener) f).onGameObjectSpawn(object);
        }
        for (Fractal fc : f.getChildren()) {
            dispatchEvent(fc, object);
        }
    }
}

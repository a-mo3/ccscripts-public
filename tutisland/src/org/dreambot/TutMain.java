package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.settings.ui.Gui;
import org.dreambot.util.MyVarps;
import org.dreambot.util.paint.FluffeesPaint;
import org.dreambot.util.paint.PaintInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MISC, name = "cCTutorial", author = "camalCase", version = 0.0)
public class TutMain extends AbstractScript implements PaintInfo, HumanMouseListener {
    FluffeesPaint paint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    Fractal tree = new Fractal().setSimpleName("Ausbot tutorial");

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


    Timer max_runtime = new Timer(60 * 1000 * 120);

    private void init() {
        
        tree.addChildren(
//                new Fractal(() -> max_runtime.finished()).setAppendLogic(() -> {
//                    System.exit(0);
//                    return false;
//                }),
                new TutorialTree().setSimpleName("Tutorial island"),
                new Fractal().setAppendLogic(() -> {
                    stop();
                    return false;
                })
        );
    }

    @Override
    public int onLoop() {
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
//        Gui.paintButton(graphics);
        paint.paint(graphics);
    }

    final int QUEST_POINT_VARP = 101;
    final int PLAY_TIME_VARCINT = 526;
    Timer runtime = new Timer();

    @Override
    public String[] getPaintInfo() {
        return new String[]{
//                ScriptStage.getScriptStage().getActiveLeaf(),
                Arrays.toString(FractalAPI.hierarchy),
                "Runtime " + runtime.formatTime(),
                String.valueOf(MyVarps.getTutVarp()),
        };
    }

    @Override
    public void onExit() {
        AnalyticsReporter.stop();
    }
}

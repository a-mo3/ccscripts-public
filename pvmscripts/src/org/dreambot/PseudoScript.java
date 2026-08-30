package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.settings.fractalsettings.FractalRoot;

import java.awt.*;

/**
 * idk if having multiple abstractscript classes would cause problems and im not about to find out
 */
public abstract class PseudoScript implements PaintInfo, HumanMouseListener {
    public void onStart(String... args) {
        onArgs(args);
        init();
    }

    boolean forceMenuManip = false;
    int forceMouseSpeed = 75; // should only be used if menu manip is forced

    /**
     * @param forceMouseSpeedLower lower random bound for mouse spd
     * @param forceMouseSpeedUpper upper random bound for mouse spd
     * @return
     */
    public PseudoScript forceMenuManip(int forceMouseSpeedLower, int forceMouseSpeedUpper) {
        forceMenuManip = true;
        this.forceMouseSpeed = Calculations.random(forceMouseSpeedLower, forceMouseSpeedUpper);
        return this;
    }

    protected CamelPaint camelPaint = null;

    public PseudoScript() {
        camelPaint = new CamelPaint(
                getScriptName(),
                this::getRuntime,
                this::getMoneyMade,
                this::getMuleOffTime,
                () -> {
                    FractalRoot r = new FractalRoot<>();
                    r.addChildren(
                            PvmMain.universalTasks,
                            getFractal()
                    );
                    Logger.info("Open gui");
                    r.makeGUI();
                    return null;
                } // todo replace to include script tree and universal tree
        );
    }

    public void onStart() {
        Logger.info("Init script");
        init();
        Logger.info("Init paint");
    }

    public void onArgs(String... args) {
    }

    public abstract void init();

    public abstract int onLoop();

    @Override
    public String[] getPaintInfo() {
        return new String[0];
    }


    // force every script to implement new paint
    public abstract String getScriptName();

    public abstract int getMoneyMade();

    public abstract Timer getRuntime();

    public abstract long getMuleOffTime();

    // previously getGUI, now returns the script tree we combine with universal tasks to parse for a gui
    public abstract Fractal getFractal();

    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_RIGHT_PLAY_SCREEN, new FractalAPI());

    public void onPaint(Graphics g) {
        Alerts.renderList(g);
        scriptPaint.paint(g);
        fractalPaint.paint(g);
        if (camelPaint != null) camelPaint.paint(g);
        try {
            onScriptPaint(g);
        } catch (Exception ignored) {
        }
    }

    public void onScriptPaint(Graphics g) {

    }

    public void onExit() {

    }
}

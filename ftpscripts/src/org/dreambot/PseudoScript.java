package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.settings.ui.Gui;

import java.awt.*;

/**
 * idk if having multiple abstractscript classes would cause problems and im not about to find out
 */
public abstract class PseudoScript implements PaintInfo, HumanMouseListener {
    public void onStart(String... args) {
        init();
    }

    public void onStart() {
        init();
    }

    public abstract void init();

    public abstract int onLoop();

    @Override
    public String[] getPaintInfo() {
        return new String[0];
    }

    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());

    public void onPaint(Graphics g) {
        Alerts.renderList(g);
        Gui.paintDiscordButton(g);
        Gui.paintButton(g);
        scriptPaint.paint(g);
        fractalPaint.paint(g);
    }


}

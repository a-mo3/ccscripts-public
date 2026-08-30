package org.dreambot.settings.fractalsettings.testing;

import org.dreambot.PseudoScript;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.fractalsettings.FractalRoot;

public class SettingsFractalTestScript extends PseudoScript {

    FractalRoot tree = new FractalRoot(new FTSettingsTwo(), "cCNewSettings");

    @Override
    public void init() {
        tree.addChildren(
                new ConfigurableHunterTest(),
                new SettingsFractalTest()
        );
    }

    @Override
    public int onLoop() {
        return tree.run();
    }

    @Override
    public String getScriptName() {
        return "Config Fractals";
    }

    @Override
    public int getMoneyMade() {
        return 0;
    }

    @Override
    public Timer getRuntime() {
        return null;
    }

    @Override
    public long getMuleOffTime() {
        return 0;
    }

    @Override
    public Fractal getFractal() {
        return tree;
    }
}

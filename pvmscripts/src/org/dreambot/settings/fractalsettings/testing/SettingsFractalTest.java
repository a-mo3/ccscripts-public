package org.dreambot.settings.fractalsettings.testing;

import org.dreambot.fractals.Fractal;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;
import org.dreambot.settings.fractalsettings.SettingsRepository;

public class SettingsFractalTest extends Fractal implements ConfigurableFractal<FTSettings> {
    @Override
    public FTSettings getSettings() {
        return SettingsRepository.getSetting(settingName(), new FTSettings());
    }

    @Override
    public String settingName() {
        return "NMZ";
    }
}

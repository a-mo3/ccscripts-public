package org.dreambot.settings.fractalsettings.testing;

import org.dreambot.fractals.Fractal;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;
import org.dreambot.settings.fractalsettings.SettingsRepository;

public class ConfigurableHunterTest extends Fractal implements ConfigurableFractal<FTSettingsTwo> {
    @Override
    public int onLoop() {
        log(String.valueOf(getSettings().minLootValue));
        return 1000;
    }

    @Override
    public FTSettingsTwo getSettings() {
        return SettingsRepository.getSetting(settingName(), new FTSettingsTwo());
    }

    @Override
    public String settingName() {
        return "Hunter";
    }
}

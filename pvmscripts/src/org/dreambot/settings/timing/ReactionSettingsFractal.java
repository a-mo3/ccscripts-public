package org.dreambot.settings.timing;

import org.dreambot.fractals.Fractal;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;
import org.dreambot.settings.fractalsettings.SettingsRepository;

/**
 * a fractal that has no behaviour but just uses the framework to let people adjust the settings
 */
public class ReactionSettingsFractal extends Fractal implements ConfigurableFractal<ReactionSettings> {
    public ReactionSettingsFractal() {
        ReactionGenerator.setReactionSettings(getSettings());
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public ReactionSettings getSettings() {
        return SettingsRepository.getSetting(settingName(), new ReactionSettings());
    }

    @Override
    public String settingName() {
        return "reactionTiming";
    }
}

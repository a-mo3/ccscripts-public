package org.dreambot.settings.fractalsettings;

/**
 * When a fractal that will be used in many scripts should have settings, eg
 * Hunter - do salamanders or falconry past X level
 * NMZ - use zapper power up
 * the fractal can implement this, provide its data file
 * then the script will save and load it under its directory and the trainings setting will be saved for that script
 * the gui should display these as tabs
 */
public interface ConfigurableFractal<T> {
    // the instance of the settings
    T getSettings();

    // used to save and load the file
    String settingName();
}

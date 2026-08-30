package org.dreambot.ui.mappings;


import org.dreambot.fractals.Fractal;
import org.dreambot.ui.components.DreamPanel;

/**
 * all UI component make a panel for their org.dreambot.settings
 * they serialize those org.dreambot.settings into a data class, this can be saved for using quickstart
 * they use the data class to make a fractal, add that to the AIOMain fractal and run da script
 *
 * @param <T> Type of the ui components data class, used for serialization
 */
public interface UIMapping<T> {
    DreamPanel makePanel();

    Fractal makeFractal(T data);

    // UI components need to generate a data class so they can be serialized
    // and the org.dreambot.settings can be launched again from quickstart
    T generateDataclass();
}

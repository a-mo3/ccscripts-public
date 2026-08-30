package org.dreambot.gui;

import org.dreambot.fractals.IronFractal;

/**
 * a fractal DTO is a class that has fields for the arguments a fractal requires in its constructor
 * for use in the configurable factory UIs
 */
public abstract class FractalDTO {
    public abstract FractalDTO getInstance();

    public abstract IronFractal toFractal();

    public abstract String name();


    @Override
    public String toString() {
        return name();
    }
}

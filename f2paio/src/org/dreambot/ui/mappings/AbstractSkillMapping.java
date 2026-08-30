package org.dreambot.ui.mappings;

import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.Fractal;
import org.dreambot.ui.components.DreamPanel;

public class AbstractSkillMapping<T> implements UIMapping<T> {
    int targetLevel = 0;

    @Override
    public DreamPanel makePanel() {
        return null;
    }

    @Override
    public Fractal makeFractal(T data) {
        return null;
    }

    @Override
    public T generateDataclass() {
        return null;
    }

    public void updateTargetLevel(int targetLevel) {
        Logger.log("Updating target lvl");
        this.targetLevel = targetLevel;
    }

    public int getTargetLevel() {
        return targetLevel;
    }
}
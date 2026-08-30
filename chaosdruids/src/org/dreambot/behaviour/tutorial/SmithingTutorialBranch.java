package org.dreambot.behaviour.tutorial;


import org.dreambot.fractals.Fractal;

public class SmithingTutorialBranch extends Fractal {
    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() < 360;
    }
}

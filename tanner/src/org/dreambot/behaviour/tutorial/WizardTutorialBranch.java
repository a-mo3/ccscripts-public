package org.dreambot.behaviour.tutorial;


import org.dreambot.fractals.Fractal;

public class WizardTutorialBranch extends Fractal {
    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() < 1000;
    }
}

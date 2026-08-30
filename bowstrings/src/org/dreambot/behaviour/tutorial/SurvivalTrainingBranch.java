package org.dreambot.behaviour.tutorial;


import org.dreambot.fractals.Fractal;

public class SurvivalTrainingBranch extends Fractal {
    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() < 120;
    }
}

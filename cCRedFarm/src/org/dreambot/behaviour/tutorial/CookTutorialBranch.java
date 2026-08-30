package org.dreambot.behaviour.tutorial;


import org.dreambot.fractals.Fractal;

public class CookTutorialBranch extends Fractal {
    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() < 170;
    }
}

package org.dreambot.behaviour.tutorial;


import org.dreambot.fractals.Fractal;

public class QuestTutorialBranch extends Fractal {
    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() < 250;
    }
}

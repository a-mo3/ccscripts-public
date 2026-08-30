package org.dreambot.behaviour.tutorial.charcreator.design;


import org.dreambot.api.methods.interactive.Players;
import org.dreambot.behaviour.tutorial.charcreator.ApperanceWidgets;
import org.dreambot.behaviour.tutorial.charcreator.CharacterFeature;

import java.util.Random;

public enum HandDesign implements CharacterFeature {
    BRACLET(289),
    EMPTY(290)
    ;
    public int value;

    HandDesign(int value) {
        this.value = value;
    }

    @Override
    public int currentlySelected() {
        final int index = 9;
        return Players.getLocal().getAppearance()[index];
    }

    @Override
    public boolean isComplete() {
        final int index = 9;
        return Players.getLocal().getAppearance()[index] == value;
    }

    @Override
    public boolean selectLeft() {
        return ApperanceWidgets.HANDS_DESIGN.selectLeft();
    }

    @Override
    public boolean selectRight() {
        return ApperanceWidgets.HANDS_DESIGN.selectRight();
    }

    public static HandDesign getRandom() {
        HandDesign[] a = values();
        return a[new Random().nextInt(a.length)];
    }

    @Override
    public int getTarget() {
        return value;
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }
}

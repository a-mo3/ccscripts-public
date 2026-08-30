package org.dreambot.behaviour.tutorial.charcreator.design;


import org.dreambot.api.methods.interactive.Players;
import org.dreambot.behaviour.tutorial.charcreator.ApperanceWidgets;
import org.dreambot.behaviour.tutorial.charcreator.CharacterFeature;

import java.util.Random;

public enum FeetDesign implements CharacterFeature {
    SHORT(298),
    LONG(299);
    public int value;

    FeetDesign(int value) {
        this.value = value;
    }

    @Override
    public int currentlySelected() {
        final int index = 10;
        return Players.getLocal().getEquipment().get(index).getID(); // todo NPE
    }

    @Override
    public boolean isComplete() {
        final int index = 10;
        return Players.getLocal().getAppearance()[index] == value;
    }

    @Override
    public boolean selectLeft() {
        return ApperanceWidgets.FEET_DESIGN.selectLeft();
    }

    @Override
    public boolean selectRight() {
        return ApperanceWidgets.FEET_DESIGN.selectRight();
    }

    public static FeetDesign getRandom() {
        FeetDesign[] a = values();
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

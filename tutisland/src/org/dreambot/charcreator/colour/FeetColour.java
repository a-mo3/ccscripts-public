package org.dreambot.charcreator.colour;


import org.dreambot.api.methods.interactive.Players;
import org.dreambot.charcreator.ApperanceWidgets;
import org.dreambot.charcreator.CharacterFeature;

import java.util.Random;

public enum FeetColour implements CharacterFeature {
    BROWN(0),
    DARK_GREEN(1),
    GREY(2),
    BLACK(3),
    LIGHT_BROWN(4),
    METAL(5),
    ;
    public int value;
    // index in the player comps colors

    FeetColour(int value) {
        this.value = value;
    }

    @Override
    public int currentlySelected() {
        final int index = 2;
        return Players.getLocal().getBodyColors()[index];
    }

    @Override
    public boolean isComplete() {
        final int index = 3;
        return Players.getLocal().getBodyColors()[index] == value;
    }

    @Override
    public boolean selectLeft() {
        return ApperanceWidgets.FEET_COLOUR.selectLeft();
    }

    @Override
    public boolean selectRight() {
        return ApperanceWidgets.FEET_COLOUR.selectRight();
    }

    public static FeetColour getRandom() {
        FeetColour[] a = values();
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

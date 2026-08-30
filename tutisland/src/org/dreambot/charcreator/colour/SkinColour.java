package org.dreambot.charcreator.colour;


import org.dreambot.api.methods.interactive.Players;
import org.dreambot.charcreator.ApperanceWidgets;
import org.dreambot.charcreator.CharacterFeature;

import java.util.Random;

public enum SkinColour implements CharacterFeature {
    WHITE(0),
    MIXED(1),
    AFRICAN_AMERICAN(2),
    BLACK(3),
    NATIVE(4),
    AFRICAN(5),
    AFRICAGAMER1(6),
    INDOORS_WHITE(7)
    ;
    public int value;

    SkinColour(int value) {
        this.value = value;
    }

    @Override
    public int currentlySelected() {
        final int index = 4;
        return Players.getLocal().getBodyColors()[index];
    }

    @Override
    public boolean isComplete() {
        final int index = 4;
        return Players.getLocal().getBodyColors()[index] == value;
    }

    @Override
    public boolean selectLeft() {
        return ApperanceWidgets.SKIN_COLOUR.selectLeft();
    }

    @Override
    public boolean selectRight() {
        return ApperanceWidgets.SKIN_COLOUR.selectRight();
    }

    public static SkinColour getRandom() {
        SkinColour[] a = values();
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

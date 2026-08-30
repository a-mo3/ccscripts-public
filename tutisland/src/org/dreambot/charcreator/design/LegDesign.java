package org.dreambot.charcreator.design;


import org.dreambot.api.methods.interactive.Players;
import org.dreambot.charcreator.ApperanceWidgets;
import org.dreambot.charcreator.CharacterFeature;

import java.util.Random;

public enum LegDesign implements CharacterFeature {
    PLAIN(292),
    SHORTS(293),
    FLARED(294),
    CUFFED(295),
    TORN(296),
    DOUBLE_CUFFED(297),
    CUFFED_BELT(356),
    HUNTING(357),
    ANKLE_HIGH(358),
    TRAMP_SHORTS(359),
    HOMELESS(360),
    ;
    public int value;

    LegDesign(int value) {
        this.value = value;
    }

    @Override
    public int currentlySelected() {
        final int index = 7;
        return Players.getLocal().getAppearance()[index];
    }

    @Override
    public boolean isComplete() {
        final int index = 7;
        return Players.getLocal().getAppearance()[index] == value;
    }

    @Override
    public boolean selectLeft() {
        return ApperanceWidgets.LEGS_DESIGN.selectLeft();
    }

    @Override
    public boolean selectRight() {
        return ApperanceWidgets.LEGS_DESIGN.selectRight();
    }

    public static LegDesign getRandom() {
        LegDesign[] a = values();
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

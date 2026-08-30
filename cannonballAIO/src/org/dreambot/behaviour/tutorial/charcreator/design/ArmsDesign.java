package org.dreambot.behaviour.tutorial.charcreator.design;


import org.dreambot.api.methods.interactive.Players;
import org.dreambot.behaviour.tutorial.charcreator.ApperanceWidgets;
import org.dreambot.behaviour.tutorial.charcreator.CharacterFeature;

import java.util.Random;

public enum ArmsDesign implements CharacterFeature {
    PLAIN(282),
    BUFF(283),
    LONG_SLEEVES(284),
    CUFFED(285),
    SCUFFED(286),
    METAL(287),
    STRIPED(288),
    STRIPED_CUFFED(340),
    WHITE_CUFFS(341),
    WHITE_SLEEVES(342),
    LOOSE_SLEEVES(343),
    TORN(344),
    ;

    public int value;

    ArmsDesign(int value) {
        this.value = value;
    }

    @Override
    public int currentlySelected() {
        final int index = 6;
        return Players.getLocal().getAppearance()[index];
    }

    @Override
    public boolean isComplete() {
        final int index = 6;
        return Players.getLocal().getAppearance()[index] == value;
    }

    @Override
    public boolean selectLeft() {
        return ApperanceWidgets.ARMS_DESIGN.selectLeft();
    }

    @Override
    public boolean selectRight() {
        return ApperanceWidgets.ARMS_DESIGN.selectRight();
    }

    public static ArmsDesign getRandom() {
        ArmsDesign[] a = values();
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

package org.dreambot.behaviour.tutorial.charcreator.design;


import org.dreambot.api.methods.interactive.Players;
import org.dreambot.behaviour.tutorial.charcreator.ApperanceWidgets;
import org.dreambot.behaviour.tutorial.charcreator.CharacterFeature;

import java.util.Random;

// these only apply to males
public enum JawDesign implements CharacterFeature {
    NONE(270),
    GOATEE(266),
    LONG(267),
    HANDLE_BAR(367),
    MEDIUM(268),
    MOUSTACHE(269),
    SHORT(271),
    POINTY(272),
    SPLIT(273),
    MUTTON(368),
    FULL_MUTTON(369),
    BIG_MOUSTACHE(370),
    WAXED_MOUSTACHE(371),
    DALL(372),
    VIZIER(373),
    ;
    public int value;

    JawDesign(int value) {
        this.value = value;
    }

    @Override
    public int currentlySelected() {
        final int index = 11;
        return Players.getLocal().getAppearance()[index];
    }

    @Override
    public boolean isComplete() {
        final int index = 11;
        return Players.getLocal().getAppearance()[index] == value;
    }

    @Override
    public boolean selectLeft() {
        return ApperanceWidgets.JAW_DESIGN.selectLeft();
    }

    @Override
    public boolean selectRight() {
        return ApperanceWidgets.JAW_DESIGN.selectRight();
    }

    public static JawDesign getRandom() {
        JawDesign[] a = values();
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

package org.dreambot.charcreator.design;


import org.dreambot.api.methods.interactive.Players;
import org.dreambot.charcreator.ApperanceWidgets;
import org.dreambot.charcreator.CharacterFeature;

import java.util.Random;

public enum TorsoDesign implements CharacterFeature {
    PLAIN(274),
    THREE_BUTTONS(275),
    THREE_CROSSES(276),
    LAPEL(277),
    TWO_POCKETS(278),
    STRIPES(279),
    HOMELESS(280),
    HALF(281),
    STRIPE(361),
    OPENED(362),
    TRIMMED(363),
    OPENED_BELT(364),
    HOMELESS_VEST(365),
    HOMELESS_VEST_THIN(366),
    ;
    public int value;

    TorsoDesign(int value) {
        this.value = value;
    }

    @Override
    public int currentlySelected() {
        final int index = 4;
//        return Players.getLocal().getPlayer().getPlayerComposition().getEquipmentIds()[index];
        return Players.getLocal().getAppearance()[index];
    }

    @Override
    public int getTarget() {
        return value;
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }

    @Override
    public boolean isComplete() {
        final int index = 4;
        return Players.getLocal().getAppearance()[index] == value;
    }

    @Override
    public boolean selectLeft() {
        return ApperanceWidgets.TORSO_DESIGN.selectLeft();
    }

    @Override
    public boolean selectRight() {
        return ApperanceWidgets.TORSO_DESIGN.selectRight();
    }

    public static TorsoDesign getRandom() {
        TorsoDesign[] a = values();
        return a[new Random().nextInt(a.length)];
    }
}

package org.dreambot.charcreator.colour;


import org.dreambot.api.methods.interactive.Players;
import org.dreambot.charcreator.ApperanceWidgets;
import org.dreambot.charcreator.CharacterFeature;

import java.util.Random;

public enum TorsoColour implements CharacterFeature {
    DEFAULT_GREEN(0),
    BLACK(1),
    MAROON(2),
    NAVY_BLUE(3),
    DESERT(4),
    WHITE(5),
    RED(6),
    BLUE(7),
    GREEN(8),
    GREEN_YELLOW(9),
    PURPLE(10),
    ORANGE(11),
    LIGHT_PINK(12),
    TURQUOISE(13),
    LIGHT_BLUE(14),
    DARK_GREEN(15), // matches default pants
    BLACK_TWO(16),
    LIGHT_GREY(17),
    OFF_WHITE(18),
    ORANGE_CREAM(19),
    BABY_BLUE(20),
    DARK_BLUE(21),
    LIGHT_PINK_TWO(22),
    STRAWBERRY(23),
    MAROON_TWO(24),
    LIGHT_GREEN(25),
    SWAMP_GREEN(26),
    PURPLE_TWO(27),
    MACLOF_PURPLE(28),
    ;

    public int value;

    TorsoColour(int value) {
        this.value = value;
    }

    @Override
    public int currentlySelected() {
        final int index = 1;
        return Players.getLocal().getBodyColors()[index];
    }

    @Override
    public boolean isComplete() {
        final int index = 1;
        return Players.getLocal().getBodyColors()[index] == value;
    }

    @Override
    public boolean selectLeft() {
        return ApperanceWidgets.TORSO_COLOUR.selectLeft();
    }

    @Override
    public boolean selectRight() {
        return ApperanceWidgets.TORSO_COLOUR.selectRight();
    }

    public static TorsoColour getRandom() {
        TorsoColour[] a = values();
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

package org.dreambot.behaviour.tutorial.charcreator.colour;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.behaviour.tutorial.charcreator.ApperanceWidgets;
import org.dreambot.behaviour.tutorial.charcreator.CharacterFeature;

import java.util.Random;

public enum LegColour implements CharacterFeature {
    DEFAULT_GREEN(0),
    SHIRT_GREEN(1),
    BLACK(2),
    MAROON(3),
    NAVY_BLUE(4),
    DESERT(5),
    WHITE(6),
    RED(7),
    BLUE(8),
    GREEN(9),
    GREEN_YELLOW(10),
    PURPLE(11),
    ORANGE(12),
    LIGHT_PINK(13),
    TURQUOISE(14),
    LIGHT_BLUE(15),
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
    // index in the player comps colors
    public static final int LEG_COLOUR_INDEX = 2;

    LegColour(int value) {
        this.value = value;
    }

    @Override
    public int currentlySelected() {
        final int index = 2;
        return Players.getLocal().getBodyColors()[index];
    }

    @Override
    public boolean isComplete() {
        final int index = 2;
        return Players.getLocal().getBodyColors()[index] == value;
    }

    @Override
    public boolean selectLeft() {
        return ApperanceWidgets.LEGS_COLOUR.selectLeft();
    }

    @Override
    public boolean selectRight() {
        return ApperanceWidgets.LEGS_COLOUR.selectRight();
    }

    public static LegColour getRandom() {
        LegColour[] a = values();
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

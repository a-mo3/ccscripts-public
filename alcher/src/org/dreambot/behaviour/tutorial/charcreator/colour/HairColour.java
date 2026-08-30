package org.dreambot.behaviour.tutorial.charcreator.colour;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.behaviour.tutorial.charcreator.ApperanceWidgets;
import org.dreambot.behaviour.tutorial.charcreator.CharacterFeature;

import java.util.Random;

public enum HairColour implements CharacterFeature {
    BROWN(0),
    WHITE(1),
    GREY(2),
    BLACK(3),
    GINGER(4),
    BLOND(5),
    DIRTY_BLONDE(6),
    LIGHT_BROWN(7),
    BLUE(8),
    GREEN(9),
    RED(10),
    PURPLE(11),
    REALLY_BLACK(12),
    DARKER_GREY(13),
    LIGHT_GREY(14),
    LIGHT_ORANGE(15),
    LIGHT_BLUE(16),
    LAPIS_BLUE(17),
    LIGHT_PINK(18),
    LIGHT_RED(19),
    MAROON(20),
    LIGHT_GREEN(21),
    DARK_GREEN(22),
    DARK_PURPLE(23),
    LIGHT_PURPLE(24),
    NAVY_BLUE(25),
    BRIGHT_RED(26),
    BRIGHT_YELLOW(27),
    DARKER_PURPLE(28),
    BRIGHT_BLUE(29),
    ;
    public int value;

    HairColour(int value) {
        this.value = value;
    }

    @Override
    public int currentlySelected() {
        final int index = 2;
        return Players.getLocal().getBodyColors()[index];
    }

    @Override
    public boolean isComplete() {
        final int index = 0;
        return Players.getLocal().getBodyColors()[index] == value;
    }

    @Override
    public boolean selectLeft() {
        return ApperanceWidgets.HAIR_COLOUR.selectLeft();
    }

    @Override
    public boolean selectRight() {
        return ApperanceWidgets.HAIR_COLOUR.selectRight();
    }

    public static HairColour getRandom() {
        HairColour[] a = values();
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

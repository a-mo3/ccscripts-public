package org.dreambot.charcreator;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.widgets.WidgetChild;

public enum ApperanceWidgets {
    SKIN_COLOUR(59, 60, 4, true),
    HAIR_COLOUR(43, 44, 0, true),
    TORSO_COLOUR(47, 48, 1, true),
    LEGS_COLOUR(51, 52, 2, true),
    FEET_COLOUR(55, 56, 0, true),

    HEAD_DESIGN(12, 13, 8, false),
    JAW_DESIGN(14, 15, 4, false),
    TORSO_DESIGN(20, 21, 4, false),
    ARMS_DESIGN(24, 25, 4, false),
    HANDS_DESIGN(28, 29, 4, false),
    LEGS_DESIGN(32, 33, 4, false),
    FEET_DESIGN(36, 37, 4, false),
    ;

    private static final int PARENT = 679;
    private int leftChild;
    private int rightChild;
    private int index; // this shits pointless now
    private boolean isColor;

    ApperanceWidgets(int left, int right, int index, boolean color) {
        leftChild = left;
        rightChild = right;
        this.index = index;
        isColor = color;
    }

    public boolean selectLeft() {
        WidgetChild leftButton = Widgets.get(PARENT, leftChild);
        if (leftButton != null) {
            return leftButton.interact("Select");
        }
        return false;
    }

    public boolean selectRight() {
        WidgetChild rightButton = Widgets.get(PARENT, rightChild);
        if (rightButton != null) {
            return rightButton.interact("Select");
        }
        return false;
    }

    public int getValue() {
        if (isColor) return Players.getLocal().getBodyColors()[index];
        return Players.getLocal().getAppearance()[index];
    }
}

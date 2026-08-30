package org.dreambot.charcreator;

public interface CharacterFeature {
    // the currently selected id
    int currentlySelected();

    int getTarget();

    int getOrdinal();

    /**
     * @return if you have this enum selected
     */
    boolean isComplete();

    /**
     * @return selects the left arrow
     */
    boolean selectLeft();

    /**
     * @return selects the right arrow
     */
    boolean selectRight();
}

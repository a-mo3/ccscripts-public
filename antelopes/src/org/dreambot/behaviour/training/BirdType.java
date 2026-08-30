package org.dreambot.behaviour.training;

/**
 * birds to catch hunter
 */
public enum BirdType {
    COPPER_LONGTAIL(9344, 9379) // im not sure if these ids are the right way around
    ;

    BirdType(int BROKEN_SNARE_ID, int CAUGHT_SNARE_ID) {
        this.BROKEN_SNARE_ID = BROKEN_SNARE_ID;
        this.CAUGHT_SNARE_ID = CAUGHT_SNARE_ID;
    }

    final int BROKEN_SNARE_ID;
    final int CAUGHT_SNARE_ID;
}

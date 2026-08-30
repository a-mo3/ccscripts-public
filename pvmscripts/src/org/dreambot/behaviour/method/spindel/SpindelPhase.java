package org.dreambot.behaviour.method.spindel;

import org.dreambot.api.methods.prayer.Prayer;

public enum SpindelPhase {
    RANGE_SPECIAL(Prayer.PROTECT_FROM_MISSILES),
    RANGE_BENIGN(Prayer.PROTECT_FROM_MISSILES),
    MAGE_SPECIAL(Prayer.PROTECT_FROM_MAGIC),
    MAGE_BENIGN(Prayer.PROTECT_FROM_MAGIC),
    ;

    private final Prayer overHead;

    SpindelPhase(Prayer overHead) {
        this.overHead = overHead;
    }

    public SpindelPhase getNext() {
        return values()[(ordinal() + 1) % 4];
    }
}

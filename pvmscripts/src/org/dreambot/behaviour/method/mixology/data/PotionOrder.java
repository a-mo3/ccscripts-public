package org.dreambot.behaviour.method.mixology.data;

import lombok.Setter;
import lombok.ToString;

public class PotionOrder {

    private final int idx;
    private final PotionType potionType;
    private final PotionModifier potionModifier;

    @Setter
    private boolean fulfilled;

    public PotionOrder(int idx, PotionType potionType, PotionModifier potionModifier) {
        this.idx = idx;
        this.potionType = potionType;
        this.potionModifier = potionModifier;
    }

    public int idx() {
        return idx;
    }

    public PotionType potionType() {
        return potionType;
    }

    public PotionModifier potionModifier() {
        return potionModifier;
    }

    public boolean fulfilled() {
        return fulfilled;
    }

    @Override
    public String toString() {
        return potionType + " " + potionModifier + " " + fulfilled;
    }
}
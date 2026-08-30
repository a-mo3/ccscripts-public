package org.dreambot.behaviour.method.gemstone;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.fractals.data.ItemID;

import java.util.function.Supplier;

public enum GemStoneSpecWeapons {
    GMAUL(ItemID.GRANITE_MAUL, 50, () -> Skill.ATTACK.getLevel() >= 50 && Skill.STRENGTH.getLevel() >= 50),

    ;

    public final int id;
    public final int cost;
    public final Supplier<Boolean> canUse;

    GemStoneSpecWeapons(int id, int cost, Supplier<Boolean> canUse) {
        this.id = id;
        this.cost = cost;
        this.canUse = canUse;
    }
}

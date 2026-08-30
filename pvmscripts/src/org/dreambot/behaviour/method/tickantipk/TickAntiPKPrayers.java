package org.dreambot.behaviour.method.tickantipk;

import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.TickDecision;

public class TickAntiPKPrayers extends TickDecision {
    @Override
    public boolean evaluate() {
        if (Skill.PRAYER.getBoostedLevel() == 0) return false;
        Prayers.toggle(true, Prayer.PROTECT_ITEM);
        Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
        return false;
    }
}

package org.dreambot.behaviour.method.gemstone;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.fractals.TickDecision;

public class GemStoneRangeStyle extends TickDecision {
    final int def;

    public GemStoneRangeStyle(int def) {
        this.def = def;
    }

    @Override
    public boolean evaluate() {
        if (Skill.DEFENCE.getLevel() < def) {
            if (Combat.getCombatStyle() != CombatStyle.RANGED_DEFENCE) {
                log("Set ranged def");
                Combat.setCombatStyle(CombatStyle.RANGED_DEFENCE);
            }
        } else {
            if (Combat.getCombatStyle() != CombatStyle.RANGED_RAPID) {
                log("Ranged rapid");
                Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
            }
        }
        return false;
    }
}

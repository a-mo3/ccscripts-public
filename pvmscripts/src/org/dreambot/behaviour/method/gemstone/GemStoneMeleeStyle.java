package org.dreambot.behaviour.method.gemstone;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.fractals.TickDecision;

import java.util.HashMap;
import java.util.Map;

public class GemStoneMeleeStyle extends TickDecision {
    final Map<Skill, Integer> skillTargets;

    public GemStoneMeleeStyle(Map<Skill, Integer> targets) {
        this.skillTargets = targets;

        styleMap.put(Skill.STRENGTH, CombatStyle.STRENGTH);
        styleMap.put(Skill.ATTACK, CombatStyle.ATTACK);
        styleMap.put(Skill.DEFENCE, CombatStyle.DEFENCE);

    }

    /**
     * @param targets map of skill (atk, str, def) | target level
     * @return the lowest of your stats that doesnt match the
     */
    public Skill getSkill(Map<Skill, Integer> targets) {
        Skill lowest = null;
        for (Map.Entry<Skill, Integer> target : targets.entrySet()) {
            Skill skill = target.getKey();
            int targetLevel = target.getValue();
            int lvl = skill.getLevel();
            if (targetLevel <= lvl) continue;
            if (lowest == null) lowest = skill;
            if (lvl < lowest.getLevel()) lowest = skill;
        }
        return lowest;
    }

    // todo change this based on weapons style, or dont i forget how these work on lost v dreabot
    Map<Skill, CombatStyle> styleMap = new HashMap<>();

    @Override
    public boolean evaluate() {
        Skill desiredSkill = getSkill(skillTargets);
        CombatStyle desiredStyle = (desiredSkill == null ? Combat.getCombatStyle() : styleMap.get(desiredSkill));
        if (Combat.getCombatStyle() != desiredStyle) {
            log("Set style to " + desiredStyle);
            if (desiredStyle == null) {
                log("Style null, we're cooked.");
                return false;
            }
            Combat.setCombatStyle(desiredStyle);
        }
        return false;
    }
}

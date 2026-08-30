package org.dreambot.behaviour.combat;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.utilities.Logger;

import java.util.*;

public class MeleeStyleSelector implements StyleSelector {
    List<Skill> meleeSkills = Arrays.asList(Skill.STRENGTH, Skill.ATTACK, Skill.DEFENCE);
    Map<Skill, CombatStyle> styleMap;


    public MeleeStyleSelector() {
        styleMap = new HashMap<>();
        styleMap.put(Skill.STRENGTH, CombatStyle.STRENGTH);
        styleMap.put(Skill.ATTACK, CombatStyle.ATTACK);
        styleMap.put(Skill.DEFENCE, CombatStyle.DEFENCE);
    }

    @Override
    public boolean setStyle() {
        Skill s = meleeSkills.stream().min(Comparator.comparingInt(i -> i.getLevel() / 10)).orElse(null);
        if (Combat.getCombatStyle() != styleMap.get(s)) {
            Logger.info("Set " + s);
            Combat.setCombatStyle(styleMap.get(s));
            return true;
        }
        return false;
    }
}

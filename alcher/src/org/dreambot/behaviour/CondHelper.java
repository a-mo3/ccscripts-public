package org.dreambot.behaviour;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

public class CondHelper {
    /**
     *
     * @param s skill
     * @param high high exclusive
     * @param low low inclusive
     * @return if between the two
     */
    public static boolean skillBetween(Skill s, int high, int low) {
        return Skills.getRealLevel(s) < high && Skills.getRealLevel(s) >= low;
    }
}

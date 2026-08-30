package org.dreambot.behaviour.method.toa;

import org.dreambot.fractals.Fractal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * invocations have active / deactive action depending on their state, to set all we should be able to clear all
 * and then just loop through our target and get it going
 */
public class ConfigureInvos extends Fractal {
    List<Invocation> invocations = Arrays.asList(
            Invocation.SOFTCORE_RUN,
            Invocation.WALK_FOR_IT,
            Invocation.WALK_THE_PATH,
            Invocation.DEADLY_PRAYERS,
            Invocation.ON_A_DIET,
            Invocation.LIVELY_LARVAE,
            Invocation.MORE_OVERLORDS,
            Invocation.BLOWING_MUD,
            Invocation.NOT_JUST_A_HEAD,
            Invocation.ARTERIAL_SPRAY,
            Invocation.BLOOD_THINNERS,
            Invocation.STAY_VIGILANT,
            Invocation.FEELING_SPECIAL,
            Invocation.GOTTA_HAVE_FAITH,
            Invocation.JUNGLE_JAPES,
            Invocation.SHAKING_THINGS_UP,
            Invocation.OVERCLOCKED,
            Invocation.OVERCLOCKED_2,
            Invocation.INSANITY
    );

    // 774, 69 action: Clear All
}

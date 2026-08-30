package org.dreambot.behaviour.method.toa;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.widgets.WidgetChild;

@RequiredArgsConstructor
public enum Invocation {

    TRY_AGAIN(5),
    PERSISTENCE(10),
    SOFTCORE_RUN(15),
    HARDCORE_RUN(25),
    WALK_FOR_IT(10),
    JOG_FOR_IT(15),
    RUN_FOR_IT(20),
    SPRINT_FOR_IT(25),
    NEED_SOME_HELP(15),
    NEED_LESS_HELP(25),
    NO_HELP_NEEDED(40),
    WALK_THE_PATH(50),
    PATHSEEKER(15),
    PATHFINDER(40),
    PATHMASTER(50),
    QUIET_PRAYERS(20),
    DEADLY_PRAYERS(20),
    ON_A_DIET(15),
    DEHYDRATION(30),
    OVERLY_DRAINING(15),
    LIVELY_LARVAE(5),
    MORE_OVERLORDS(15),
    BLOWING_MUD(10),
    MEDIC(15),
    AERIAL_ASSAULT(10),
    NOT_JUST_A_HEAD(15),
    ARTERIAL_SPRAY(10),
    BLOOD_THINNERS(5),
    UPSET_STOMACH(15),
    DOUBLE_TROUBLE(20),
    KEEP_BACK(10),
    STAY_VIGILANT(15),
    FEELING_SPECIAL(20),
    MIND_THE_GAP(10),
    GOTTA_HAVE_FAITH(10),
    JUNGLE_JAPES(5),
    SHAKING_THINGS_UP(10),
    BOULDERDASH(10),
    ANCIENT_HASTE(10),
    ACCELERATION(10),
    PENETRATION(10),
    OVERCLOCKED(10),
    OVERCLOCKED_2(10),
    INSANITY(50),
    ;

    @Getter
    private final int raidLevel;

    public int getWidgetId() {
        return ordinal() * 3;
    }

    public WidgetChild getWidget() {
        return Widgets.get(774, 52, getWidgetId());
    }
}
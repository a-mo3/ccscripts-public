package org.dreambot.behaviour.method.chaosfanatic;

import org.dreambot.behaviour.method.chaosfanatic.decisions.ExitWithLoot;
import org.dreambot.behaviour.method.chaosfanatic.decisions.FightChaosFanatic;
import org.dreambot.behaviour.method.chaosfanatic.decisions.GoToChaosFanatic;
import org.dreambot.fractals.TickFractal;
import org.dreambot.scriptdata.ChaosFanaticSettings;

import java.util.function.Supplier;

public class ChaosFanatic extends TickFractal {
    public ChaosFanatic(Supplier<Boolean> acceptCondition, ChaosFanaticSettings settings) {
        super(acceptCondition);

        setSimpleName("Chaos Fanatic");
        addChildren(
                new ExitWithLoot(settings).setSimpleName("Exit"),
                new GoToChaosFanatic().setSimpleName("Go to CF"),
                new FightChaosFanatic(settings).setSimpleName("Fight CF")
        );
    }
}

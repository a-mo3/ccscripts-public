package org.dreambot.behaviour.method.chaoselemental;

import org.dreambot.behaviour.method.chaoselemental.decisions.ExitWithLoot;
import org.dreambot.behaviour.method.chaoselemental.decisions.FightChaosElemental;
import org.dreambot.behaviour.method.chaoselemental.decisions.GoToChaosElemental;
import org.dreambot.behaviour.method.chaosfanatic.decisions.FightChaosFanatic;
import org.dreambot.behaviour.method.chaosfanatic.decisions.GoToChaosFanatic;
import org.dreambot.fractals.TickFractal;
import org.dreambot.scriptdata.ChaosElementalSettings;

import java.util.function.Supplier;

public class ChaosElemental extends TickFractal {
    public ChaosElemental(Supplier<Boolean> acceptCondition, ChaosElementalSettings settings) {
        super(acceptCondition);

        setSimpleName("Chaos Fanatic");
        addChildren(
                new ExitWithLoot(settings).setSimpleName("Exit"),
                new GoToChaosElemental().setSimpleName("Go to CF"),
                new FightChaosElemental(settings).setSimpleName("Fight CF")
        );
    }
}

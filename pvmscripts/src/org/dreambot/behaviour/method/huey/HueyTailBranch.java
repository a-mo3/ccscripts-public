package org.dreambot.behaviour.method.huey;

import org.dreambot.behaviour.method.huey.mainfight.HueyEatDecision;
import org.dreambot.behaviour.method.huey.mainfight.HueySpec;
import org.dreambot.behaviour.method.huey.tail.HueyTailAttack;
import org.dreambot.fractals.TickFractal;
import org.dreambot.scriptdata.HueycoatlSettings;

import java.util.function.Supplier;

public class HueyTailBranch extends TickFractal {
    public HueyTailBranch(Supplier<Boolean> acceptCondition, HueycoatlSettings settings) {
        super(acceptCondition);
        setSimpleName("Tail");
        addChildren(
                new HueyToggleRun().setSimpleName("Toggle run"),
                new HueySetAutocast(settings.loadout).setSimpleName("Set Autocast"),
                // todo ensure we're on a crush style
                new HueyTailPrayDecision(settings.loadout, settings.safePray).setSimpleName("Prayer"),
                // potions
                new HueyPotionDecision().setSimpleName("Tail potion"),
                // food
                new HueyEatDecision().setSimpleName("Tail eat"),

                HueyLightningWatch.getInstance().setSimpleName("Lightning"),

                new HueySpec(settings.useBurningClawsSpec).setSimpleName("Spec"),
                new HueyTailAttack().setSimpleName("Tail attack")
        );
    }
}

package org.dreambot.behaviour.training.magic;

import org.dreambot.api.Client;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class F2PMagicBranch extends Fractal {
    public F2PMagicBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("F2P magic training");

        addChildren(
                new ImpCatcher().setSimpleName("Impcatcher")
                        .setPrependLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Magic.castSpell(Normal.HOME_TELEPORT);
                                Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                            }
                            return false;
                        }),
                new MagicCombat(25, 15).setSimpleName("Getting some hp levels"),
                new MageIceGiants(() -> true)
                        .setSimpleName("Magic giants")
        );
    }

    public F2PMagicBranch(Supplier<Boolean> acceptCondition, int defTgt) {
        super(acceptCondition);
        setSimpleName("F2P magic training");

        addChildren(
                new ImpCatcher().setSimpleName("Impcatcher")
                        .setPrependLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Magic.castSpell(Normal.HOME_TELEPORT);
                                Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                            }
                            return false;
                        }),
                new MageIceGiants(() -> true)
                        .setDefenceTarget(defTgt)
                        .setSimpleName("Magic giants")
        );
    }
}

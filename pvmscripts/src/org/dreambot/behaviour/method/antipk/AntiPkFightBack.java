package org.dreambot.behaviour.method.antipk;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class AntiPkFightBack extends Fractal {
    public AntiPkFightBack(Supplier<Boolean> acceptCondition) {
        super(() -> Magic.canCast(Normal.ENTANGLE)
                && Magic.canCast(Normal.BIND)
                && acceptCondition.get());
    }

    @Override
    public int onLoop() {
        Player enemy = Players.closest(x -> x.getName().equals(AntiPkBranch.getAttackerName()));
        if (enemy == null) {
            log("Entangle back couldnt find the attacker");
            AntiPkBranch.setAttackerName(null);
            return ReactionGenerator.getQuick();
        }

        log("entangling");
        Magic.castSpellOn(bestRootSpell(), enemy);
        return ReactionGenerator.getQuick();
    }

    public static Spell bestRootSpell() {
        if (Magic.canCast(Normal.ENTANGLE)) return Normal.ENTANGLE;
        if (Magic.canCast(Normal.BIND)) return Normal.BIND;
        return null;
    }
}

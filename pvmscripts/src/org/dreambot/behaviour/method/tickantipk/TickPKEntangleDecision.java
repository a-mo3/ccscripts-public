package org.dreambot.behaviour.method.tickantipk;

import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.TickDecision;

import java.util.function.Supplier;

public class TickPKEntangleDecision extends TickDecision {
    final Supplier<Player> topEnemy;
    public TickPKEntangleDecision(Supplier<Player> topEnemy) {
        setSimpleName("Entangle");
        this.topEnemy = topEnemy;
    }

    @Override
    public boolean evaluate() {
        if (!Magic.canCast(Normal.ENTANGLE)) return false;

        Player t = topEnemy.get();
        if (t != null) {
            log("Cast entangle on mans");
            Magic.castSpellOn(Normal.ENTANGLE, t);
            return true;
        }
        return false;
    }
}

package org.dreambot.fractals;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.utilities.Logger;

@Getter
@Setter
@Accessors(chain = true)
public abstract class TickDecision {
    private String simpleName = "";
    /**
     * once per tick a TickFractal evaluates all its children in order,
     * if true this is a blocking action, do nothing else this tick
     * if false you can continue evaluation
     * eg. shouldIEat, even if you eat you can still walk in the same tick after
     * eating would put you on attacking cooldown, so you may not want to eat and would return true after attacking.
     */
    public abstract boolean evaluate();

    /**
     * log an the unit responsible and the tick it was decided
     * @param msg log message
     */
    protected void log(String msg) {
        Logger.info(String.format("[%s] - %d: %s", simpleName, Client.getGameTick(), msg));
    }
}

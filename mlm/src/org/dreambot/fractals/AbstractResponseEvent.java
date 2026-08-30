package org.dreambot.fractals;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.core.Instance;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

@Accessors(chain = true)
@Getter
@Setter
public abstract class AbstractResponseEvent<T> {
    T response;
    private int sleepLow = ReactionGenerator.getReactionSettings().normalLow;
    private int sleepHigh = ReactionGenerator.getReactionSettings().normalHigh;
    Supplier<Boolean> breakCondition;

    public T executed() {
        while (true) {
            if (breakCondition != null && breakCondition.get()) {
                onExit();
                Log.info("Break condition true");
                return null;
            }

            if (getResponse() != null) {
                onExit();
                return response;
            }

            if (!validate()) {
                onExit();
                return null;
            }
            Sleep.sleep(onLoop());
        }
    }

    /**
     * checks that if true would, in this context break a loop / cause a stall
     * @return true if we should keep going
     */
    private boolean validate() {
        if (Client.getInstance().getScriptManager().isPaused()) return false;
        if (!Client.isLoggedIn()) return false;
        if (!Instance.getInstance().getScriptManager().isRunning()) return false;
        return true;
    }

    abstract public int onLoop();

    private void onExit() {
        Logger.info("AbstractEvent onExit");
    }

    protected int sleep() {
        return Calculations.random(sleepLow, sleepHigh);
    }
}

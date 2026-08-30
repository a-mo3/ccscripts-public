package org.dreambot.fractals;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.core.Instance;
import org.dreambot.settings.timing.ReactionGenerator;

@Accessors(chain = true)
@Getter
@Setter
public abstract class AbstractResponseEvent<T> {
    T response;
    private int sleepLow = ReactionGenerator.getReactionSettings().normalLow;
    private int sleepHigh = ReactionGenerator.getReactionSettings().normalHigh;
    protected boolean allowLogout = false;

    public T executed() {
        onStart();
        while (true) {
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
     *
     * @return true if we should keep going
     */
    private boolean validate() {
        if (Client.getInstance().getScriptManager().isPaused()) return false;
        if (!allowLogout && !Client.isLoggedIn()) return false;
        if (!allowLogout && Client.getGameStateID() == 45) return false; // 45 is hopping
        return Instance.getInstance().getScriptManager().isRunning();
    }

    abstract public int onLoop();

    protected void onExit() {
        Logger.info("AbstractEvent onExit");
    }

    protected void onStart() {
        Logger.info("event onstart " + this);
    }

    protected int sleep() {
        return Calculations.random(sleepLow, sleepHigh);
    }
}

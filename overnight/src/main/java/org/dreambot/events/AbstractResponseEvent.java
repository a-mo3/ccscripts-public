package org.dreambot.events;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

@Accessors(chain = true)
@Getter
@Setter
public abstract class AbstractResponseEvent<T> {
    T response;
    private int sleepLow = 400;
    private int sleepHigh = 800;

    public T executed() {
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
     * @return true if we should keep going
     */
    private boolean validate() {
        if (!Client.isLoggedIn()) return false;
        if (!Client.getInstance().getScriptManager().isRunning()) return false;
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

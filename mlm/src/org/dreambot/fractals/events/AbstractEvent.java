package org.dreambot.fractals.events;

import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.Client;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

@Setter @Getter
public abstract class AbstractEvent {
    boolean failed;
    boolean complete;

    public void onStart() {}

    public void execute() {
        onStart();
        while (true) {
            if (isFailed()) {
                Logger.info("AbstractEvent Failed");
                onExit();
                return;
            }

            if (isComplete()) {
                Logger.info("AbstractEvent Failed");
                onExit();
                return;
            }

            if (!validate()) {
                Logger.info("event failed validation");
                onExit();
                return;
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
        if (!Client.getInstance().getScriptManager().isRunning()) return false;
        return true;
    }

    abstract public int onLoop();

    public void onExit() {
        Logger.info("AbstractEvent onExit");
    }
}

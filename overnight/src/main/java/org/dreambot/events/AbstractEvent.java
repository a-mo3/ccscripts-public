package org.dreambot.events;

import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.Client;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

@Setter @Getter
public abstract class AbstractEvent {
    boolean failed;
    boolean complete;

    public void execute() {
        while (true) {
            if (isFailed()) {
                onExit();
                return;
            }

            if (isComplete()) {
                onExit();
                return;
            }

            if (!validate()) {
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
        if (!Client.isLoggedIn()) return false;
        if (!Client.getInstance().getScriptManager().isRunning()) return false;
        return true;
    }

    abstract public int onLoop();

    public void onExit() {
        Logger.info("AbstractEvent onExit");
    }
}

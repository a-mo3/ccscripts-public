package org.dreambot.fractals.events;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.core.Instance;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.LinkedList;
import java.util.function.Supplier;

@Accessors(chain = true)
@Getter
@Setter
public abstract class AbstractResponseEvent<T> {
    T response;
    private int sleepLow = ReactionGenerator.getReactionSettings().normalLow;
    private int sleepHigh = ReactionGenerator.getReactionSettings().normalHigh;
    protected boolean allowLogout = false;
    public static String lastRan = "";
    private Supplier<Boolean> breakCondition = () -> false;
    public static Supplier<Boolean> globalBreakCondition = () -> false;
    public static LinkedList<String> history = new LinkedList<>();
    public boolean ignoreGlobalCondition = false;
    Timer timeout = new Timer(5 * 60 * 1000);

    public T executed() {
        onStart();
        history.push(this.toString());
        while (true) {
            if (!ignoreGlobalCondition && globalBreakCondition.get()) {
                Logger.info("Global Break condition true");
                onExit();
                history.pop();
                return null;
            }

            if (breakCondition.get()) {
                Logger.info("Break condition true");
                onExit();
                history.pop();
                return null;
            }

            if (getResponse() != null) {
                onExit();
                history.pop();
                return response;
            }

            if (timeout.finished()) {
                Logger.info("evnet timed out 5 min");
                onExit();
                history.pop();
                return null;
            }

            if (!validate()) {
                history.pop();
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
        if (!Instance.getInstance().getScriptManager().isRunning()) return false;
        return true;
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

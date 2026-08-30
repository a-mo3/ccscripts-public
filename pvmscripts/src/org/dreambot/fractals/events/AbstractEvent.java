package org.dreambot.fractals.events;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.PvmMain;
import org.dreambot.api.Client;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;

import java.util.function.Supplier;

@Setter
@Getter
@Accessors(chain = true)
public abstract class AbstractEvent {
    boolean failed;
    boolean complete;
    Supplier<Boolean> interruptCondition;
    public static Supplier<Boolean> globalInterruptCondition = () -> false;

    public void onStart() {
    }

    public void execute() {
        onStart();
        while (true) {
            if (interruptCondition != null && interruptCondition.get()) {
                Logger.info("Event interrupted " + this);
                onExit();
                return;
            }

            EventExitCondition c = AbstractResponseEvent.globalExitConditions.stream().filter(EventExitCondition::shouldExit).findFirst().orElse(null);
            if (c != null) {
                Logger.info("Global Break condition true " + c.name);
                onExit();
                return;
            }

            if (globalInterruptCondition.get()) {
                Logger.info("Event global interrupted " + this);
                onExit();
                return;
            }

            if (isFailed()) {
                Logger.info("AbstractEvent Failed " + this);
                onExit();
                return;
            }

            if (isComplete()) {
                Logger.info("AbstractEvent Completed" + this);
                onExit();
                return;
            }

            if (!validate()) {
                Logger.info("event failed validation " + this);
                onExit();
                return;
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
        if (PvmMain.isMouseTraining) return false;
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

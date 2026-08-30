package org.dreambot.fractals.events;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.PvmMain;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.core.Instance;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
    //    private Supplier<Boolean> breakCondition = () -> false;
//    public static Supplier<Boolean> globalBreakCondition = () -> Worlds.getCurrent().isLeagueWorld();
    public static LinkedList<String> history = new LinkedList<>();
    public boolean ignoreGlobalConditions = false;
    Timer timeout = new Timer(5 * 60 * 1000);

    public static List<EventExitCondition> globalExitConditions = new ArrayList<>(Arrays.asList(
            new EventExitCondition(() -> Worlds.getCurrent().isLeagueWorld(), "LEAGUES_EXIT")
    ));

    public List<EventExitCondition> exitConditions = new ArrayList<>(Arrays.asList(
    ));

    public static void addGlobalExitCondition(EventExitCondition eventExitCondition) {
        globalExitConditions.add(eventExitCondition);
    }

    public static void addGlobalExitCondition(Supplier<Boolean> condition, String exitConditionName) {
        globalExitConditions.add(new EventExitCondition(condition, exitConditionName));
    }

    public AbstractResponseEvent<T> addExitCondition(EventExitCondition eventExitCondition) {
        exitConditions.add(eventExitCondition);
        return this;
    }

    public T executed() {
        onStart();
        history.push(this.toString());
        while (true) {
            if (!ignoreGlobalConditions) {
                EventExitCondition c = globalExitConditions.stream().filter(EventExitCondition::shouldExit).findFirst().orElse(null);
                if (c != null) {
                    Logger.info("Global Break condition true " + c.name);
                    onExit();
                    history.pop();
                    return null;
                }
            }

            EventExitCondition c = exitConditions.stream().filter(EventExitCondition::shouldExit).findFirst().orElse(null);
            if (c != null) {
                Logger.info("Exit condition true " + c.name);
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
                Logger.info("event timed out 5 min");
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
        if (PvmMain.isMouseTraining) return false;
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
        return Calculations.random(Math.min(sleepHigh, sleepLow), Math.max(sleepHigh, sleepLow));
    }
}

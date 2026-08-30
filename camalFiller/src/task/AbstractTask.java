package task;

import config.Config;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.TaskNode;

/**
 * @author camalCase
 * @version 1
 * 25th aug refactor
 * desc: main, adds nodes & starts gui.
 */
public abstract class AbstractTask extends TaskNode {

    // Obtain an instance of ScriptManger to use in tasks, will be used for stopping.
    protected ScriptManager manager = ScriptManager.getScriptManager();

    // Obtain an instance of the Config class to use in our tasks.

    protected Config config = Config.getConfig();

    /**
     * Checks whether the task should run.
     */
    public abstract boolean accept();

    /**
     * Executes the task if accept() returns true.
     */
    public abstract int execute();
}
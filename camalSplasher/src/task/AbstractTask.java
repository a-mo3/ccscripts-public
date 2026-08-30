package task;

import config.Config;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.TaskNode;

/**
 * @author camalCase
 * @version 1
 * created 28 aug 2021
 * desc: extends tasknode to add extra functionality for tasks
 */


public abstract class AbstractTask extends TaskNode {
    // Gets an instance of Config object to use in TaskNode
    protected Config config = Config.getConfig();
    protected ScriptManager manager = ScriptManager.getScriptManager();
    protected final Area SEAGULLS = new Area(3027, 3237, 3034, 3236);




    // Checks whether the task should run.
    public abstract boolean accept();


    // Executes the task if accept() returns true.
    public abstract int execute();
}

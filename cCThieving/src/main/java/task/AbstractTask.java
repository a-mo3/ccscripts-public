package task;

import config.Config;
import org.dreambot.api.script.TaskNode;

public abstract class AbstractTask extends TaskNode {
    protected Config config = Config.getConfig();

}

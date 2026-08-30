package org.dreambot.behaviour;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.config.Config;

public abstract class AbstractTask extends TaskNode {
    protected Config config = Config.getConfig();

    protected final Area LARGE_REDCHIN_AREA = new Area(2540, 2924, 2564, 2910);
    protected final Area SMALL_AREA_RED = new Area(2557, 2917, 2559, 2915, 0);

    protected boolean walkTo(Area area) {
        if (!ScriptManager.getScriptManager().isRunning()) {
            // to close these methods when script is turned off
            return false;
        }
        if (Walking.getRunEnergy() > 15) {
            if (!Walking.isRunEnabled()) {
                Walking.toggleRun();
            }
        }
        if (!area.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) {
                if (Walking.shouldWalk(6)) Walking.walk(area.getRandomTile());
                Sleep.sleepUntil(Walking::shouldWalk, 3000);
            }
            Sleep.sleep(500);
            walkTo(area);
        }
        return true;
    }

    protected int getTrapLimit() {
        int level = Skills.getRealLevel(Skill.HUNTER);
        if (level < 20) {
            return 50;
        } else if (level < 40) {
            return 2;
        } else if (level < 60) {
            return 3;
        } else if (level < 80) {
            return 4;
        } else {
            return 5;
        }
    }
}

package task;

import config.Config;
import config.Pickaxe;
import config.Rock;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.items.Item;

public abstract class AbstractTask extends TaskNode {
    protected Config config = Config.getConfig();
    protected Rock rocks;


    // this is just for pickaxes to check if they can be equipped, used for progression
    protected boolean canEquip(Item pickaxe) {
        if (!pickaxe.getName().contains("pickaxe")) {
            return false;
        }
        int atkLevel = Skills.getRealLevel(Skill.ATTACK);
        int miningLevel = Skills.getRealLevel(Skill.MINING);

        for (Pickaxe p : Pickaxe.values()) {
            if (pickaxe.getID() == p.getID()) {
                if (atkLevel >= p.ATKREQ && miningLevel >= p.getREQ()) {
                    return true;
                }
            }
        }
        return false;
    }
}

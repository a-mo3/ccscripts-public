package org.dreambot.behaviour;

import org.dreambot.framework.Branch;
import org.dreambot.util.MyVarps;

public class QuestTutorialBranch extends Branch {
    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() < 250;
    }
}

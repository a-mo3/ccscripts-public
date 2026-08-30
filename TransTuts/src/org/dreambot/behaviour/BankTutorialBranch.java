package org.dreambot.behaviour;

import org.dreambot.framework.Branch;
import org.dreambot.util.MyVarps;

public class BankTutorialBranch extends Branch {
    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() < 540;
    }
}

package org.dreambot.behaviour.mining;

import org.dreambot.api.methods.map.Area;

public interface MiningLocation {
    Area getLocation();
    boolean isHcimSafe();
    boolean isMembers();
}

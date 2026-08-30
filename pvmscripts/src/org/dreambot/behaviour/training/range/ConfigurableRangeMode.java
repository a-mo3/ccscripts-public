package org.dreambot.behaviour.training.range;

import lombok.Getter;
import org.dreambot.LocalSDNOwnershipCache;
import org.dreambot.api.Client;
import org.dreambot.api.script.ScriptManager;

import java.util.Arrays;

@Getter
public enum ConfigurableRangeMode {
    SANDCRABS(),
    DISTRIBUTED(),
    GEMSTONE_CRAB(2103),
    SCURRIUS(2078),
    NMZ(2019)
    ;

    // IDs for scripts that must have any of to use this mode
    final int[] scriptIDs;

    ConfigurableRangeMode(int... scriptIDs) {
        this.scriptIDs = scriptIDs;
    }

    public boolean isOwned() {
        return LocalSDNOwnershipCache.ownsAny(scriptIDs);
//        return scriptIDs.length == 0 || Arrays.stream(scriptIDs).anyMatch(x -> sm.hasSDNScript(x) || sm.hasPurchasedScript(x) || sm.hasPremiumScript(x));
    }
}

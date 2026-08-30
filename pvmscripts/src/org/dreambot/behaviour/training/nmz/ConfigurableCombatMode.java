package org.dreambot.behaviour.training.nmz;

import lombok.Getter;
import org.dreambot.LocalSDNOwnershipCache;
import org.dreambot.api.Client;
import org.dreambot.api.script.ScriptManager;

import java.util.Arrays;

@Getter
public enum ConfigurableCombatMode {
    // does sandcrab until target levels
    SANDCRABS(),
    GEMSTONE_CRAB(2103),
    // does sulphur naguas, quests and prayer training
    SULPHUR_NAGUAS(1846, 1845),
    SCURRIUS(2078),
    NMZ(2019) // todo get ID once script is pushed
    ;

    // IDs for scripts that must have any of to use this mode
    final int[] scriptIDs;

    ConfigurableCombatMode(int... scriptIDs) {
        this.scriptIDs = scriptIDs;
    }

    public boolean isOwned() {
        return LocalSDNOwnershipCache.ownsAny(scriptIDs);
//        return scriptIDs.length == 0 || Arrays.stream(scriptIDs).anyMatch(x -> sm.hasSDNScript(x) || sm.hasPurchasedScript(x) || sm.hasPremiumScript(x));
    }
}

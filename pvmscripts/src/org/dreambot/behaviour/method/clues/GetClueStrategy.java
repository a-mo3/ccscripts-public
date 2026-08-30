package org.dreambot.behaviour.method.clues;

import org.dreambot.LocalSDNOwnershipCache;
import org.dreambot.api.Client;
import org.dreambot.api.script.ScriptManager;

import java.util.Arrays;

public enum GetClueStrategy {
    PURO_PURO(2061),
    VARLAMORE_CHEST(),
    BUYING_ECLECTIC(),
    ;


    // IDs for scripts that must have any of to use this mode
    final int[] scriptIDs;

    GetClueStrategy(int... scriptIDs) {
        this.scriptIDs = scriptIDs;
    }

    public boolean isOwned() {
        return LocalSDNOwnershipCache.ownsAny(scriptIDs);
//                Arrays.stream(scriptIDs).anyMatch(x -> sm.hasSDNScript(x) || sm.hasPurchasedScript(x) || sm.hasPremiumScript(x));
    }
}

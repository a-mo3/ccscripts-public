package org.dreambot;

import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * pinging sdn all the time causes problems for power users mass launching
 * (more me requesting purchased has and has premium, but im going to cache it any way)
 * cache ownership of script ids
 */
public class LocalSDNOwnershipCache {
    // scriptid, owned
    private static Map<Integer, Boolean> ownedCache = new HashMap<>();

    public static boolean ownsAny(int... scriptIds) {
        ScriptManager sm = ScriptManager.getScriptManager();
        if (scriptIds.length == 0) return true;
        for (int id : scriptIds) {
            if (!ownedCache.containsKey(id)) {
                boolean owns = sm.hasSDNScript(id);
                Logger.info("Checking ownership of " + id + " " + owns);
                ownedCache.put(id, owns);
            }
            if (ownedCache.get(id)) return true;
        }
        return false;
    }
}

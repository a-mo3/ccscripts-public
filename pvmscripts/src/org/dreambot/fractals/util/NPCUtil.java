package org.dreambot.fractals.util;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.NPC;

public class NPCUtil {
    public static boolean interact(String name) {
        NPC npc = NPCs.closest(name);
        if (npc == null) {
            Logger.info("Failed to interact with " + name);
            return false;
        }

        return npc.interact();
    }

    public static boolean interact(String name, String interaction) {
        NPC npc = NPCs.closest(name);
        if (npc == null) {
            Logger.info("Failed to interact with " + name);
            return false;
        }

        return npc.interact(interaction);
    }
}

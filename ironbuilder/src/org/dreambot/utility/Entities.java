package org.dreambot.utility;

import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.ArrayList;
import java.util.List;

public class Entities {
    public static Entity closest(String name) {
        NPC n = NPCs.closest(name);
        if (n != null) return n;
        return GameObjects.closest(name);
    }
}

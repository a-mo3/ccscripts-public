package org.dreambot.fractals.util;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.loadout.ItemVariants;

public class UtilProvider {
    public static void stdWalk(Area area) {
        if (Walking.shouldWalk() && (Walking.getDestination() == null || !area.contains(Walking.getDestination()))) {
            if (Walking.shouldWalk(6)) Walking.walk(area.getCenter());
        }
    }

    public static void stdWalk(Tile tile) {
        if (Walking.shouldWalk() && (Walking.getDestination() == null || !tile.equals(Walking.getDestination()))) {
            if (Walking.shouldWalk(6)) Walking.walk(tile);
        }
    }

    public static void staminaUp() {
        Item stamina = ItemVariants.STAMINA_POTION.getItem();
        if (stamina != null && Walking.getRunEnergy() < 20) {
            if (Inventory.isItemSelected()) Inventory.deselect();
            stamina.interact("Drink");
            return;
        }

        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 1) {
            Walking.toggleRun();
        }
    }
}

package org.dreambot.fractals.util;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;

public class ObjectUtil {
    public static boolean interact(String name, String interaction) {
        GameObject object = GameObjects.closest(name);
        if (object == null) {
            Logger.info(name + " not found - objutil");
            return false;
        }

        object.interact(interaction);
        return true;
    }

    public static boolean useOn(String name, Item item) {
        GameObject object = GameObjects.closest(name);
        if (object == null) {
            Logger.info(name + " not found - objutil");
            return false;
        }

        if (item == null) {
            Logger.info("Provided item was null objectUtil");
            return false;
        }

        item.useOn(object);
        return true;
    }

    public static boolean interact(String name) {
        GameObject object = GameObjects.closest(name);
        if (object == null) {
            Logger.info(name + " not found - objutil");
            return false;
        }

        object.interact();
        return true;
    }

    public static boolean interact(int id) {
        GameObject object = GameObjects.closest(id);
        if (object == null) {
            Logger.info(id + " not found - objutil");
            return false;
        }

        object.interact();
        return true;
    }
}

package org.dreambot.antiban;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;

/**
 * For now just a sleepUntil method that has a chance to mouse off and hover another item
 */
public class Antiban {
    public static boolean enabled = false;
    public static int mouseOffChance = 80;


    public static boolean sleepUntil(Condition predicate, long timeout) {
        if (!enabled) return Sleep.sleepUntil(predicate, timeout);
        return sleepUntil(predicate, timeout, 50);

    }

    public static boolean sleepUntil(Condition predicate, long timeout, long polling) {
        if (!enabled) return Sleep.sleepUntil(predicate, timeout, polling);
        return sleepUntil(predicate, () -> false, timeout, polling);
    }


    public static boolean sleepUntil(Condition predicate, Condition resetCondition, long timeout, long polling) {
        if (!enabled) return Sleep.sleepUntil(predicate, resetCondition, timeout, polling);
        // stop here if instant true so we dont mouse on and then off
        if (predicate.verify()) return true;
        long start = System.currentTimeMillis();

        int random = Calculations.random(101);
        if (100 - (mouseOffChance % 100) < random) Mouse.moveOutsideScreen();

        while ((System.currentTimeMillis() - start) < timeout && !predicate.verify()) {
            if (resetCondition != null && resetCondition.verify()) start = System.currentTimeMillis();
            Sleep.sleep(polling);
        }
        return predicate.verify();
    }
}

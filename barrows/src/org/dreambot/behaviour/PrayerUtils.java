package org.dreambot.behaviour;

import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;

import java.util.Arrays;

public class PrayerUtils {
    public static boolean isActive(Prayer... prayers) {
        return Arrays.stream(prayers).anyMatch(Prayers::isActive);
    }

    public static void disableAll(Prayer... prayers) {
        for (Prayer p : prayers) {
            if (Prayers.isActive(p)) Prayers.toggle(false, p);
        }
    }
}

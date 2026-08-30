package org.dreambot.fractals.util;

import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.util.Arrays;

public class PrayerUtils {
    public static boolean isActive(Prayer... prayers) {
        return Arrays.stream(prayers).anyMatch(Prayers::isActive);
    }

    public static void disableAll() {
        disable(Prayer.values());
    }

    public static void disable(Prayer... prayers) {
        for (Prayer p : prayers) {
            if (Prayers.isActive(p)) toggle(false, p);
        }
    }

    public static void toggle(boolean active, Prayer targetPrayer) {
        if (Prayers.isActive(targetPrayer) == active) return;
        if (!Menu.isMenuManipulationActive()) {
            Logger.log("Enable menu manipulation for a better experience");
            Prayers.toggle(active, targetPrayer);
            return;
        }

        WidgetChild wc = targetPrayer.getWidgetChild();
        if (wc == null) {
            Tabs.open(Tab.PRAYER);
            wc = targetPrayer.getWidgetChild();
        }

        if (wc == null) return;
        wc.interact();
    }
}

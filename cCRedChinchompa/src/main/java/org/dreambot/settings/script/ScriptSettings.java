package org.dreambot.settings.script;

import lombok.Getter;
import lombok.Setter;

public class ScriptSettings {
    @Setter
    @Getter
    private static SettingsData settingsData = new SettingsData(
            150,
            10,
            15_000_000,
            24,
            500_000,
            false
    );

    public static int getMinGP() {
        return settingsData.getInitialGP();
    }

    public static long getMuleOffTime() {
        return (long) settingsData.hoursUntilMuleOff * 60 * 60 * 1000;
    }

    public static int getMuleRemainder() {
        return settingsData.getGpRemainingAfterMuling();
    }

    public static boolean stopAfterFishing() {
        return settingsData.isStopAfterFishing();
    }
}

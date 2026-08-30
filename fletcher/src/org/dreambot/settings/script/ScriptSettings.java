package org.dreambot.settings.script;

import lombok.Getter;
import lombok.Setter;

public class ScriptSettings {
    @Setter
    @Getter
    private static SettingsData settingsData = new SettingsData(
            12_000_000,
            3_000_000,
            false,
            false,
            -1,
            3
    );

    public static int getMinGP() {
        return settingsData.getInitialGP();
    }

    public static int getMuleRemainder() {
        return settingsData.getGpRemainingAfterMuling();
    }

    public static boolean makeMagics() {
        return settingsData.isStringMagics();
    }

    public static boolean fletchFromLogs() {
        return settingsData.fletchFromLogs;
    }

    public static int forceWorld() {
        return settingsData.forceWorld;
    }


    public static int hoursUntilMuleOff() {
        return settingsData.hoursUntilMuleOff >= 0 ? settingsData.hoursUntilMuleOff : 8;
    }
}

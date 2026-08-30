package org.dreambot.settings.script;

import lombok.Getter;
import lombok.Setter;

public class ScriptSettings {
    @Setter
    @Getter
    private static SettingsData settingsData = new SettingsData();

    public static int getMinGP() {
        return settingsData.getInitialGP();
    }


    public static long getMuleOffTime() {
        return (long) settingsData.hoursUntilMuleOff * 60 * 60 * 1000;
    }

    public static int getMuleRemainder() {
        return settingsData.getGpRemainingAfterMuling();
    }

}

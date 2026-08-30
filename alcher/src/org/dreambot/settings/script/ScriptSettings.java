package org.dreambot.settings.script;

import lombok.Getter;
import lombok.Setter;

public class ScriptSettings {
    @Setter
    @Getter
    private static SettingsData settingsData = new SettingsData();


    public static int getMinGP() {
        return settingsData.initalGp;
    }
}

package org.dreambot.settings.script;

import lombok.Getter;
import lombok.Setter;

public class ScriptSettings {
    @Setter @Getter
    private static SettingsData settingsData = new SettingsData(100);

    public static int getChinMax() {
        return settingsData.getChinMax();
    }
}

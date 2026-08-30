package org.dreambot.settings.script;

import lombok.Getter;
import lombok.Setter;

public class ScriptSettings {
    @Setter
    @Getter
    private static SettingsData settingsData = new SettingsData(true, false);

    public static boolean shouldBuryBones() {
        return settingsData.isBuryBones();
    }

    public static boolean trainTo73() {
        return settingsData.isTrainTo73();
    }
}

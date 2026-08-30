package org.dreambot.settings.script;

import lombok.Getter;
import lombok.Setter;
import org.dreambot.fractals.data.ItemID;

public class ScriptSettings {
    @Setter
    @Getter
    private static SettingsData settingsData = new SettingsData();

    public static int getMinGP() {
        return settingsData.initalGp;
    }

    public static int getFoodId() {
        return settingsData.foodId > 0 ? settingsData.foodId : ItemID.SHARK;
    }
}

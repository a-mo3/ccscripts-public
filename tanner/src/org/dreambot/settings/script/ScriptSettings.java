package org.dreambot.settings.script;

import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.data.ItemID;

public class ScriptSettings {
    @Setter
    @Getter
    private static SettingsData settingsData = new SettingsData();


    public static int getMinGP() {
        return settingsData.initalGp;
    }

    public static int getHideID() {
        String col = settingsData.hideColor;
        if (col.toLowerCase().contains("green")) return ItemID.GREEN_DRAGONHIDE;
        if (col.toLowerCase().contains("blue")) return ItemID.BLUE_DRAGONHIDE;
        if (col.toLowerCase().contains("black")) return ItemID.BLACK_DRAGONHIDE;
        if (col.toLowerCase().contains("red")) return ItemID.RED_DRAGONHIDE;

        Logger.info("Unknown hide color set, defaulting to green" + settingsData.hideColor);
        return ItemID.GREEN_DRAGONHIDE;
    }
}

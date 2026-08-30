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

    public static int getPrayerTarget() {
        return settingsData.prayerTarget >= 43 ? settingsData.prayerTarget : 43;
    }

    public static int getRangeTarget() {
        return settingsData.rangedTarget >= 61 ? settingsData.rangedTarget : 61;
    }

    public static boolean isBlack() {
        return settingsData.dragonMode == DragonMode.BLACK;
    }

    public static boolean isRed() {
        return settingsData.dragonMode == DragonMode.RED;
    }

    public static boolean isBlue() {
        return settingsData.dragonMode == DragonMode.BLUE;
    }

    public static int getChestID() {
        return settingsData.chestID <= 0 ? ItemID.MONKS_ROBE_TOP : settingsData.chestID;
    }

    public static int getLegID() {
        return settingsData.legID <= 0 ? ItemID.MONKS_ROBE : settingsData.legID;
    }

    public static int getBootID() {
        return settingsData.bootID <= 0 ? ItemID.SNAKESKIN_BOOTS : settingsData.bootID;
    }

    public static int getHatID() {
        return settingsData.hatID <= 0 ? ItemID.SNAKESKIN_BANDANA : settingsData.hatID;
    }
}

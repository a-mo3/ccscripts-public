package org.dreambot.settings.script;

import lombok.Getter;
import lombok.Setter;

public class ScriptSettings {
    @Setter
    @Getter
    private static SettingsData settingsData = new SettingsData(
//            false,
            12_000_000,
            2000,
            2_000_000,
            8,
            5,
            25,
            25,
            55,
            AntiPkMode.SKULLED_OR_EQUIPMENT,
            StaffMode.TRIDENT,
            true,
            1000,
            false,
            true,
            true,
            500,
//            false,
            -1,
            -1,
            -1,
            true,
            false,
            false,
            true,
            false,
            true,
            false,
            0,
            false
    );

    public static int getFireBoltCharges() {
        return settingsData.fireBoltCharges < 50 ? 500 : settingsData.fireBoltCharges;
    }

    public static int getMinLootValue() {
        if (settingsData.minLootValue == 0) return 1000;
        return settingsData.minLootValue;
    }

    public static int getMinGP() {
        return settingsData.initalGp;
    }
}

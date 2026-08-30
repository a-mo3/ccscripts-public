package org.dreambot.settings.script;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ScriptSettings {
    @Setter
    @Getter
    private static SettingsData settingsData = new SettingsData(5000,
            10_000_000,
            20,
            20,
            2,
            2,
            12,
            1_000_000);

    public static int getMinGP() {
        return settingsData.getInitialGP();
    }

    public static int getBeadBuyPrice() {
        return settingsData.getBeadBuyPrice();
    }

    public static int getHpTarget() {
        return settingsData.getHpTarget();
    }

    public static int getDefenceTarget() {
        return settingsData.getDefenceTarget();
    }

    public static int getEnergyPotions() {
        return settingsData.getEnergyPotions();
    }

    public static int getSalmons() {
        return settingsData.getSalmons();
    }

    public static long getMuleOffTime() {
        return (long) settingsData.hoursUntilMuleOff * 60 * 60 * 1000;
    }

    public static int getMuleRemainder() {
        return settingsData.getGpRemainingAfterMuling();
    }
}

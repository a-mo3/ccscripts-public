package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.settings.SettingsLoader;

public class FtpCraftingSettings {
    private static FtpCraftingData data;

    public static FtpCraftingData getData() {
        if (data == null) {
            SettingsLoader<FtpCraftingData> loader = new SettingsLoader<>(FtpCraftingData.class);
            data = loader.loadFile("settings.json", new FtpCraftingData());
        }

        return data;
    }

    public static class FtpCraftingData {
        @SerializedName("hoursUntilMuleOff")
        public int hoursUntilMuleOff = 8;
        @SerializedName("moneyLeftAfterMuling")
        public int moneyLeftAfterMuling = 30_000;
        @SerializedName("stopAfterUnrestricted")
        public boolean stage = false;
        @SerializedName("ignorePlaytime")
        public boolean ignorePlaytime = false;
        @SerializedName("forceCrafting")
        public CraftingModes forceCrafting = CraftingModes.EMERALD_AMULETS_U;
        @SerializedName("noMuleMode")
        public boolean noMuleMode = false;
    }
}

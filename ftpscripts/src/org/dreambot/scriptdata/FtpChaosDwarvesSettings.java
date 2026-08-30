package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.settings.SettingsLoader;

public class FtpChaosDwarvesSettings {
    private static Data data;

    public static Data getData() {
        if (data == null) {
            SettingsLoader<Data> loader = new SettingsLoader<>(Data.class);
            data = loader.loadFile("settings.json", new Data());
        }

        return data;
    }

    public static class Data {
        @SerializedName("hoursUntilMuleOff")
        public int hoursUntilMuleOff = 8;
        @SerializedName("moneyLeftAfterMuling")
        public int moneyLeftAfterMuling = 30_000;
        @SerializedName("stopAfterUnrestricted")
        public boolean stage = false;
        @SerializedName("whenStoppingIgnorePlaytime")
        public boolean ignorePlaytime = false;
        @SerializedName("minCombatLevel")
        public int minCombatLevel = 45;
        @SerializedName("noMuleMode")
        public boolean noMuleMode = false;
    }
}

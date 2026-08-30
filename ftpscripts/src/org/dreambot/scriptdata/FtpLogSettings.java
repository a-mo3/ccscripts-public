package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.settings.SettingsLoader;

public class FtpLogSettings {
    private static FtpLogData data;

    public static FtpLogData getData() {
        if (data == null) {
            SettingsLoader<FtpLogData> loader = new SettingsLoader<>(FtpLogData.class);
            data = loader.loadFile("settings.json", new FtpLogData());
        }

        return data;
    }

    public static class FtpLogData {
        @SerializedName("hoursUntilMuleOff")
        public int hoursUntilMuleOff = 8;
        @SerializedName("moneyLeftAfterMuling")
        public int moneyLeftAfterMuling = 30_000;
        @SerializedName("restockQuantity")
        public int restockQuantity = 1200;
        @SerializedName("stopAfterUnrestricted")
        public boolean stage = false;
        @SerializedName("ignorePlaytime")
        public boolean ignorePlaytime = false;
        @SerializedName("noMuleMode")
        public boolean noMuleMode = false;
    }
}

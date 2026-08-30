package org.dreambot.muling.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.dreambot.settings.SettingsLoader;

@Getter
public class MuleSettings {
    private static MuleSettings muleSettings = null;

    @Expose
    @SerializedName("muleIPAddress")
    String ipAddress = "localhost";
    @Expose
    @SerializedName("mulePort")
    int port = 9696;

    public static String getIPAddress() {
        if (muleSettings == null) {
            SettingsLoader<MuleSettings> muleSettingsLoader = new SettingsLoader<>(MuleSettings.class);
            muleSettings = muleSettingsLoader.loadFile("muleSettings.json", new MuleSettings());
        }
        return muleSettings.ipAddress;
    }

    public static String getMuleAddress() {
        if (muleSettings == null) {
            SettingsLoader<MuleSettings> muleSettingsLoader = new SettingsLoader<>(MuleSettings.class);
            muleSettings = muleSettingsLoader.loadFile("muleSettings.json", new MuleSettings());
        }
        return muleSettings.ipAddress + ":" + muleSettings.port;
    }
}

package org.dreambot.muling.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.dreambot.settings.SettingsLoader;

@Getter
public class MuleSettings {
    private static MuleSettings muleSettings = null;

    public MuleSettings() {
    }

    public MuleSettings(int port) {
        this.port = port;
    }

    @Expose
    @SerializedName("muleIPAddress")
    String ipAddress = "localhost";
    @Expose
    @SerializedName("mulePort")
    int port = 9696;
    @SerializedName("reportMulingAnalytics")
    boolean reportMulingAnalytics = false;

    public static String getIPAddress() {
        if (muleSettings == null) {
            SettingsLoader<MuleSettings> muleSettingsLoader = new SettingsLoader<>(MuleSettings.class);
            muleSettings = muleSettingsLoader.loadFile("muleSettings.json", new MuleSettings());
        }
        return muleSettings.ipAddress;
    }


    public static int getPort() {
        if (muleSettings == null) {
            SettingsLoader<MuleSettings> muleSettingsLoader = new SettingsLoader<>(MuleSettings.class);
            muleSettings = muleSettingsLoader.loadFile("muleSettings.json", new MuleSettings());
        }
        return muleSettings.port;
    }

    public static String getMuleAddress() {
        if (muleSettings == null) {
            SettingsLoader<MuleSettings> muleSettingsLoader = new SettingsLoader<>(MuleSettings.class);
            muleSettings = muleSettingsLoader.loadFile("muleSettings.json", new MuleSettings());
        }
        return muleSettings.ipAddress + ":" + muleSettings.port;
    }

    public static boolean isAnalyticsOn() {
        return false;
//        if (muleSettings == null) {
//            SettingsLoader<MuleSettings> muleSettingsLoader = new SettingsLoader<>(MuleSettings.class);
//            muleSettings = muleSettingsLoader.loadFile("muleSettings.json", new MuleSettings());
//        }
//        return muleSettings.reportMulingAnalytics;
    }

    // returns what it was set to
    public static boolean toggleAnalytics() {
        SettingsLoader<MuleSettings> muleSettingsLoader = new SettingsLoader<>(MuleSettings.class);
        if (muleSettings == null) {
            muleSettings = muleSettingsLoader.loadFile("muleSettings.json", new MuleSettings());
        }
        muleSettings.reportMulingAnalytics = !muleSettings.reportMulingAnalytics;
        muleSettingsLoader.saveFile("muleSettings.json", muleSettings);
        return muleSettings.reportMulingAnalytics;
    }

    public static void setPort(int port) {
        muleSettings = new MuleSettings(port);
    }
}

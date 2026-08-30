package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class RoguesChestSettings {
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
    @SerializedName("lootThreshold")
    public int lootThreshold = 150_000;
    @SerializedName("alwaysDoWitchesHouse")
    public boolean alwaysWitchesHouse = false;
    @SerializedName("doHardDiary")
    public boolean doHardWildernessDiary = false;
    @SerializedName("deadmanMode")
    public boolean dmm = false;
}
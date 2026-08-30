package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class TurothSettings {
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("preSlayerCombatTarget")
    public int preSlayerCombatTarget = 30;
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;
    @SerializedName("minLootValue")
    public int minLootValue = 1000;

}
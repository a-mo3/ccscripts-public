package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class SpectreSettings {
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("preSlayerCombatTarget")
    public int preSlayerCombatTarget = 30;
    @SerializedName("postSlayerCombatTarget")
    public int postSlayerCombatTarget = 75;
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
}

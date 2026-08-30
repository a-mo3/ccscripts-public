package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class GargoyleSettings {
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
    @SerializedName("useGuthans")
    public boolean useGuthans = true;
    @SerializedName("unlockedAutoHammer")
    public boolean unlockedAutoHammer = false;
}

package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class WyrmSettings {
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("preSlayerCombatTarget")
    public int preSlayerCombatTarget = 30;
    @SerializedName("postSlayerCombatTarget")
    public int postSlayerCombatTarget = 60;
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
    @SerializedName("buryBones")
    public boolean buryBones = false;
    @SerializedName("noPrayMode")
    public boolean noPrayMode = false;
}

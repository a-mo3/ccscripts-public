package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class SulphurSettings {
    @SerializedName("preSlayerCombatTarget")
    public int preSlayerCombatTarget = 30;
    @SerializedName("postSlayerCombatTarget")
    public int postSlayerCombatTarget = 75;
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;
    @SerializedName("herbloreTarget")
    public int herbloreTarget = 38;
    @SerializedName("minLootValue")
    public int minLootValue = 300;
}

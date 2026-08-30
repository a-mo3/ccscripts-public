package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class SpirtutalMagesSettings {
    @SerializedName("preSlayerCombatTarget")
    public int preSlayerCombatTarget = 30;
    @SerializedName("postSlayerCombatTarget")
    public int postSlayerCombatTarget = 75;
    @SerializedName("prayerTarget")
    public int prayerTarget = 43;
}

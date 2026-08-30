package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class KuraskSettings {
    @SerializedName("preSlayerCombatTarget")
    public int preSlayerCombatTarget = 30;
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;
    @SerializedName("flickMode")
    public boolean flickMode = true;
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
}
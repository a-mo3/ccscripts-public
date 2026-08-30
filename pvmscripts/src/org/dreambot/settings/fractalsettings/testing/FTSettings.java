package org.dreambot.settings.fractalsettings.testing;

import com.google.gson.annotations.SerializedName;

public class FTSettings {
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("slayerLvl")
    public int slayerLvl = 30;
    @SerializedName("slayDragons")
    public boolean slayDragons = false;
    @SerializedName("slayWithWhip")
    public boolean slayWithWhip = true;
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;
    @SerializedName("prayInPOH")
    public boolean prayInPOH = true;
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
}

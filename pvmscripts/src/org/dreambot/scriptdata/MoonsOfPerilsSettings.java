package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class MoonsOfPerilsSettings {
    @SerializedName("preSlayerCombatTarget")
    public int preSlayerCombatTarget = 30;
    @SerializedName("postSlayerCombatTarget")
    public int postSlayerCombatTarget = 75;
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;
    @SerializedName("herbloreTarget")
    public int herbloreTarget = 38;
    @SerializedName("cookingTarget")
    public int cookingTarget = 60;
    @SerializedName("fishingTarget")
    public int fishingTarget = 60;

    @SerializedName("killBloodMoon")
    public boolean killBloodMoon = true;
    @SerializedName("killBlueMoon")
    public boolean killBlueMoon = true;
    @SerializedName("killEclipseMoon")
    public boolean killEclipse = true;

    @SerializedName("oneTickFlick")
    public boolean prayerFlicking = true;

    @SerializedName("useDualMacuahuitl")
    public boolean useDualMacs = false;
    @SerializedName("maxPing")
    public int maxPing = 250;

}

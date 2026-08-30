package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class MasterFarmerSettings {
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
    @SerializedName("getRoguesOutfit")
    public boolean getRoguesOutfit = true;
    @SerializedName("alwaysDoWitchesHouse")
    public boolean alwaysWitchesHouse = false;
    @SerializedName("trainTheivingUntil")
    public int thievingTrainingTarget = 55;
    @SerializedName("farmingTarget")
    public int farmingTarget = 85;
}
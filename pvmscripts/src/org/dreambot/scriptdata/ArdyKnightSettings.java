package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class ArdyKnightSettings {
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
    @SerializedName("alwaysDoWitchesHouse")
    public boolean alwaysWitchesHouse = false;
    @SerializedName("trainTheivingUntil")
    public int thievingTrainingTarget = 55;
    @SerializedName("doPaladinsAfterLevel")
    public int paladinsAfter = 70;
}
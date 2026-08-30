package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class ZombieChestSettings {
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("initalGP")
    public int initalGP = 16_000_000;
    @SerializedName("moneyLeftAfterMuling")
    public int moneyLeftAfterMuling = 1_000_000;
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
    @SerializedName("keysPerTrip")
    public int keysPerTrip = 15;
    @SerializedName("restockMultiple")
    public int restockMultiplier = 2;
    @SerializedName("keyBuyPrice")
    public int keyBuyPrice = 20_000;
}

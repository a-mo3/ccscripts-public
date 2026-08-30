package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class SunlightAntelopeSettings {
    @SerializedName("fletchBolts")
    public boolean fletchBolts = true;
    @SerializedName("hitpointsTarget")
    public int hitpointsTarget = 10;
    @SerializedName("defenceTarget")
    public int defenceTarget = 1;
    @SerializedName("agilityTarget")
    public int agilityTarget = 1;
}

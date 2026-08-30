package org.dreambot.behaviour.misc;

import com.google.gson.annotations.SerializedName;
import org.dreambot.settings.ui.nui.UIExplanation;

public class MuleOffSettings {
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 6;
    @SerializedName("hourVariation")
    @UIExplanation("mule off +- this many hours")
    public int hourVariation = 0;
    @SerializedName("moneyLeftAfterMuling")
    public int moneyLeftAfterMuling = 1_000_000;
    @SerializedName("muleIPAddress")
    public String ipAddress = "localhost";
    @SerializedName("mulePort")
    public int port = 9696;
    @SerializedName("disableMuleOn")
    public boolean disableMuleOn = false;
    @SerializedName("minMuleOnAmount")
    public int minMuleOnAmount = 250_000;
}

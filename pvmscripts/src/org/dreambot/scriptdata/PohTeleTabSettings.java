package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.teletabs.poh.PohTeleTabOption;

public class PohTeleTabSettings {
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("teleportTab")
    public PohTeleTabOption option = PohTeleTabOption.VARROCK;
    @SerializedName("agilityReq")
    public int agility = 0;
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
    @SerializedName("blacklistedHouses")
    public String blacklistedHouses = "";
}

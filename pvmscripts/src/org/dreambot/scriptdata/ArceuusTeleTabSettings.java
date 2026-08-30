package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.teletabs.ArceuusTeleTabOption;

public class ArceuusTeleTabSettings {
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("teleportTab")
    public ArceuusTeleTabOption option = ArceuusTeleTabOption.ARCEUUS_BARROWS;
    @SerializedName("agilityReq")
    public int agility = 0;
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
}

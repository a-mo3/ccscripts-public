package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.training.hunter.GrayChinSpot;

public class GrayChinSettings {
    @SerializedName("minLootValue")
    public int minLootValue = 2500;
    @SerializedName("crashOthers")
    public boolean crash = false; // if you leave the chasm or crash other people after entering spindel
    @SerializedName("grayChinSpot")
    public GrayChinSpot spot = GrayChinSpot.KOUREND;
}

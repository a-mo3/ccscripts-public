package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.gorillas.GorillaLoadout;

public class GorillaSettings {
    @SerializedName("loadout")
    public GorillaLoadout gorillaLoadout = GorillaLoadout.SUNLIGHT_WHIP;
    @SerializedName("flickPrayers")
    public boolean flickPrayer = false;
}

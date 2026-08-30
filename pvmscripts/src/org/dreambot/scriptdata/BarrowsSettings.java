package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.barrows.BarrowsLoadout;

public class BarrowsSettings {
    @SerializedName("rangeTarget")
    public int rangeTarget = 75;
    @SerializedName("magicTarget")
    public int magicTarget = 75;

    @SerializedName("prayerTarget")
    public int prayerTarget = 43;

    @SerializedName("loadout")
    public BarrowsLoadout loadout = BarrowsLoadout.AIR_MSB;
}

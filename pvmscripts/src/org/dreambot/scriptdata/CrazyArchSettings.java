package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.chaosfanatic.ChaosFanaticLoadout;
import org.dreambot.behaviour.method.crazyarch.CrazyLoadout;

public class CrazyArchSettings {
    @SerializedName("loadout")
    public CrazyLoadout loadout = CrazyLoadout.MAGE_TRIDENT;
    @SerializedName("magicTarget")
    public int magicTarget = 75;

    @SerializedName("rangeTarget")
    public int rangeTarget = 0;

    @SerializedName("prayerTarget")
    public int prayerTarget = 37;

    @SerializedName("flickPrayer")
    public boolean flickPrayer = true;

    @SerializedName("leaveWhenInventoryIsWorth")
    public int leaveAmount = 150_000;
}
package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.chaoselemental.ChaosElementalLoadout;
import org.dreambot.behaviour.method.chaosfanatic.ChaosFanaticLoadout;

public class ChaosElementalSettings {
    @SerializedName("loadout")
    public ChaosElementalLoadout loadout = ChaosElementalLoadout.MSB_AMETHYST;
    @SerializedName("rangeTarget")
    public int rangeTarget = 75;
    @SerializedName("prayerTarget")
    public int prayerTarget = 37;
    @SerializedName("flickPrayer")
    public boolean flickPrayer = true;

    @SerializedName("leaveWhenInventoryIsWorth")
    public int leaveAmount = 150_000;
//    @SerializedName("suicideToBank")
//    public boolean suicideToBank = true;

}
package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.crazyarch.CrazyLoadout;
import org.dreambot.scriptdata.loadouts.AviansieLoadout;

/**
 * crazy archeologist
 */
public class CrazySettings {
    @SerializedName("prayerTarget")
    public int prayerTarget = 45;

    @SerializedName("magicTarget")
    public int magicTarget = 75;
    @SerializedName("meleeTarget")
    public int meleeTarget = 0;

    @SerializedName("loadout")
    public CrazyLoadout loadout = CrazyLoadout.MAGE_TRIDENT;

    @SerializedName("flick")
    public boolean flick = true;

    @SerializedName("exitLootValue")
    public int exitLootValue = 150_000;
}

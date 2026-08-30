package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.scriptdata.loadouts.AviansieLoadout;

public class AvianSettings {
    @SerializedName("rangeTarget")
    public int rangeTarget = 70;
    @SerializedName("defTarget")
    public int defTarget = 80;
    @SerializedName("prayerTarget")
    public int prayerTarget = 45;
    @SerializedName("minLootValue")
    public int minLootValue = 500;
    @SerializedName("loadout")
    public AviansieLoadout loadout = AviansieLoadout.DEFAULT;

    @SerializedName("flick")
    public boolean flick;
    @SerializedName("doHardDiary")
    public boolean doHardWildernessDiary = false;
}

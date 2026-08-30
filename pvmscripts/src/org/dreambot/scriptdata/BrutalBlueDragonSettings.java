package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.bluedragons.BlueDragonLoadout;
import org.dreambot.fractals.util.CombatMode;

public class BrutalBlueDragonSettings {
    @SerializedName("minLootValue")
    public int minLootValue = 1000;

    @SerializedName("attackTarget")
    public int attackTarget = 0;
    @SerializedName("strengthTarget")
    public int strengthTarget = 0;
    @SerializedName("defenceTarget")
    public int defenceTarget = 0;

    @SerializedName("rangeTarget")
    public int rangeTarget = 0;
    @SerializedName("rangeDefTarget")
    public int rangeDefTarget = 0;

    @SerializedName("magicTarget")
    public int magicTarget = 80;
    @SerializedName("magicDefTarget")
    public int magicDefTarget = 0;

    @SerializedName("prayerTarget")
    public int prayerTarget = 43;

    @SerializedName("loadout")
    public BlueDragonLoadout loadout = BlueDragonLoadout.MAGIC_WATER;
}

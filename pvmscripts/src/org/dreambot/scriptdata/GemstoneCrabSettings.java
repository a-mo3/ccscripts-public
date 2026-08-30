package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.gemstone.GemstoneCrabMagicLoadout;
import org.dreambot.behaviour.method.gemstone.GemstoneCrabMeleeLoadout;
import org.dreambot.behaviour.method.gemstone.GemstoneCrabRangeLoadout;

public class GemstoneCrabSettings {
    @SerializedName("prayerTarget")
    public int prayerTarget = 0;

    @SerializedName("meleeLoadout")
    public GemstoneCrabMeleeLoadout meleeLoadout = GemstoneCrabMeleeLoadout.OBBY_SARA;

    @SerializedName("customMeleeLoadout")
    public String customMeleeLoadout = "";

    @SerializedName("attackTarget")
    public int attackTarget = 70;
    @SerializedName("strengthTarget")
    public int strengthTarget = 70;
    @SerializedName("defenceTarget")
    public int defenceTarget = 70;

    @SerializedName("rangeLoadout")
    public GemstoneCrabRangeLoadout rangeLoadout = GemstoneCrabRangeLoadout.KNIVES;
    @SerializedName("customRangeLoadout")
    public String customRangeLoadout = "";
    @SerializedName("rangeTarget")
    public int rangeTarget = 70;
    @SerializedName("rangeDefenceTarget")
    public int rangeDefenceTarget = 40;

    @SerializedName("magicLoadout")
    public GemstoneCrabMagicLoadout magicLoadout = GemstoneCrabMagicLoadout.AIR_STAFF;
    @SerializedName("customMagicLoadout")
    public String customMagicLoadout = "";
    @SerializedName("magicTarget")
    public int magicTarget = 70;
    @SerializedName("magicDefenceTarget")
    public int magicDefenceTarget = 40;
}

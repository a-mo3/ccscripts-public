package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.greendragon.GDKLoadout;
import org.dreambot.behaviour.method.greendragon.GDKLocation;

public class GDKSettings {
    @SerializedName("useLootingBag")
    public boolean useLootingBag = false;
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
    @SerializedName("prayerTarget")
    public int prayerTarget = 43;
    @SerializedName("F2PMeleeTraining")
    public boolean ftpMeleeTraining = false;
    @SerializedName("attackTarget")
    public int attackTarget = 0;
    @SerializedName("strengthTarget")
    public int strengthTarget = 0;
    @SerializedName("defenceTarget")
    public int defenceTarget = 40;
    @SerializedName("F2PRangeTraining")
    public boolean ftpRangeTraining = false;
    @SerializedName("rangeTarget")
    public int rangeTarget = 70;
    @SerializedName("F2PMagicTraining")
    public boolean ftpMagicTraining = false;
    @SerializedName("magicTarget")
    public int magicTarget = 0;
    @SerializedName("prayMelee")
    public boolean prayMelee = true;
    @SerializedName("avoidCompetition")
    public boolean avoidCompetition = true;
    @SerializedName("exitLootValue")
    public int exitLootValue = 150_000;
    @SerializedName("minStrBoost")
    public int minBoost = 4;
    @SerializedName("eatAbove")
    public int eatAbove = 50;
    @SerializedName("location")
    public GDKLocation location = GDKLocation.UNDER_RUINS;
    @SerializedName("gdkMethod")
    public GDKLoadout gdkLoadout = GDKLoadout.URSINE;
    @SerializedName("etherRechargeQuantity")
    public int etherRechargeQuantity = 350;
    @SerializedName("enforceMaxGP")
    public boolean enforceMaxGP = false;
    @SerializedName("maxGP")
    public int maxGP = 15_000_000;
}

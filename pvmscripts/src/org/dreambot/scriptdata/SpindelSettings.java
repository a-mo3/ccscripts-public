package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.spindel.SpindelLoadout;

public class SpindelSettings {
    @SerializedName("rangeTarget")
    public int rangeTarget = 80;
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;
    @SerializedName("attackTarget")
    public int atkTarget = 70;
    @SerializedName("strengthTarget")
    public int strTarget = 80;
    @SerializedName("defenceTarget")
    public int defTarget = 60;
    @SerializedName("minLootValue")
    public int minLootValue = 2500;
    @SerializedName("loadout")
    public SpindelLoadout loadout = SpindelLoadout.VIGGORAS_DHIDE;
    @SerializedName("crashOthers")
    public boolean crash = true; // if you leave the chasm or crash other people after entering spindel
    @SerializedName("useCombatPrayer")
    public boolean boostPray = true; // eagle eye / piety or their lesser variants
    @SerializedName("exitLootValue")
    public int exitLootValue = 150_000;
    @SerializedName("maxKillsPerRun")
    public int maxKillsPerRun = 10;
    @SerializedName("useLootingBag")
    public boolean useLootingBag = false;
    @SerializedName("EmptyCoffer")
    public boolean emptyCoffer = true;
    @SerializedName("etherRechargeQuantity")
    public int etherRechargeQuantity = 350;
}

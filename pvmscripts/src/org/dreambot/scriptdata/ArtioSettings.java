package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.artio.ArtioLoadout;

public class ArtioSettings {
    @SerializedName("rangeTarget")
    public int rangeTarget = 80;
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;
    @SerializedName("magicTarget")
    public int magicTarget = 60;
    @SerializedName("attackTarget")
    public int atkTarget = 70;
    @SerializedName("strengthTarget")
    public int strTarget = 80;
    @SerializedName("defenceTarget")
    public int defTarget = 60;
    @SerializedName("minLootValue")
    public int minLootValue = 2500;
    @SerializedName("loadout")
    public ArtioLoadout loadout = ArtioLoadout.ACCURSED;
    @SerializedName("crashOthers")
    public boolean crash = true;
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
    @SerializedName("useSuperCombat")
    public boolean useSuperCombat = false;
}

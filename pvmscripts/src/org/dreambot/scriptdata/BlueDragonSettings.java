package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.bluedragons.BlueDragonLoadout;
import org.dreambot.fractals.util.CombatMode;

public class BlueDragonSettings {
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
    @SerializedName("F2PMeleeTraining")
    public boolean ftpMeleeTraining = false;
    @SerializedName("attackTarget")
    public int attackTarget = 30;
    @SerializedName("strengthTarget")
    public int strengthTarget = 30;
    @SerializedName("defenceTarget")
    public int defenceTarget = 30;
    @SerializedName("F2PRangeTraining")
    public boolean ftpRangeTraining = false;
    @SerializedName("rangeTarget")
    public int rangeTarget = 0;
    @SerializedName("F2PMagicTraining")
    public boolean ftpMagicTraining = false;
    @SerializedName("magicTarget")
    public int magicTarget = 60;
    @SerializedName("loadout")
    public BlueDragonLoadout loadout = BlueDragonLoadout.MAGIC_WATER;
    @SerializedName("userCombatPrayers")
    public boolean useCombatPrayers = true;
    @SerializedName("prayerTarget")
    public int prayerTarget = 43;
    @SerializedName("trainAlchingWhileAgility")
    public boolean agilityAlch = true;

    public CombatMode getCombatMode() {
        return loadout.getCombatMode();
    }
}

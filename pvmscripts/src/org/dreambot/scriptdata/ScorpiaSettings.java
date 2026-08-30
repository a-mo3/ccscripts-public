package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.scorpia.ScorpiaLoadout;

public class ScorpiaSettings {
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;
    @SerializedName("loadout")
    public ScorpiaLoadout loadout = ScorpiaLoadout.TRIDENT;
    @SerializedName("magicTarget")
    public int magicTarget = 79;
    @SerializedName("useCombatPrayer")
    public boolean boostPray = true; // magic boost
    @SerializedName("exitLootValue")
    public int exitLootValue = 120_000;
}

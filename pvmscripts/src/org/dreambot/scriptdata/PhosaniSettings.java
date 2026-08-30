package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.nightmare.PhosaniLoadout;

public class PhosaniSettings {
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
    public PhosaniLoadout loadout = PhosaniLoadout.BLOOD_MOON_KIT;
    @SerializedName("useCombatPrayer")
    public boolean boostPray = true; // eagle eye / piety or their lesser variants
}
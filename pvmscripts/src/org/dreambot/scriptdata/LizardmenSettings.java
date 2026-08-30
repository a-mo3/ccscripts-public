package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.lizardmen.LizardRoom;
import org.dreambot.behaviour.method.lizardmen.LizardmenLoadout;
import org.dreambot.settings.ui.nui.UIExplanation;

public class LizardmenSettings {
    @SerializedName("loadout")
    public LizardmenLoadout loadout = LizardmenLoadout.D_HIDES_ROSEWOOD_BLOWPIPE;
    @SerializedName("shouldFlick")
    @UIExplanation("If the script should flick prayers when killing druids")
    public boolean shouldFlick = true;
    @SerializedName("prayerTarget")
    @UIExplanation("Script will get atleast 43 for prot melee, combat ring")
    public int prayerTarget = 43;
    @SerializedName("rangeTarget")
    public int rangeTarget = 75;
    @SerializedName("rangeDefTarget")
    public int defTarget = 40;
    @SerializedName("attackTarget")
    public int attackTarget = 0;
    @SerializedName("room")
    public LizardRoom room = LizardRoom.WEST;

}
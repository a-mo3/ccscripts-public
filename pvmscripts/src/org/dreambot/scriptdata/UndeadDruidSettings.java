package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.undeaddruids.UndeadDruidLoadout;
import org.dreambot.settings.ui.nui.UIExplanation;

public class UndeadDruidSettings {
    @SerializedName("loadout")
    public UndeadDruidLoadout loadout = UndeadDruidLoadout.D_HIDES_RANGE;
    @SerializedName("shouldFlick")
    @UIExplanation("If the script should flick prayers when killing druids")
    public boolean shouldFlick = true;
    @SerializedName("prayerTarget")
    public int prayerTarget = 37;
    @SerializedName("rangeTarget")
    public int rangeTarget = 75;
    @SerializedName("attackTarget")
    public int attackTarget = 0;

}
package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.api.methods.world.Location;
import org.dreambot.behaviour.method.huey.HueyLoadout;
import org.dreambot.behaviour.method.scorpia.ScorpiaLoadout;

public class HueycoatlSettings {
    @SerializedName("prayerTarget")
    public int prayerTarget = 43;

    @SerializedName("magicTarget")
    public int magicTarget = 0;

    @SerializedName("meleeBaseTarget")
    public int meleeTarget = 75;

    @SerializedName("loadout")
    public HueyLoadout loadout = HueyLoadout.FULL_BLOOD;

    @SerializedName("useInstanceWhenSolo")
    public boolean instanceSolo = true;
    @SerializedName("useInstanceWhenTeam")
    public boolean instanceTeam = false;
    @SerializedName("teamSize")
    public int teamSize = 5;

    @SerializedName("useBurningClawsSpec")
    public boolean useBurningClawsSpec = false;

    // disables protection prayer flicking instead just camps pray for ~a hundred game cycles before it hits you
    @SerializedName("safePrayAgainstProjectiles")
    public boolean safePray = false;

    @SerializedName("worldRegionPreference")
    public Location regionPreference = Location.GERMANY;
}

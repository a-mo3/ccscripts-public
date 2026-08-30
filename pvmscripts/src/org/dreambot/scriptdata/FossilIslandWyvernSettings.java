package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.rdk.RedDragonLoadout;

public class FossilIslandWyvernSettings {

    @SerializedName("loadout")
    public RedDragonLoadout loadout = RedDragonLoadout.RANGE;
}

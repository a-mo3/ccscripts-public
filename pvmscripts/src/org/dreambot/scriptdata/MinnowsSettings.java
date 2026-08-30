package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.undeaddruids.UndeadDruidLoadout;
import org.dreambot.settings.ui.nui.UIExplanation;

public class MinnowsSettings {
    @SerializedName("stopAt82Fishing")
    @UIExplanation("When training in F2P after 82 fighting the script will stop, for building accounts")
    public boolean stopAfterFishing;

}
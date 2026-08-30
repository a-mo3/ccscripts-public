package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.venenatis.VenenatisLoadout;
import org.dreambot.behaviour.method.vetion.VetionLoadout;
import org.dreambot.behaviour.method.vetion.WildernessRunMode;
import org.dreambot.settings.WrappedLocation;
import org.dreambot.settings.ui.nui.UIExplanation;

public class VenenatisSettings {
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;

    @SerializedName("meleeTarget")
    public int meleeTarget = 70;

    @SerializedName("loadout")
    public VenenatisLoadout loadout = VenenatisLoadout.SGS_MONK;
    @SerializedName("runMode")
    public WildernessRunMode runMode = WildernessRunMode.RUN;
    @SerializedName("serverPreference")
    @UIExplanation("Do not select US_EAST or US_WEST, no servers exist with those regions")
    public WrappedLocation loc = WrappedLocation.USA;
    @SerializedName("discordPKWebhook")
    @UIExplanation("Send a screenshot to this url when a team gets into a PK situation")
    public String pkWebhook = "";

    @SerializedName("teamSize")
    public int teamSize = 10;
    @SerializedName("exitLootValue")
    public int exitLootValue = 150_000;

    @SerializedName("flickPrayers")
    public boolean flickPrayers = true;
}

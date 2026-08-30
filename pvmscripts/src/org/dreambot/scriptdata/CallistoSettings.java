package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.api.methods.world.Location;
import org.dreambot.behaviour.method.callisto.CallistoLoadout;
import org.dreambot.behaviour.method.callisto.leavecallisto.CallistoLeaveMode;
import org.dreambot.behaviour.method.vetion.WildernessRunMode;
import org.dreambot.settings.WrappedLocation;
import org.dreambot.settings.ui.nui.UIExplanation;

public class CallistoSettings {
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;

    @SerializedName("magicTarget")
    public int magicTarget = 79;
    @SerializedName("hpTarget")
    @UIExplanation("Will train combat if you are below this HP")
    public int hpTarget = 60;
    @SerializedName("rangeTarget")
    public int rangeTarget = 70;

    @SerializedName("loadout")
    public CallistoLoadout loadout = CallistoLoadout.DHIDE_TRIDENT;
    @SerializedName("runMode")
    public WildernessRunMode runMode = WildernessRunMode.RUN;
    @SerializedName("serverPreference")
    @UIExplanation("Do not select US_EAST or US_WEST, no servers exist with those regions")
    public WrappedLocation loc = WrappedLocation.USA;
    @SerializedName("discordPKWebhook")
    @UIExplanation("Send a screenshot to this url when a team gets into a PK situation")
    public String pkWebhook = "";

    @SerializedName("teamSize")
    @UIExplanation("How many accounts the script will group together before making a new team")
    public int teamSize = 10;
    @SerializedName("exitLootValue")
    @UIExplanation("When the inventory value is more than this the bot banks")
    public int exitLootValue = 150_000;


    @SerializedName("flickPrayers")
    public boolean flickPrayers = true;
    @SerializedName("leaveMode")
    public CallistoLeaveMode leaveMode = CallistoLeaveMode.DIRECT;


    @SerializedName("clanChat")
    @UIExplanation("This is not required to make the script work, it is for specific users with 3rd worlders on payroll")
    public String clanChat = "";
    @SerializedName("forceWord")
    @UIExplanation("This forces the server to assign on only 1 specific world, override location, for specific use cases")
    public int forceWorld = -1;
//    @SerializedName("worldHopWhenBank")
//    @UIExplanation("When banking will try to hop before leaving Callisto cave, during a death, to avoid pkers camping escape")
//    public boolean worldHopBank = false;
}

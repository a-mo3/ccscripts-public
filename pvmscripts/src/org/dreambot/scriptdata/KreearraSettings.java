package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.gwd.EcuKeyStrategy;
import org.dreambot.behaviour.method.gwd.GWDBoltPreference;
import org.dreambot.behaviour.method.gwd.RingPreference;
import org.dreambot.behaviour.method.gwd.kree.KreeLoadout;
import org.dreambot.settings.ui.nui.UIExplanation;

public class KreearraSettings {
    @SerializedName("loadout")
    public KreeLoadout loadout = KreeLoadout.BREW_KARILS_DCB_BLOWPIPE_DINHS;
    @SerializedName("brewQuantity")
    public int brewQuantity = 12;
    @SerializedName("restoreQuantity")
    public int restoreQuantity = 6;
    @SerializedName("prayerQuantity")
    public int prayerQuantity = 2;
    @SerializedName("staminaQuantity")
    public int staminaQuantity = 1;
    @SerializedName("blackChinQuantity")
    public int blackChinQuantity = 0;
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;
    @SerializedName("f2pRangeTarget")
    public int f2pRangeTarget = 0;
    @SerializedName("rangeTarget")
    public int rangeTarget = 92;
    @SerializedName("defenceTarget")
    public int defenceTarget = 75;
    @SerializedName("RingPreference")
    public RingPreference ringPreference = RingPreference.NONE;
    @SerializedName("boltPreference")
    public GWDBoltPreference boltPreference = GWDBoltPreference.DIAMOND;
    @SerializedName("killCountOrKeysStrategy")
    public EcuKeyStrategy ecuKeyStrategy = EcuKeyStrategy.KILL_COUNT;
    @SerializedName("safePrayerWhenGettingKC")
    public boolean safePrayerWhenGettingKC = true;
    @SerializedName("threeKeysPerTrip")
    public boolean threeKeysPerTrip = false;
    @SerializedName("useGroupMode")
    public boolean groupMode = true;
    @SerializedName("teamSize")
    public int teamSize = 10;

    @SerializedName("requiredTeammate")
    @UIExplanation("When entering a team it was for this many of your team to be outside awaiting or inside kree attacking before entering itself")
    public int requiredTeammates = 5;
}

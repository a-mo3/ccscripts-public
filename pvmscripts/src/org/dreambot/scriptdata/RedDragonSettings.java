package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.rdk.RedDragonLoadout;

public class RedDragonSettings {
    @SerializedName("agilityTarget")
    public int agilityTarget = 0; // used to a shortcut
    @SerializedName("ftpMagicTarget")
    public int ftpMagicTarget = 0;
    @SerializedName("ftpRangeTarget")
    public int ftpRangeTarget = 0;
    @SerializedName("ftpDefTarget")
    public int ftpDefTarget = 0;

    @SerializedName("magicTarget")
    public int magicTarget = 0;
    @SerializedName("rangeTarget")
    public int rangeTarget = 0;
    @SerializedName("defenceTarget")
    public int defenceTarget = 0;
    @SerializedName("prayerTarget")
    public int prayerTarget = 0;

    @SerializedName("loadout")
    public RedDragonLoadout loadout = RedDragonLoadout.RANGE;
}

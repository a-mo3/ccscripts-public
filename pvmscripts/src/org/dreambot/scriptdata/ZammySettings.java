package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.gwd.EcuKeyStrategy;
import org.dreambot.behaviour.method.gwd.GWDBoltPreference;
import org.dreambot.behaviour.method.gwd.RingPreference;
import org.dreambot.behaviour.method.gwd.zammy.ZammyLoadout;

public class ZammySettings {
    @SerializedName("loadout")
    public ZammyLoadout loadout = ZammyLoadout.KARILS_CB;
    @SerializedName("brewQuantity")
    public int brewQuantity = 7;
    @SerializedName("restoreQuantity")
    public int restoreQuantity = 9;
    @SerializedName("staminaQuantity")
    public int staminaQuantity = 6;
    @SerializedName("prayerTarget")
    public int prayerTarget = 60;
    @SerializedName("f2pRangeTarget")
    public int f2pRangeTarget = 0;
    @SerializedName("defenceTarget")
    public int defenceTarget = 60;
    @SerializedName("rangeTarget")
    public int rangeTarget = 92;
    @SerializedName("RingPreference")
    public RingPreference ringPreference = RingPreference.NONE;
    @SerializedName("boltPreference")
    public GWDBoltPreference boltPreference = GWDBoltPreference.DIAMOND;
    @SerializedName("killCountOrKeysStrategy")
    public EcuKeyStrategy ecuKeyStrategy = EcuKeyStrategy.KILL_COUNT;
    @SerializedName("maxWorldPing")
    public int maxWorldPing = 250;
    @SerializedName("leaveIfCrashed")
    public boolean leaveBossIfCrashed = true;
    @SerializedName("threeKeysPerTrip")
    public boolean threeKeysPerTrip = false;
    @SerializedName("sweatPrayer")
    public boolean sweatPrayer = false;
    @SerializedName("tileSkipAfterAtk")
    public boolean tileSkipping = true;
    @SerializedName("expensiveLootThreshold")
    public int expensiveLootThreshold = 50_000;
}

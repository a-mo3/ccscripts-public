package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.fractals.data.ItemID;

@Data
@Unobfuscated
public class SettingsData {
    @SerializedName("f2pMode")
    public boolean noMember = false;
    @SerializedName("initialGp")
    public int initalGp = 15_000_000;
    @SerializedName("moneyLeftAfterMuling")
    public int moneyLeftAfterMuling = 2_000_000;
    @SerializedName("profitThreshold")
    public int profitThreshold = 2_000_000;
    @SerializedName("natureRuneCount")
    public int natureRuneCount = 1200;
    @SerializedName("restockUntilXAlchableItems")
    public int restockUntil = 50;
    @SerializedName("alchItems")
    public AlchItem[] alchItems = new AlchItem[]{
            new AlchItem(ItemID.MAGIC_LONGBOW, 1270, 1000),
            new AlchItem(ItemID.MAGIC_SHORTBOW, 820, 1000),
            new AlchItem(ItemID.MYSTIC_AIR_STAFF, 25_000, 200),
            new AlchItem(ItemID.MYSTIC_WATER_STAFF, 25_000, 200),
            new AlchItem(ItemID.MYSTIC_EARTH_STAFF, 25_100, 200),
            new AlchItem(ItemID.AIR_BATTLESTAFF, 8900, 200),
            new AlchItem(ItemID.FIRE_BATTLESTAFF, 8900, 200),
            new AlchItem(ItemID.EARTH_BATTLESTAFF, 8900, 200),
    };
    @SerializedName("alchWarningPrice")
    public int alchWarningPrice = 1_000_000;
    @SerializedName("tradeUnrestrict")
    public boolean tradeUnrestrict = false;
    @SerializedName("autoAlch")
    public boolean autoAlch = false;
    @SerializedName("maxAlchablePrice")
    public int maxAlchablePrice = 50_000;
    @SerializedName("minProfit")
    public int minProfit = 55;
    @SerializedName("autoAlchMuleTimer")
    public int autoAlchMuleTime = 6;
    @SerializedName("bankAllOnStart")
    public boolean bankAllOnStart = false;
    @SerializedName("stopAt55")
    public boolean stopAt55;
}

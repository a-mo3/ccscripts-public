package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.fractals.data.ItemID;

@Data
@Unobfuscated
@Getter
public class SettingsData {
    //    @SerializedName("muleOffItems")
//    public boolean muleOffItems;
    @SerializedName("initialGp")
    public int initalGp = 16_000_000;
    @SerializedName("moneyLeftAfterMuling")
    public int moneyLeftAfterMuling = 3_000_000;
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 8;
    @SerializedName("hitpointsTarget")
    public int hitpointsTarget = 40;
    @SerializedName("prayerTarget")
    public int prayerTarget = 75;
    @SerializedName("combatTarget")
    public int combatTarget = 40;
    @SerializedName("defenceTarget")
    public int defenceTarget = 30;
    @SerializedName("rangedTarget")
    public int rangedTarget = 50;
    @SerializedName("eatAbove")
    public int eatAbove = 51;
    @SerializedName("antiPkMode")
    public AntiPkMode antiPKMode = AntiPkMode.SKULLED;
    @SerializedName("minLootValue")
    public int minLootValue = 650;
    //    @SerializedName("useLootingBag")
//    public boolean useLootingBag;
    @SerializedName("avoidCompetition")
    public boolean avoidCompetition;
    @SerializedName("minBoost")
    public int minBoost = 4;
    @SerializedName("dragonMode")
    public DragonMode dragonMode = DragonMode.BLUE;
    @SerializedName("chestID")
    public int chestID = ItemID.MONKS_ROBE_TOP;
    @SerializedName("legID")
    public int legID = ItemID.MONKS_ROBE;
    @SerializedName("hatID")
    public int hatID = ItemID.SNAKESKIN_BANDANA;
    @SerializedName("bootID")
    public int bootID = ItemID.SNAKESKIN_BOOTS;
}

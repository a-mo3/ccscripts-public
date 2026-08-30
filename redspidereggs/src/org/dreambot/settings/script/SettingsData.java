package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import org.dreambot.api.script.Unobfuscated;

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
    @SerializedName("combatTarget")
    public int combatTarget = 40;
    @SerializedName("defenceTarget")
    public int defenceTarget = 30;
    @SerializedName("rangedTarget")
    public int rangedTarget = 75;
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
    @SerializedName("quickHop")
    public boolean quickHop;
    @SerializedName("meleeMode")
    public boolean meleeMode = true;
    @SerializedName("prayMelee")
    public boolean prayerMelee = false;
    @SerializedName("prayerPotionCount")
    public int prayerPotCount = 0;
    @SerializedName("foodCount")
    public int foodCount = 18;
    @SerializedName("onlyStab")
    public boolean onlyStab = false;
    @SerializedName("combatLimit")
    public int combatLimit = 99;
    @SerializedName("trainAgilityForShortcut")
    public boolean shortcut = false;


}

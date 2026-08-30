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
    @SerializedName("useWhip")
    public boolean useWhip = false;
    @SerializedName("useExperimentalRangeLocations")
    public boolean allRangeLocs = false;
    @SerializedName("runAway")
    public boolean runAway = true;
    @SerializedName("whitelistedEquipment")
    public int[] whitelistedEquipment = new int[]{
            ItemID.OAK_LONGBOW
    };
    @SerializedName("useWhiteList")
    public boolean useWhiteList;
    @SerializedName("useBlackList")
    public boolean useBlackList;
    @SerializedName("blackListedEquipment")
    public int[] blackListedEquipment = new int[]{
//            ItemID.SWAMP_PASTE
    };
    @SerializedName("useLootingBag")
    public boolean useLootingBag;
    @SerializedName("useRuneArrows")
    public boolean useRuneArrows = false;
    @SerializedName("useBoostPotions")
    public boolean useBoostPotions = false;
    @SerializedName("useStaminaPotion")
    public boolean useStaminaPotion = false;
    @SerializedName("minimumBoost")
    public int minBoost = 5;
    @SerializedName("getAvas")
    public boolean getAvas = false;
    @SerializedName("multipleMeleeAreas")
    public boolean multipleMeleeAreas = true;
    @SerializedName("foodId")
    public int foodId = ItemID.SHARK;
    @SerializedName("disablePkList")
    public boolean disablePkList = false;
}

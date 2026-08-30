package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;

@Data
@AllArgsConstructor
@Unobfuscated
public class SettingsData {
    //    @SerializedName("muleOffItems")
//    public boolean muleOffItems;
    @SerializedName("initialGp")
    public int initalGp;
    @SerializedName("beadBuyPrice")
    public int beadBuyPrice;
    @SerializedName("moneyLeftAfterMuling")
    public int moneyLeftAfterMuling;
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff;
    @SerializedName("howManySetsToRestock")
    public int setsToRestock;
    @SerializedName("magicTarget")
    public int magicTarget;
    @SerializedName("hitpointsTarget")
    public int hitpointsTarget;
    @SerializedName("defenceTarget")
    public int defenceTarget;
    @SerializedName("eatAbove")
    public int eatAbove;
    @SerializedName("antiPkMode")
    public AntiPkMode antiPKMode;
    @SerializedName("staffMode")
    public StaffMode staffMode;
    @SerializedName("useOccult")
    public boolean useOccult;
    @SerializedName("minLootValue")
    public int minLootValue;
    @SerializedName("useLootingBag")
    public boolean useLootingBag;
    @SerializedName("avoidCompetition")
    public boolean avoidCompetition;
    @SerializedName("quickHop")
    public boolean quickHop;
    @SerializedName("fireBoltCharges")
    public int fireBoltCharges;
    //    @SerializedName("newSpots")
//    public boolean useNewSpots = false;
    @SerializedName("hatId")
    public int hatId;
    @SerializedName("chestId")
    public int chestId;
    @SerializedName("legId")
    public int legId;
    @SerializedName("runAwayAntiPk")
    public boolean runaway;
    @SerializedName("attackCompetition")
    public boolean attackCompetition;
    @SerializedName("useStaminaPotions")
    public boolean useStaminas;
    @SerializedName("collectDeathItems")
    public boolean collectDeathItems;
    @SerializedName("decantPotions")
    public boolean decantpotions;
    @SerializedName("turnInKeys")
    public boolean turnInKeys;
    @SerializedName("rangeMode")
    public boolean useRangeMode = false;
    @SerializedName("armourDefReq")
    public int armourDefReq;
    @SerializedName("stopAt55")
    public boolean stopAt55;
}

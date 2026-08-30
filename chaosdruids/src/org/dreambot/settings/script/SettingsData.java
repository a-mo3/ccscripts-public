package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.fractals.data.ItemID;

@Data
@Unobfuscated
public class SettingsData {
    @SerializedName("initialGp")
    public int initalGp = 16_000_000;
    @SerializedName("moneyLeftAfterMuling")
    public int moneyLeftAfterMuling = 300_000;
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("hitpointsTarget")
    public int hitpointsTarget = 30;
    @SerializedName("combatTargets")
    public int combatTargets = 25;
    //    @SerializedName("eatAbove")
//    public int eatAbove;
//    @SerializedName("antiPkMode")
//    public AntiPkMode antiPKMode;
    @SerializedName("foodId")
    public int foodId = ItemID.LOBSTER;
    @SerializedName("avoidCompetition")
    public boolean avoidCompetition = true;
    @SerializedName("useLootingBag")
    public boolean useLootingBag = true;
    @SerializedName("minLootVal")
    public int minLootVal = 450;
    @SerializedName("worshipSatanAndDisableSantaHat")
    public boolean disableSanta = false;
    @SerializedName("stage")
    public boolean stage = false;
    @SerializedName("pureMode")
    public boolean pureMode = false;
}

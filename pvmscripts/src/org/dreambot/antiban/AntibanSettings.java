package org.dreambot.antiban;

import com.google.gson.annotations.SerializedName;
import org.dreambot.settings.ui.nui.UIExplanation;

public class AntibanSettings {
    @SerializedName("enable")
    @UIExplanation("Anti ban is experimental rn")
    public boolean enabled = false;

    @SerializedName("mouseOffChance")
    @UIExplanation("Whenever the script sleeps, the chance you mouse off")
    public int mouseOffChance = 80; // represents % /100

    @SerializedName("hoverEntityChance")
    @UIExplanation("Whenever the script sleeps, the chance you mouse off")
    public int hoverEntity = 5; // represents % /100

//    @SerializedName("autoBreaks")
//    public boolean autoBreaks = true;
//
//    @SerializedName("allowSkillingBreaks")
//    public boolean skillingBreaks = false;
//
//    @SerializedName("allowExploreBreaks")
//    public boolean exploreBreaks = false;
//
//    @SerializedName("allowAfkBreaks")
//    public boolean afkBreaks = true;
//
//    @SerializedName("skillBreakBias")
//    @UIExplanation("the % this is of the total biases is the % chance to roll this kind of break")
//    public int skillBreakBias = 10;
//
//    @SerializedName("exploreBreakBias")
//    @UIExplanation("the % this is of the total biases is the % chance to roll this kind of break")
//    public int exploreBreakBias = 10;
//
//    @SerializedName("afkBreakBias")
//    @UIExplanation("the % this is of the total biases is the % chance to roll this kind of break")
//    public int afkBreakBias = 10;
//
//    @SerializedName("maxDailyBottingHours")
//    @UIExplanation("max time you bot before the bot goes offline for the day")
//    public int maxDailyBottingTime = 12;
//
//    @SerializedName("minDailyBottingHours")
//    @UIExplanation("min time you bot before the bot goes offline for the day")
//    public int minDailyBottingTime = 7;
//
//    @SerializedName("minHoursBreaking")
//    @UIExplanation("min time during botting period spent breaking")
//    public int minTimeBreaking = 1;
//
//    @SerializedName("maxHoursBreakingHours")
//    @UIExplanation("max time during botting period spent breaking")
//    public int maxTimeBreaking = 3;
//
//    @SerializedName("breakClusteringChance")
//    @UIExplanation("Chance breaks happen close together, 100 = all at once, 0 = many random breaks")
//    public int clustering = 60;
}

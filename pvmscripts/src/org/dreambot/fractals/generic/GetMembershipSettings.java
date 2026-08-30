package org.dreambot.fractals.generic;

import com.google.gson.annotations.SerializedName;
import org.dreambot.settings.ui.nui.UIExplanation;

public class GetMembershipSettings {
    @SerializedName("initalGP")
    public int initalGP = 16_000_000;
    @SerializedName("bondBuyPrice")
    public int bondPrice = 14_000_000;
    @SerializedName("bankDumpOverBondup")
    @UIExplanation("Does not buy a bond and get membership, instead dumps entire bank and mules off, use qs arg bankDump to cause this")
    public boolean bankDump = false;
    @SerializedName("maxGP")
    @UIExplanation("When your account has more than this amount of gp we force a mule off")
    public int maxGP = Integer.MAX_VALUE;
}

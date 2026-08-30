package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SettingsData {
    @SerializedName("initalGp")
    int initialGP = 13_000_000;
    @SerializedName("hoursUntilMuleOff")
    int hoursUntilMuleOff = 12;
    @SerializedName("gpRemainingAfterMuling")
    int gpRemainingAfterMuling = 500_000;

    @SerializedName("miningTarget")
    int miningTarget = 52;
    @SerializedName("copperCompetitionMax")
    int copperCompetitionMax = 4;
    @SerializedName("ironCompetitionMax")
    int ironCompetitionMax = 4;
    @SerializedName("coalCompetitionMax")
    int coalCompetitionMax = 4;
    @SerializedName("disableTopFloor")
    boolean disableTopFloor = true;
    @SerializedName("dontBuyProspector")
    boolean dontBuyProspector = false;
    @SerializedName("dragonPickaxe")
    boolean useDragonPickaxe = true;
}

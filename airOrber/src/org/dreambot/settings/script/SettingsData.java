package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class SettingsData {
    @SerializedName("beadBuyPrice")
    int beadBuyPrice;
    @SerializedName("initalGp")
    int initialGP;
    @SerializedName("hpTarget")
    int hpTarget;
    @SerializedName("defenceTarget")
    int defenceTarget;
    @SerializedName("energyPotions")
    int energyPotions;
    @SerializedName("salmons")
    int salmons;
    @SerializedName("hoursUntilMuleOff")
    int hoursUntilMuleOff;
    @SerializedName("gpRemainingAfterMuling")
    int gpRemainingAfterMuling;
    @SerializedName("useSharks")
    boolean useSharks;
}

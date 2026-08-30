package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;

@Data
@AllArgsConstructor
@Unobfuscated
public class SettingsData {
    @SerializedName("trawlerContributionLimit")
    public int trawlerContributionLimit;
    @SerializedName("trawlerBankCheckTimeMinutes")
    public int trawlerBankCheckTimeMinutes;
    @SerializedName("initialGP")
    public int initialGP;
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff;
    @SerializedName("gpRemainingAfterMuling")
    public int gpRemainingAfterMuling;
    @SerializedName("stopAt82Fishing")
    public boolean stopAfterFishing;
}

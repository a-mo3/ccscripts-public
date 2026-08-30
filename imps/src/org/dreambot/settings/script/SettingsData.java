package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import org.dreambot.api.script.Unobfuscated;

@Data
@Unobfuscated
@Getter
public class SettingsData {
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 8;
    @SerializedName("moneyLeftAfterMuling")
    public int moneyLeftAfterMuling = 30_000;
    @SerializedName("restockQuantity")
    public int restockQuantity = 1200;
    @SerializedName("stopAfterUnrestricted")
    public boolean stage = false;
    @SerializedName("skillsBeforeCombat")
    public boolean skillsBeforeCombat = false;
}

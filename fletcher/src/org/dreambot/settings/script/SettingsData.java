package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;

@Data
@AllArgsConstructor
@Unobfuscated
public class SettingsData {
    @SerializedName("initialGP")
    public int initialGP;
    @SerializedName("gpRemainingAfterMuling")
    public int gpRemainingAfterMuling;
    @SerializedName("stringMagics")
    public boolean stringMagics;
    @SerializedName("fletchFromLogs")
    public boolean fletchFromLogs;
    @SerializedName("forceWorld")
    public int forceWorld;
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 3;
}

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
    @SerializedName("fillMode")
    public FillMode fillMode = FillMode.JUG;
    @SerializedName("restockQuantity")
    public int restockQuantity = 1200;
    @SerializedName("stopAfterUnrestricted")
    public boolean stage = false;
    @SerializedName("ignorePlaytime")
    public boolean ignorePlaytime = false;
    @SerializedName("alreadyUnrestricted") // for used to be p2p accounts
    public boolean alreadyUnrestriced = false;
    @SerializedName("itemMuleOff") // for used to be p2p accounts
    public boolean itemMuleOff = false;
}

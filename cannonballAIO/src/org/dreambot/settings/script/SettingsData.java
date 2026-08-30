package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;

@Data
@Unobfuscated
public class SettingsData {
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff;
    @SerializedName("muleOffItems")
    public boolean muleOffItems = true;
    @SerializedName("gpRemainingAfterMuling")
    public int gpRemainingAfterMuling = 500_000;
    @SerializedName("steelBarRestockQuantity")
    public int steelBarRestockQuantity = 1000;
    @SerializedName("initialGp")
    public int initalGp = 12_000_000;
    @SerializedName("getDoubleMould")
    public boolean getDoubleMould = true;
    @SerializedName("stopAfterFoundry")
    public boolean stopAfterFoundry = false;
}

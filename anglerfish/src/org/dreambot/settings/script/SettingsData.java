package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;

@Data
@Unobfuscated
public class SettingsData {
    @SerializedName("muleOffItems")
    public boolean muleOffItems = false;
    @SerializedName("initialGp")
    public int initalGp = 12_000_000;
    @SerializedName("moneyLeftAfterMuling")
    public int moneyLeftAfterMuling = 100_000;
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("stopAfterFishing")
    public boolean stopAfterFishing = false;
}

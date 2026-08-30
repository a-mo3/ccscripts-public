package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;

@Data
@Unobfuscated
public class SettingsData {
    //    @SerializedName("muleOffItems")
//    public boolean muleOffItems;
    @SerializedName("initialGp")
    public int initalGp = 16_000_000;
    @SerializedName("moneyLeftAfterMuling")
    public int moneyLeftAfterMuling = 3_000_000;
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 8;
}

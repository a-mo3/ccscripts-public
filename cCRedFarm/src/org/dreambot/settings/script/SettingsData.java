package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;

@Data
@Unobfuscated
public class SettingsData {
    @SerializedName("initialGP")
    public int initialGP = 15_000_000;
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 8;
    @SerializedName("gpRemainingAfterMuling")
    public int gpRemainingAfterMuling = 1_000_000;
    @SerializedName("tradeOffChins")
    public boolean tradeOffChins = false;
    @SerializedName("bankAtXChins")
    public int chinMax = 100;
    @SerializedName("shouldBuryBones")
    public boolean shouldBuryBones = true;
    @SerializedName("avoidCompetition")
    public boolean avoidCompetition = true;
    @SerializedName("boxTrapRestock")
    public int boxTrapRestock = 100;
    @SerializedName("boxTrapBuyPrice")
    public int boxTrapBuyPrice = 500;
}

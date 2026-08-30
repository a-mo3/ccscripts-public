package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.fractals.data.ItemID;

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
    @SerializedName("trainWithRedChins")
    public boolean redChins = true;
    @SerializedName("quickHop")
    public boolean quickHop = true;
    @SerializedName("antiPKMode")
    AntiPkMode antiPkMode = AntiPkMode.SKULLED_OR_EQUIPMENT;
    @SerializedName("whitelistedEquipment")
    public int[] whitelistedEquipment = new int[]{
            ItemID.OAK_LONGBOW
    };
    @SerializedName("useWhiteList")
    public boolean useWhiteList;
    @SerializedName("useBlackList")
    public boolean useBlackList;
    @SerializedName("blackListedEquipment")
    public int[] blackListedEquipment = new int[]{
            ItemID.SWAMP_PASTE
    };
    @SerializedName("avoidCompetition")
    public boolean avoidCompetition = true;
    @SerializedName("boxTrapRestock")
    public int boxTrapRestock = 100;
    @SerializedName("boxTrapBuyPrice")
    public int boxTrapBuyPrice = 500;
    @SerializedName("runAway")
    public boolean runAway = true;
    @SerializedName("pkerPainting")
    public boolean pkerPainting = false;
    @SerializedName("scavengeAntelopeDrops")
    public boolean looting = true;
    @SerializedName("trainMode")
    public TrainMode trainMode = TrainMode.BLACK_CHINS;
    @SerializedName("fletch")
    public boolean fletch = false;
}

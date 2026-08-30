package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.fractals.data.ItemID;

@Data
@Unobfuscated
public class SettingsData {
    @SerializedName("initialGp")
    public int initalGp = 16_000_000;
    @SerializedName("moneyLeftAfterMuling")
    public int moneyLeftAfterMuling = 300_000;
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("lootWebhookURL")
    public String lootWebhookURL = "";
    @SerializedName("collectDeathItems")
    public boolean collectDeathItems = true;
    @SerializedName("decantPotions")
    public boolean decantpotions = false;
    @SerializedName("prayMeleeInTunnel")
    public boolean prayInTunnel = false;
    @SerializedName("prayerPotionCount")
    public int prayerPotionCount = 8;
    @SerializedName("rewardTarget")
    public int rewardTarget = 750;
    @SerializedName("rangeTorsoID")
    public int rangeTorsoID = ItemID.BLUE_DHIDE_BODY;
    @SerializedName("rangeLegsID")
    public int rangeLegsID = ItemID.BLUE_DHIDE_CHAPS;
    @SerializedName("meleeTorsoID")
    public int meleeTorsoID = ItemID.RUNE_CHAINBODY;
    @SerializedName("meleeLegsID")
    public int meleeLegsID = ItemID.RUNE_PLATELEGS;
    @SerializedName("hatID")
    public int hatID = -1;
    @SerializedName("gloveID")
    public int gloveID = -1;
    @SerializedName("level3start")
    public boolean trainAll = true;
    @SerializedName("minLootValue")
    public int minLootValue = 999;
    @SerializedName("defenceTarget")
    public int defenceTarget = 40;
    @SerializedName("defCastUntil")
    public int barrowsDefTarget = 40;
    @SerializedName("progressiveDhides")
    public boolean progressiveRangeGear = false;
    @SerializedName("prayerTarget")
    public int prayerTarget = 43;
}

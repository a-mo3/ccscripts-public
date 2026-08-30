package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.behaviour.method.pirates.PirateEquipmentLoadout;
import org.dreambot.behaviour.method.pirates.PirateInventoryLoadout;

public class ZombiePirateSettings {

    @SerializedName("minLootValue")
    public int minLootValue = 1000;
    @SerializedName("prayerTarget")
    public int prayerTarget = 43;
    @SerializedName("F2PMeleeTraining")
    public boolean ftpMeleeTraining = false;
    @SerializedName("attackTarget")
    public int attackTarget = 0;
    @SerializedName("strengthTarget")
    public int strengthTarget = 0;
    @SerializedName("defenceTarget")
    public int defenceTarget = 40;
    @SerializedName("F2PRangeTraining")
    public boolean ftpRangeTraining = false;
    @SerializedName("rangeTarget")
    public int rangeTarget = 70;
    @SerializedName("F2PMagicTraining")
    public boolean ftpMagicTraining = false;
    @SerializedName("magicTarget")
    public int magicTarget = 0;
    //        @SerializedName("etherRechargeQuantity")
//        public int etherRechargeQuantity = 350;
    @SerializedName("equipmentLoadout")
    public PirateEquipmentLoadout pirateEquipmentLoadout = PirateEquipmentLoadout.DHIDE_ROSEWOOD_BLOWPIPE;
    @SerializedName("inventoryLoadout")
    public PirateInventoryLoadout pirateInventoryLoadout = PirateInventoryLoadout.WINES;
    @SerializedName("exitLootValue")
    public int exitLootValue = 150_000;
    //        @SerializedName("antiCrash")
//        public boolean antiCrash = true;
//        @SerializedName("antiCrashSecondsThreashold")
//        public int anticrashTime = 15_000;
    @SerializedName("stopAfterF2P")
    public boolean stopAfterFTP = false;
    //        @SerializedName("useAltarForPrayer") // when at zombie pirates, use blighted restores or the altar
//        public boolean useAltarForPrayer = true;
    @SerializedName("useBoostPrayer")
    public boolean useBoostPrayer = false;
    @SerializedName("boostPrayer")
    public Prayer boostPrayer = Prayer.EAGLE_EYE;
    @SerializedName("doDiaries")
    public boolean doDiaries = true;
    @SerializedName("flickPrayers")
    public boolean flick = false;
    @SerializedName("useAvas")
    public boolean useAvas = true;
}

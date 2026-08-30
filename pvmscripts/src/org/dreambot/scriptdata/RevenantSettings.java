package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.revs.behaviour.RevenantTeleportStrategy;
import org.dreambot.behaviour.method.revs.data.*;

public class RevenantSettings {
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("useLootingBag")
    public boolean useLootingBag = false;
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
    @SerializedName("etherRechargeQuantity")
    public int etherRechargeQuantity = 350;
    @SerializedName("equipmentLoadout")
    public RevenantEquipmentLoadout revenantEquipmentLoadout = RevenantEquipmentLoadout.DHIDE_MSB;
    @SerializedName("inventoryLoadout")
    public RevenantInventoryLoadout revenantInventoryLoadout = RevenantInventoryLoadout.MANTAS_RESTORE;
    @SerializedName("teleportStrategy")
    public RevenantTeleportStrategy teleportStrategy = RevenantTeleportStrategy.BURNING_NECKLACE;
    @SerializedName("targetRevenant")
    public RevenantLocations targetRevenant = RevenantLocations.GOBLINS;
    @SerializedName("exitLootValue")
    public int exitLootValue = 150_000;
    @SerializedName("skullUp")
    public boolean skullUp = false;
    @SerializedName("useEtherumBracelet")
    public boolean useEtherBracelet = false;
    @SerializedName("centerWhenNoRevs")
    public boolean centerWhenNoRevs = true;
    @SerializedName("antiCrash")
    public boolean antiCrash = true;
    @SerializedName("antiCrashSecondsThreashold")
    public int anticrashTime = 15_000;
    @SerializedName("useAvarice")
    public boolean useAvarice = false;
    @SerializedName("braceletRecharge")
    public int braceletRecharge = 350;
    @SerializedName("stopAfterF2P")
    public boolean stopAfterFTP = false;

    @SerializedName("doHardDiary")
    public boolean doHardWildernessDiary = false;
}

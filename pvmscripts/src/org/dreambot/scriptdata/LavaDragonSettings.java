package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.lavadragons.LavaDragonAntiPKStrategy;
import org.dreambot.behaviour.method.lavadragons.LavaDragonLoadout;

public class LavaDragonSettings {
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
    @SerializedName("alwaysDoWitchesHouse")
    public boolean alwaysWitchesHouse = true;
    @SerializedName("ftpTrainMagicUntil")
    public int ftpMagicTarget = 60;
    @SerializedName("ftpDefCastUntilLevel")
    public int ftpDefenseTarget = 35;
    @SerializedName("trainMagicUntil")
    public int magicTarget = 60;
    @SerializedName("defCastUntilLevel")
    public int defenseTarget = 35;
    @SerializedName("restockMultiple")
    public int restockMultiple = 4;


    @SerializedName("enableWaterBolt")
    public boolean enabledWaterBolt = false;
    @SerializedName("enableWaterBlast")
    public boolean enabledWaterBlast = false;
    @SerializedName("enableWaterWave")
    public boolean enabledWaterWave = false;

    @SerializedName("useOccult")
    public boolean useOccult = false;
    @SerializedName("useLootingBag")
    public boolean useLootingBag = false;
    @SerializedName("avoidCompetition")
    public boolean avoidCompetition = true;
    @SerializedName("suicideToBank")
    public boolean suicide = false;

    @SerializedName("antiPKStrategy")
    public LavaDragonAntiPKStrategy antiPKStrategy = LavaDragonAntiPKStrategy.SKULLED_IN_COMBAT_RANGE;
    @SerializedName("loadout")
    public LavaDragonLoadout lavaDragonLoadout = LavaDragonLoadout.TRIDENT_SEAS_RECOMMENDED;
}
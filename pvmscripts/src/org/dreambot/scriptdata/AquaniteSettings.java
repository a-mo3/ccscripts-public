package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.lavadragons.LavaDragonLoadout;

public class AquaniteSettings {
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

    @SerializedName("useOccult")
    public boolean useOccult = false;
    @SerializedName("useLootingBag")
    public boolean useLootingBag = false;
    @SerializedName("avoidCompetition")
    public boolean avoidCompetition = true;

    /* todo grimstone transpot method
     * Sailing to grimstone seems to be fastest from weiss
     * the lowest req way to get to weiss would be 330 POH teleport, unstable because of troll houses
     * or go to rellekka and take larrys boat, idk the quest req for that and it takes a long time
     * either way you'd need making friends with my arm to access this
     *
     * waterbirth teleport tablet or lunar spellbook or poh is fastest without that quest, probably
     */

    @SerializedName("loadout")
    public LavaDragonLoadout lavaDragonLoadout = LavaDragonLoadout.TRIDENT_SEAS_RECOMMENDED;
}
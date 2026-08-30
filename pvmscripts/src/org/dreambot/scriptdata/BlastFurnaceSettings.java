package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.blastfurnace.BlastFurnaceModes;
import org.dreambot.behaviour.method.blastfurnace.BlastFurnaceRestockStrategy;

public class BlastFurnaceSettings {
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("minLootValue")
    public int minLootValue = 1000;
    @SerializedName("blastFuranceMode")
    public BlastFurnaceModes mode = BlastFurnaceModes.STEEL;
    @SerializedName("restockStrategy")
    public BlastFurnaceRestockStrategy restockStrategy = BlastFurnaceRestockStrategy.BREAK;
    @SerializedName("useStaminas")
    public boolean useStaminas = false;
}

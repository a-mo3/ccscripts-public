package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.settings.ui.EquipmentSelections;

public class NMZScriptSettings {
    @SerializedName("nmzEquipment")
    public EquipmentSelections nmzCustomEquipment = EquipmentSelections.NMZ;
    @SerializedName("rangeEquipment")
    public EquipmentSelections rangeEquipment = EquipmentSelections.NMZ;
    @SerializedName("rangeTarget")
    public int rangeTarget = 0;
    @SerializedName("attackTarget")
    public int attackTgt = 99;
    @SerializedName("defenceTarget")
    public int defenceTgt  = 99;
    @SerializedName("strengthTarget")
    public int strengthTgt = 99;

}

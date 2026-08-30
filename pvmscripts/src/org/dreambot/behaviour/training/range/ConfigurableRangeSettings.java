package org.dreambot.behaviour.training.range;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.gemstone.GemstoneCrabRangeLoadout;
import org.dreambot.settings.ui.EquipmentSelections;
import org.dreambot.settings.ui.nui.UIExplanation;

public class ConfigurableRangeSettings {
    @SerializedName("trainingMode")
    public ConfigurableRangeMode trainingMode = ConfigurableRangeMode.SANDCRABS;
    @SerializedName("nmzEquipment")
    public EquipmentSelections nmzCustom = EquipmentSelections.NMZ;
    @SerializedName("defTarget")
    public int defTarget = 40;
    @SerializedName("gemStoneLoadout")
    GemstoneCrabRangeLoadout gemstoneLoadout = GemstoneCrabRangeLoadout.KNIVES;
    @SerializedName("gemCustomStoneLoadout")
    String gemstoneCustomLoadout = "";

    @SerializedName("useFlickingWhenAvailable")
    @UIExplanation("1T flicks to conserve prayer when using a method that supports it")
    public boolean flicking = true;

//    @SerializedName("useDharoksAbove92HP")
//    public boolean useDharoks = false;
}

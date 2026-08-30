package org.dreambot.behaviour.training.nmz;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.gemstone.GemstoneCrabMeleeLoadout;
import org.dreambot.settings.ui.EquipmentSelections;
import org.dreambot.settings.ui.nui.UIExplanation;

public class ConfigurableCombatSettings {
    @SerializedName("trainingMode")
    public ConfigurableCombatMode trainingMode = ConfigurableCombatMode.SANDCRABS;
    @SerializedName("nmzEquipment")
    public EquipmentSelections nmzCustom = EquipmentSelections.NMZ;
    @SerializedName("enableFightArena")
    public boolean fightArena = true;
    @SerializedName("enableWitchsHouse")
    public boolean witchsHouse = true;
    @SerializedName("enableVampyreSlayer")
    public boolean vampyreSlayer = true;

    @SerializedName("attackTarget")
    public int attackTarget = 100;
    @SerializedName("defenceTarget")
    public int defenceTarget = 100;
    @SerializedName("strengthTarget")
    public int strengthTarget = 100;

    @SerializedName("gemstoneMeleeLoadout")
    public GemstoneCrabMeleeLoadout gemstoneCrabMeleeLoadout = GemstoneCrabMeleeLoadout.OBBY_SARA;
    @SerializedName("customLoadoutGemstoneOnly")
    public String gemstoneCustomLoadout = "";


    @SerializedName("useFlickingWhenAvailable")
    @UIExplanation("1T flicks to conserve prayer when using a method that supports it")
    public boolean flicking = true;

//    @SerializedName("useDharoksAbove92HP")
//    public boolean useDharoks = false;
}

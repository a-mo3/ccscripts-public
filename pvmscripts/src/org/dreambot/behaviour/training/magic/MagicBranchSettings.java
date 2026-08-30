package org.dreambot.behaviour.training.magic;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.gemstone.GemstoneCrabMagicLoadout;
import org.dreambot.behaviour.method.gemstone.GemstoneCrabRangeLoadout;
import org.dreambot.settings.ui.nui.UIExplanation;

public class MagicBranchSettings {
    @SerializedName("magicTrainingMode")
    ConfigurableMagicBranch.MagicTrainingMode trainingMode = ConfigurableMagicBranch.MagicTrainingMode.STANDARD;
    @SerializedName("defTarget")
    int defTarget = 40;
    @SerializedName("gemStoneLoadout")
    GemstoneCrabMagicLoadout gemstoneLoadout = GemstoneCrabMagicLoadout.AIR_STAFF;
    @SerializedName("gemCustomStoneLoadout")
    String gemstoneCustomLoadout = "";

    @SerializedName("useFlickingWhenAvailable")
    @UIExplanation("1T flicks to conserve prayer when using a method that supports it")
    public boolean flicking = true;
}

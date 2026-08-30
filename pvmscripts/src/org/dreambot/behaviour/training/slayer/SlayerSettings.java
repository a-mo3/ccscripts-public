package org.dreambot.behaviour.training.slayer;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.dreambot.settings.ui.nui.UIExplanation;

@Getter
public class SlayerSettings {
    @SerializedName("mode")
    @UIExplanation("How to train slayer, with melee ranged or magic")
    SlayerMode slayerMode = SlayerMode.RANGED;
    @SerializedName("flickPrayers")
    @UIExplanation("When we have a task we should pray for, do we flick or camp prayer")
    boolean flickPrayer = false;
}

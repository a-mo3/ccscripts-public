package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.settings.ui.nui.UIExplanation;

public class ImpSettings {
    @SerializedName("staging")
    @UIExplanation("Turns the script off after trade unlocked, for staging accounts.")
    public boolean stage = false;
}
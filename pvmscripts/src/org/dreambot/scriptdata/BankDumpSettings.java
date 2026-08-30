package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.settings.ui.nui.UIExplanation;

public class BankDumpSettings {
    @SerializedName("ignoreUnderValue")
    @UIExplanation("Ignores items worth less than this, should be atleast 1")
    public int ignoreUnder = 5;
}
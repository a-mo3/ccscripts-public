package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.settings.ui.nui.UIExplanation;

public class TutorialScriptSettings {
    @SerializedName("walkAboutMinutes")
    @UIExplanation("Time in minutes, the script will go walk about after completing tutorial island")
    public int walkAboutTime = 3;
}
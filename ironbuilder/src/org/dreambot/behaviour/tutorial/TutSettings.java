package org.dreambot.behaviour.tutorial;

import com.google.gson.annotations.SerializedName;
import org.dreambot.fractals.IronmanType;

public class TutSettings {
    @SerializedName("ironmanType")
    public IronmanType ironmanType = IronmanType.NORMAL;
}

package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.artio.ArtioLoadout;

public class FletchSettings {
    @SerializedName("stringMagics")
    public boolean stringMagics;
    @SerializedName("fletchFromLogs")
    public boolean fletchFromLogs;
}

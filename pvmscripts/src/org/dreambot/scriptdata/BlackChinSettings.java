package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.training.hunter.GrayChinSpot;

public class BlackChinSettings {
    @SerializedName("crashOthers")
    public boolean crash = false; // if you leave the chasm or crash other people after entering spindel
    @SerializedName("trainWithGrayChin")
    public boolean trainWithGray = true;
    @SerializedName("trainWithRedChins")
    public boolean trainWithRed = true;
    @SerializedName("bankAtXChins")
    public int chinLimit = 50;
    @SerializedName("forceCameraUp")
    public boolean forceCameraUp = true;
//    @SerializedName("hinSpot")
//    public GrayChinSpot spot = GrayChinSpot.KOUREND;
}

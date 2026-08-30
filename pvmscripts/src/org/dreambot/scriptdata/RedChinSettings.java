package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;

public class RedChinSettings {
    @SerializedName("trainWithGrayChin")
    public boolean trainWithGray = true;
    @SerializedName("crashOthers")
    public boolean crash = false; // if you leave the chasm or crash other people after entering spindel
    @SerializedName("useVarlamoreSpot")
    public boolean varlamoreSpot = false; // if you leave the chasm or crash other people after entering spindel

//    @SerializedName("hinSpot")
//    public GrayChinSpot spot = GrayChinSpot.KOUREND;
}

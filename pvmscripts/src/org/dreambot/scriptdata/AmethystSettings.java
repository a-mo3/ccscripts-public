package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.settings.ui.nui.UIExplanation;

public class AmethystSettings {
    @SerializedName("stopAfterFTPTraining")
    @UIExplanation("If training in f2p, stop the script after getting level before getting membership")
    public boolean stopAfterFTPMining;
    @SerializedName("trainInFTP")
    @UIExplanation("train inital mining in free to play")
    public boolean trainInFTP;
    @SerializedName("miningTarget")
    @UIExplanation("level to get before going to MLM")
    public int miningTarget = 60;

    @SerializedName("useTopFloor")
    public boolean useTopFloor = true;
    @SerializedName("buyProspector")
    public boolean buyProspector = true;
    @SerializedName("useDragonPickaxe")
    public boolean useDragonPickaxe = true;

}
package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.mta.MTAReward;

public class MTASettings {
    @SerializedName("magicTarget")
    public int magicTarget = 100; // stop script if real level hits this
    @SerializedName("rewardTarget")
    public MTAReward mtaRewardTarget = MTAReward.INFINITY_BOOTS;
}

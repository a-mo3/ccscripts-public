package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.gemstone.GemstoneCrabMagicLoadout;
import org.dreambot.behaviour.method.gemstone.GemstoneCrabMeleeLoadout;
import org.dreambot.behaviour.method.gemstone.GemstoneCrabRangeLoadout;
import org.dreambot.behaviour.method.mixology.MixologyRewardItem;

public class MixologySettings {
    @SerializedName("herbloreTrainingTarget")
    public int herbloreTarget = 60;
    @SerializedName("reward")
    public MixologyRewardItem rewardTarget = MixologyRewardItem.ALDARIUM;
}

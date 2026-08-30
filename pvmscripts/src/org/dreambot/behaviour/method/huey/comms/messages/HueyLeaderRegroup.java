package org.dreambot.behaviour.method.huey.comms.messages;

import com.google.gson.annotations.SerializedName;
import org.dreambot.api.methods.interactive.Players;

public class HueyLeaderRegroup {
    @SerializedName("messageType")
    public HueyMsgType type = HueyMsgType.LEADER_REGROUP;
    @SerializedName("username")
    public String username = Players.getLocal().getName();
}

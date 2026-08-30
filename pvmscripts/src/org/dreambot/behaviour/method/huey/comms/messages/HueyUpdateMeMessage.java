package org.dreambot.behaviour.method.huey.comms.messages;

import com.google.gson.annotations.SerializedName;
import org.dreambot.api.methods.interactive.Players;

public class HueyUpdateMeMessage {
    @SerializedName("messageType")
    public HueyMsgType type = HueyMsgType.UPDATE_ME;
    @SerializedName("username")
    public String username = Players.getLocal().getName();
}

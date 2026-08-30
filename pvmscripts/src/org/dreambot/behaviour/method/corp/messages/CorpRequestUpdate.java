package org.dreambot.behaviour.method.corp.messages;

import com.google.gson.annotations.SerializedName;
import org.dreambot.api.methods.interactive.Players;

public class CorpRequestUpdate {
    @SerializedName("messageType")
    CorpMessageType messageType = CorpMessageType.REQUEST_UPDATE;
    @SerializedName("playerName")
    public String name = Players.getLocal().getName();
}

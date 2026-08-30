package org.dreambot.behaviour.method.corp.messages;

import com.google.gson.annotations.SerializedName;

public class CorpKillCompleteMessage {
    @SerializedName("messageType")
    CorpMessageType messageType = CorpMessageType.KILL_COMPLETE;
}

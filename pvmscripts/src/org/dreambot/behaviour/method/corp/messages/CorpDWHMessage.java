package org.dreambot.behaviour.method.corp.messages;

import com.google.gson.annotations.SerializedName;

public class CorpDWHMessage {
    @SerializedName("messageType")
    CorpMessageType messageType = CorpMessageType.DWH_SPEC_HIT;

}

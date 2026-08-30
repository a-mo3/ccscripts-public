package org.dreambot.behaviour.method.corp.messages;

import com.google.gson.annotations.SerializedName;

/**
 * fired when an account hits a bgs spec
 */
public class CorpBGSMessage {
    @SerializedName("messageType")
    CorpMessageType messageType = CorpMessageType.BGS_SPEC_HIT;

    @SerializedName("damage")
    public final int damage;

    public CorpBGSMessage(int damage) {
        this.damage = damage;
    }
}

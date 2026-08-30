package org.dreambot.muling.messages.client;


import com.google.gson.annotations.SerializedName;
import org.dreambot.muling.messages.AbstractMessage;
import org.dreambot.muling.messages.MessageType;

public class UnknownTraderMessage extends AbstractMessage {
    @SerializedName("playerName")
    public final String playerName;

    public UnknownTraderMessage(String playerName) {
        super(MessageType.UNKNOWN_TRADER);
        this.playerName = playerName;
    }
}

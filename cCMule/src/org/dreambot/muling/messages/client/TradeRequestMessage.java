package org.dreambot.muling.messages.client;


import com.google.gson.annotations.SerializedName;
import org.dreambot.muling.messages.AbstractMessage;
import org.dreambot.muling.messages.MessageType;

public class TradeRequestMessage extends AbstractMessage {
    @SerializedName("requestId")
    public final String requestId;

    public TradeRequestMessage(String requestId) {
        super(MessageType.TRADE_REQUEST);
        this.requestId = requestId;
    }
}

package org.dreambot.muling.messages.client;


import com.google.gson.annotations.SerializedName;
import org.dreambot.muling.messages.AbstractMessage;
import org.dreambot.muling.messages.MessageType;

public class TradeCompletedMessage extends AbstractMessage {
    @SerializedName("success")
    public final boolean success;
    @SerializedName("reason")
    public final String reason;
    @SerializedName("requestId")
    public final String requestId;

    public TradeCompletedMessage(boolean success, String reason, String requestId) {
        super(MessageType.TRADE_COMPLETED);
        this.success = success;
        this.reason = reason;
        this.requestId = requestId;
    }
}

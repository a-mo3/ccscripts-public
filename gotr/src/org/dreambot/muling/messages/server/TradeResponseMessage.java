package org.dreambot.muling.messages.server;


import com.google.gson.annotations.SerializedName;
import org.dreambot.muling.messages.AbstractMessage;
import org.dreambot.muling.messages.MessageType;

public class TradeResponseMessage extends AbstractMessage {
    @SerializedName("success")
    public final boolean success;
    @SerializedName("errorMessage")
    public final String errorMessage;
    @SerializedName("requestId")
    public final String requestId;
    @SerializedName("playerName")
    public final String playerName;

    public TradeResponseMessage(boolean success, String errorMessage, String requestId, String playerName) {
        super(MessageType.TRADE_RESPONSE);
        this.success = success;
        this.errorMessage = errorMessage;
        this.requestId = requestId;
        this.playerName = playerName;
    }
}

package org.dreambot.muling.messages.server;

import com.google.gson.annotations.SerializedName;
import org.dreambot.muling.messages.AbstractMessage;
import org.dreambot.muling.messages.MessageType;
import org.dreambot.muling.messages.Mule;

import java.util.List;

public class ListMulesResponseMessage extends AbstractMessage {
    @SerializedName("success")
    public final boolean success;
    @SerializedName("errorMessage")
    public final String errorMessage;
    @SerializedName("mules")
    public final List<Mule> mules;

    public ListMulesResponseMessage(boolean success, String errorMessage, List<Mule> mules) {
        super(MessageType.LIST_MULES_RESPONSE);
        this.success = success;
        this.errorMessage = errorMessage;
        this.mules = mules;
    }
}

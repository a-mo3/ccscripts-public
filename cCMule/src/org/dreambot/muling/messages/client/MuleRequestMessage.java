package org.dreambot.muling.messages.client;


import com.google.gson.annotations.SerializedName;
import org.dreambot.muling.OfferedItem;
import org.dreambot.muling.RequiredItem;
import org.dreambot.muling.messages.AbstractMessage;
import org.dreambot.muling.messages.MessageType;

import java.util.List;

public class MuleRequestMessage extends AbstractMessage {
    @SerializedName("requestId")
    public final String requestId;
    @SerializedName("requestedAt")
    public final long requestedAt;
    @SerializedName("playerName")
    public final String playerName;
    @SerializedName("hasMembership")
    public final boolean hasMembership;
    @SerializedName("requiredItems")
    public final List<RequiredItem> requiredItems;
    @SerializedName("offeredItems")
    public final List<OfferedItem> offeredItems;
    @SerializedName("muleName")
    public final String muleName; // mule name will now be used when making a request to identify the purpose of the request
    // when sent by the script, when sent back it needs to be the mules name

    public MuleRequestMessage(String requestId, long requestedAt, String playerName, boolean hasMembership, List<RequiredItem> requiredItems, List<OfferedItem> offeredItems, String muleName) {
        super(MessageType.MULE_REQUEST);
        this.requestId = requestId;
        this.requestedAt = requestedAt;
        this.playerName = playerName;
        this.hasMembership = hasMembership;
        this.requiredItems = requiredItems;
        this.offeredItems = offeredItems;
        this.muleName = muleName;
    }
}

package org.dreambot.muling.messages.client;

import com.google.gson.annotations.SerializedName;
import org.dreambot.muling.messages.AbstractMessage;
import org.dreambot.muling.messages.MessageType;
import org.dreambot.muling.messages.OwnedItem;

import java.util.List;

public class OwnedItemsUpdateMessage extends AbstractMessage {
    @SerializedName("ownedItems")
    public final List<OwnedItem> ownedItems;

    public OwnedItemsUpdateMessage(List<OwnedItem> ownedItems) {
        super(MessageType.OWNED_ITEMS_UPDATE);
        this.ownedItems = ownedItems;
    }
}

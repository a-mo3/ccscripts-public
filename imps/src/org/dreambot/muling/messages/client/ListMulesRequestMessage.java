package org.dreambot.muling.messages.client;


import org.dreambot.muling.messages.AbstractMessage;
import org.dreambot.muling.messages.MessageType;

public class ListMulesRequestMessage extends AbstractMessage {
    public ListMulesRequestMessage() {
        super(MessageType.LIST_MULES_REQUEST);
    }
}

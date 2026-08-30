package org.dreambot.muling;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.dreambot.muling.messages.MuleTile;
import org.dreambot.muling.messages.OwnedItem;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class Client {
    @SerializedName("conn")
    private final WebSocket conn;
    @SerializedName("connIndex")
    private final long connIndex;
    @SerializedName("connectedAt")
    private final long connectedAt;
    @SerializedName("address")
    private final String address;
    @SerializedName("clientUsername")
    private final String clientUsername;
    @SerializedName("groups")
    private final String[] groups;
    @SerializedName("queueSize")
    private final int queueSize;
    @SerializedName("playerName")
    private final String playerName;
    @SerializedName("isMule")
    private final boolean isMule;
    @SerializedName("isMember")
    private final boolean isMember;
    @SerializedName("worldId")
    private int worldId;
    @SerializedName("tile")
    private MuleTile tile;
    @SerializedName("ownedItems")
    private final List<OwnedItem> ownedItems = new ArrayList<>();

    public Client(WebSocket conn, long connIndex, long connectedAt, String clientUsername, String[] groups, int queueSize, String playerName, boolean isMule, boolean isMember) {
        this.conn = conn;
        this.connIndex = connIndex;
        this.connectedAt = connectedAt;
        this.address = conn.getRemoteSocketAddress().toString();
        this.clientUsername = clientUsername;
        this.groups = groups;
        this.queueSize = queueSize;
        this.playerName = playerName;
        this.isMule = isMule;
        this.isMember = isMember;
    }

    public boolean hasRequiredItems(List<RequiredItem> requiredItems, List<Request> requests) {
        Map<Integer, Integer> requestCounts = new HashMap<>();
        for (Request request : requests) {
            request.getMuleRequest().requiredItems.forEach(i ->
                    requestCounts.put(i.getItemId(), i.getQuantity() + requestCounts.getOrDefault(i.getItemId(), 0)));
        }

        for (RequiredItem requiredItem : requiredItems) {
            if (getOwnedItems().stream().noneMatch(ownedItem ->
            {
                int ownedQuantity = ownedItem.getQuantity() - requestCounts.getOrDefault(ownedItem.getItemId(), 0);
                return ownedItem.getItemId() == requiredItem.getItemId() && ownedQuantity >= requiredItem.getQuantity();
            })) {
                return false;
            }
        }

        return true;
    }

    public List<OwnedItem> getRemainingItems(List<Request> requests) {
        Map<Integer, Integer> requestCounts = new HashMap<>();
        for (Request request : requests) {
            request.getMuleRequest().requiredItems.forEach(i ->
                    requestCounts.put(i.getItemId(), i.getQuantity() + requestCounts.getOrDefault(i.getItemId(), 0)));
        }

        List<OwnedItem> remainingItems = new ArrayList<>();

        for (OwnedItem ownedItem : getOwnedItems()) {
            int ownedQuantity = ownedItem.getQuantity() - requestCounts.getOrDefault(ownedItem.getItemId(), 0);
            remainingItems.add(new OwnedItem(
                    ownedItem.getItemId(),
                    ownedQuantity
            ));
        }

        return remainingItems;
    }

    public String getLoggingPrefix() {
        return String.format("org.dreambot.muling.Client-%d", connIndex);
    }

    public boolean isInGroup(String[] checkGroups) {
        for (String group : groups) {
            for (String checkGroup : checkGroups) {
                if (group.equalsIgnoreCase(checkGroup)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Client && ((Client) other).connIndex == connIndex;
    }
}

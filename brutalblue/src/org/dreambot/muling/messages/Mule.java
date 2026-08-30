package org.dreambot.muling.messages;

import com.google.gson.annotations.SerializedName;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@Value
@ToString
public class Mule {
    @SerializedName("playerName")
    public String playerName;
    @SerializedName("groups")
    public String[] groups;
    @SerializedName("worldId")
    public int worldId;
    @SerializedName("tile")
    public MuleTile tile;
    @SerializedName("member")
    public boolean member;
    @SerializedName("ownedItems")
    public List<OwnedItem> ownedItems;
    @SerializedName("remainingItems")
    public List<OwnedItem> remainingItems;
    @SerializedName("queueSize")
    public int queueSize;
    @SerializedName("requestCount")
    public int requestCount;
}

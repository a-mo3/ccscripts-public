package org.dreambot.muling.messages;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnedItem {
    @SerializedName("itemId")
    private final int itemId;
    @SerializedName("quantity")
    private final int quantity;
}

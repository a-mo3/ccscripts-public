package org.dreambot.muling;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequiredItem {
    @SerializedName("itemId")
    private final int itemId;
    @SerializedName("quantity")
    private final int quantity;
}

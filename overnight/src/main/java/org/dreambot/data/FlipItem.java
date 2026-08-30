package org.dreambot.data;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class FlipItem {
    @SerializedName("itemId")
    int itemId;
    @SerializedName("sellPrice")
    int sellPrice; // high
    @SerializedName("buyPrice")
    int buyPrice; // low
    @SerializedName("allocationPercentage")
    int allocationPercentage; //
}

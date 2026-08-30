package org.dreambot.fractals.util;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class BuyLimitData {
    @SerializedName("itemID")
    int itemId;
    @SerializedName("brought")
    int quantityBrought;
    @SerializedName("firstBuyTimestamp")
    long firstBuyTimestamp;
}

package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.method.tanning.TanningHide;

@Data
@Unobfuscated
public class TannerSettings {
    @SerializedName("hideColor")
    public TanningHide selectedHide = TanningHide.SOFT_LEATHER;
    @SerializedName("restockQuantity")
    public int restock = 1200;
    @SerializedName("hidePriceIncrease")
    public float priceIncrease = 1f;
    @SerializedName("sellHideDiscount")
    public float discount = 0.99f;
}

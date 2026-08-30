package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.api.utilities.Logger;

@Data
@Unobfuscated
public class SettingsData {
    //    @SerializedName("muleOffItems")
//    public boolean muleOffItems;
    @SerializedName("initialGp")
    public int initalGp = 15_000_000;
    @SerializedName("moneyLeftAfterMuling")
    public int moneyLeftAfterMuling = 5_000_000;
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 4;
    @SerializedName("howManySetsToRestock")
    public int setsToRestock = 1200;
    @SerializedName("hideColor")
    public String hideColor = "green";
    @SerializedName("hidePriceIncrease")
    public float priceIncrease = 1f;
    @SerializedName("sellHideDiscount")
    public float discount = 0.99f;


    public float getPriceIncrease() {
        if (priceIncrease >= 2) {
            Logger.warn("Your price increase setting is > 2, 2 is the max and means it will double the price of hide when it cannot afford it, 1.1 for 10% increase, 1.2 for 20% etc..");
            return 2;
        }
        return priceIncrease;
    }
}

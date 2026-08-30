package org.dreambot.settings.script;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dreambot.api.wrappers.items.Item;

@Accessors(chain = true)
@ToString
public class AlchItem {
    public AlchItem(int itemID, int buyPrice, int buyQuantity) {
        this.itemID = itemID;
        this.buyPrice = buyPrice;
        this.buyQuantity = buyQuantity;
    }

    @Expose
    @SerializedName("itemID")
    public int itemID;
    @Expose
    @SerializedName("buyPrice")
    public int buyPrice;
    @Expose
    @SerializedName("buyQuantity")
    public int buyQuantity;
//    @Expose(serialize = false)
//    public int timeoutTime = 4 * 60 * 60 * 1000; // default 4 hours for buy time
//    @Expose(serialize = false)
//    public long timeoutTimestamp = -1;

    public int getTotalCost() {
        return buyPrice * buyQuantity;
    }
//
//    public void timeout() {
//        timeoutTimestamp = System.currentTimeMillis();
//    }
//
//    public boolean isTimedOut() {
//        return System.currentTimeMillis() - timeoutTimestamp > timeoutTime;
//    }

    public int getNotedId() {
        return new Item(itemID, 0).getNotedItemID();
    }
}

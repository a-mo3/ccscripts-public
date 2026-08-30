package org.dreambot.fractals.loadout;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.utilities.Logger;

import java.util.function.Supplier;

@Accessors(chain = true)
@Setter
@Getter
public abstract class LoadoutItem {
    ItemVariant variant = null;
    protected int itemId;
    protected int min;
    protected int max;
    protected int refill;
    protected int buyPrice;
    protected int priceIncreases = -1;
    protected Supplier<Boolean> enabledCondition = null;
    protected Supplier<Integer> idSupplier = null;
    protected int muleRequestAmount = 10_000; // the amount to mule if you cant afford the restock

    public int getBuyPrice() {
        if (buyPrice < 0 && Client.isLoggedIn() && Client.getGameStateID() >= 30) {
            Logger.info("looking up price");
            buyPrice = LivePrices.getHigh(variant == null ? getItemId() : variant.getBaseId());
        }

        if (idSupplier != null) buyPrice = LivePrices.getHigh(idSupplier.get());
        return buyPrice;
    }

    public int getItemId() {
        if (idSupplier != null) {
            return idSupplier.get();
        }
        if (variant != null) {
            return variant.getOwnedId();
        }
        return itemId;
    }
}

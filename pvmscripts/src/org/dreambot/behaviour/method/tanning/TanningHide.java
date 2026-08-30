package org.dreambot.behaviour.method.tanning;

import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.fractals.data.ItemID;

public enum TanningHide {
    SOFT_LEATHER(ItemID.COWHIDE, "soft", ItemID.LEATHER),
    //    HARD_LEATHER,
    GREEN_DHIDE(ItemID.GREEN_DRAGONHIDE, "green", ItemID.GREEN_DRAGON_LEATHER),
    BLUE_DHIDE(ItemID.BLUE_DRAGONHIDE, "blue", ItemID.BLUE_DRAGON_LEATHER),
    RED_DHIDE(ItemID.RED_DRAGONHIDE, "red", ItemID.RED_DRAGON_LEATHER),
    BLACK_DHIDE(ItemID.BLACK_DRAGONHIDE, "black", ItemID.BLACK_DRAGON_LEATHER),
    ;

    public final int precursorId;
    public final String label;
    public final int product;

    TanningHide(int id, String label, int product) {
        this.precursorId = id;
        this.label = label;
        this.product = product;
    }

    public int profit() {
        return LivePrices.get(product) - LivePrices.get(precursorId);
    }
}

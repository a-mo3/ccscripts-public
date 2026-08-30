package org.dreambot.behaviour.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.util.OwnedItems;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * model for item that should be muled off, with conditions for if it should be sold
 * this is for cases like revs using wildy weapons and also selling wildy weapons
 * also selling training supplies but only after a certain level has been achieved
 */
@Setter
@Getter
@Accessors(chain = true)
@AllArgsConstructor
public class MuleOffItem {
    int itemID;
    // true when should be sold
    Supplier<Boolean> enabledCondition = () -> true;
    // leave remaining, <= 0
    int remainingCount = 0;

    public boolean shouldSell() {
        return OwnedItems.count(itemID, true) > remainingCount && enabledCondition.get();
    }

    public MuleOffItem(int itemID) {
        this.itemID = itemID;
    }

    public boolean isEnabled() {
        if (enabledCondition == null) return true;
        return enabledCondition.get();
    }

    public static List<MuleOffItem> makeMuleItems(int[] ids) {
        return Arrays.stream(ids)
                .mapToObj(MuleOffItem::new)
                .collect(Collectors.toList());
    }

    public static List<MuleOffItem> makeMuleItems(int[] ids, MuleOffItem... items) {
        List<MuleOffItem> fromIDs = Arrays.stream(ids)
                .mapToObj(MuleOffItem::new)
                .collect(Collectors.toList());
        if (items != null) fromIDs.addAll(Arrays.stream(items).collect(Collectors.toList()));
        return fromIDs;
    }

    @Override
    public String toString() {
        return new Item(itemID, 1).getName() + " " + itemID;
    }
}

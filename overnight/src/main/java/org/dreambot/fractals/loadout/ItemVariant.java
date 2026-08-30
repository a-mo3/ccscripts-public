package org.dreambot.fractals.loadout;

import lombok.Getter;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.wrappers.items.Item;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ItemVariant {
    private final int baseId;
    private final List<Integer> ids;

    public ItemVariant(int baseId, Integer... ids) {
        this.baseId = baseId;
        this.ids = Arrays.stream(ids).collect(Collectors.toList());
        this.ids.add(baseId);
    }

    /**
     *
     */
    public int getOwnedId() {
        for (Integer id : ids) {
            if (Bank.contains(id) || Inventory.contains(id) || Equipment.contains(id)) return id;
        }
        return baseId;
    }

    public Integer[] getIds() {
        return ids.toArray(new Integer[0]);
    }

    public Item getItem() {
        for (Integer id : ids) {
            Item i = Inventory.get(id);
            if (i != null) return i;
        }
        return null;
    }

    public boolean interact(String action) {
        Item i = getItem();
        if (i == null) {
            return false;
        }
        return Inventory.interact(i, action);
    }
}

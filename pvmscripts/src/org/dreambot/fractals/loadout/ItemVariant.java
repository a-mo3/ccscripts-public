package org.dreambot.fractals.loadout;

import lombok.Getter;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.api.wrappers.items.Item;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ItemVariant {
    @Unobfuscated
    private final int baseId;
    @Unobfuscated
    private final List<Integer> ids;

    public ItemVariant(int baseId, Integer... ids) {
        this.baseId = baseId;
        this.ids = Arrays.stream(ids).collect(Collectors.toList());
        this.ids.add(baseId);
    }

    public int getInventoryCount() {
        return Inventory.count(x -> ids.contains(x.getId()));

    }

    public int getOwnedId() {
        int bestBank = -1;
        int bestInv = -1;
        // we actually need to check equipment, then inventory, then bank
        for (Integer id : ids) {
            if (Equipment.contains(id)) return id;
            if (Inventory.contains(id)) bestInv = id;
            if (Bank.contains(id)) bestBank = id;
        }
        if (bestInv > 0) return bestInv;
        if (bestBank > 0) return bestBank;
        return baseId;
    }

    public Integer[] getIds() {
        return ids.toArray(new Integer[0]);
    }

    public Item getItem() {
        Item i = Inventory.get(x -> ids.contains(x.getId()));
        return i;
    }

    public boolean interact(String action) {
        Item i = getItem();
        if (i == null) {
            return false;
        }
        return Inventory.interact(i, action);
    }


    public boolean contains(int id) {
        return ids.stream().anyMatch(x -> x == id);
    }
}

package org.dreambot.scout;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.api.wrappers.items.Item;

@Accessors(chain = true)
@Setter
@Getter
@Unobfuscated
public class EquipmentModel {
    @Unobfuscated
    int itemId;
    @Unobfuscated
    String itemName;
    @Unobfuscated
    EquipmentSlot slot;

    public EquipmentModel(Item x) {
        itemId = x.getID();
        itemName = x.getName();
        slot = EquipmentSlot.forSlotId(x.getSlot());
    }

    public EquipmentModel() {
        itemId = -1;
        itemName = "none";
        slot = null;
    }
}

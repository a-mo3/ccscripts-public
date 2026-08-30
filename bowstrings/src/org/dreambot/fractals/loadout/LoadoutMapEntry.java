package org.dreambot.fractals.loadout;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;

@AllArgsConstructor
@Data
@Accessors(chain = true)
public class LoadoutMapEntry {
    EquipmentSlot slot;
    EquipmentLoadoutItem item;
}

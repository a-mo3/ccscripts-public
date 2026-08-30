package org.dreambot.fractals.loadout;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.script.Unobfuscated;

@AllArgsConstructor
@Data
@Accessors(chain = true)
public class LoadoutMapEntry {
    @Unobfuscated
    EquipmentSlot slot;
    @Unobfuscated
    EquipmentLoadoutItem item;
}

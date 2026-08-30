package org.dreambot.behaviour.misc.tickcombat.decisions;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.fractals.TickDecision;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * some weapons need a specific index, because they have different styles for a skill, but require one for a boss
 * these are all determined by the category but im too lazy to grab all that data
 */
public class TickSetCombatStyle extends TickDecision {
    final CombatStyle style;
    final Supplier<CombatStyle> styleSupplier;

    public TickSetCombatStyle(CombatStyle style, Supplier<CombatStyle> styleSupplier) {
        this.style = style;
        this.styleSupplier = styleSupplier;
    }

    public TickSetCombatStyle(CombatStyle style) {
        this.style = style;
        this.styleSupplier = null;
    }

    public TickSetCombatStyle(Supplier<CombatStyle> styleSupplier) {
        this.style = null;
        this.styleSupplier = styleSupplier;
    }

    @Override
    public boolean evaluate() {
        CombatStyle current = Combat.getCombatStyle();
        if (current == null) return false;
        if (style != null && current != style) Combat.setCombatStyle(style);
        if (styleSupplier != null && current.equals(styleSupplier.get())) Combat.setCombatStyle(styleSupplier.get());
        return false;
    }
}

package org.dreambot.behaviour.method.scurrius;

import lombok.Setter;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.fractals.TickDecision;

import java.util.function.Supplier;

public class ManageCombatStylesScurrius extends TickDecision {
    @Setter
    Supplier<CombatStyle> styleSupplier;
    public ManageCombatStylesScurrius() {
    }

    @Override
    public boolean evaluate() {
        if (styleSupplier == null) return false;
        CombatStyle currentStyle = Combat.getCombatStyle();
        CombatStyle requiredStyle = styleSupplier.get();
        log("Combat style " + currentStyle + " - " + requiredStyle);
        if (currentStyle != requiredStyle) {
            log("Set combat style");
            Combat.setCombatStyle(requiredStyle);
        }
        return false;
    }
}

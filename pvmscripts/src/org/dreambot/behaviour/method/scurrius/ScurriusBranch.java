package org.dreambot.behaviour.method.scurrius;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.fractals.TickFractal;

import java.util.function.Supplier;

@Accessors(chain = true)
public class ScurriusBranch extends TickFractal {
    ManageCombatStylesScurrius combatStylesScurrius = new ManageCombatStylesScurrius();
    @Setter
    boolean flick = true;

    public ScurriusBranch(Supplier<Boolean> acceptCondition, ScurriusMode mode, boolean dropWhenLoot) {
        super(acceptCondition);

        addChildren(
                new ScurriusManagePrayer(flick, mode),
                new ScurriusEat(),
                new ScurriusPotion(mode),
                new ScurriusDodge(),
                combatStylesScurrius.setSimpleName("Combat style"),
                new ScurriusAttack(),
                new ScurriusLoot(dropWhenLoot)
        );
    }

    public ScurriusBranch setStyleSupplier(Supplier<CombatStyle> styleSupplier) {
        this.combatStylesScurrius.setStyleSupplier(styleSupplier);
        return this;
    }
}

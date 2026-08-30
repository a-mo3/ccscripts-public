package org.dreambot.behaviour.method.barrows.handlecrypt.decisions;

import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.behaviour.method.barrows.killbrothers.decisions.BrotherAttack;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;

public class BarrowsSetAutocast extends TickDecision {
    @Override
    public boolean evaluate() {
        Spell autocasted = Magic.getAutocastSpell();
        Spell correctSpell = BrotherAttack.getSpell();
        if (correctSpell != null
                && !Equipment.contains(ItemID.MAGIC_SHORTBOW) && !Equipment.isSlotEmpty(EquipmentSlot.WEAPON)
                && (autocasted == null || autocasted != correctSpell)) {
            log("Needs to switch autocast from " + autocasted + " to " + correctSpell);
            Magic.setAutocastSpell(BrotherAttack.getSpell());
            return true;
        }
        return false;
    }

    public static Spell getSpell() {
        Spell[] allowed = new Spell[]{
                Normal.WIND_STRIKE,
                Normal.WIND_BOLT,
                Normal.WIND_BLAST,
                Normal.WIND_WAVE,
                Normal.WIND_SURGE
        };

        return Arrays.stream(allowed)
                .filter(Magic::canCast)
                .reduce((f, s) -> s)
                .orElse(null);
    }
}

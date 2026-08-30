package org.dreambot.behaviour.method.huey;

import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.behaviour.method.barrows.killbrothers.decisions.BrotherAttack;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;

public class HueySetAutocast extends TickDecision {
    final HueyLoadout mode;
    public HueySetAutocast(HueyLoadout mode) {
        this.mode = mode;
    }

    @Override
    public boolean evaluate() {
        if (mode.getMode() != Skill.MAGIC) {
            return false;
        }
        Spell autocasted = Magic.getAutocastSpell();
        Spell correctSpell = getSpell();
        if (HueyData.isInHueyFight() && correctSpell == null) {
            log("Needs to leave");
            return HueyData.leaveFight();
        }

        if (correctSpell != null
                && !Equipment.contains(ItemID.MAGIC_SHORTBOW) && !Equipment.isSlotEmpty(EquipmentSlot.WEAPON)
                && (autocasted == null || autocasted != correctSpell)) {
            log("Needs to switch autocast from " + autocasted + " to " + correctSpell);
            Magic.setAutocastSpell(getSpell());
            return true;
        }
        return false;
    }

    public static Spell getSpell() {
        Spell[] allowed = new Spell[]{
                Normal.EARTH_STRIKE,
                Normal.EARTH_BOLT,
                Normal.EARTH_BLAST,
                Normal.EARTH_WAVE,
                Normal.EARTH_SURGE
        };

        return Arrays.stream(allowed)
                .filter(Magic::canCast)
                .reduce((f, s) -> s)
                .orElse(null);
    }
}

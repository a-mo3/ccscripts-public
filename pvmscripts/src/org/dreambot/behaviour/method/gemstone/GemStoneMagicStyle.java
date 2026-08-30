package org.dreambot.behaviour.method.gemstone;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.fractals.TickDecision;

import java.util.Arrays;

public class GemStoneMagicStyle extends TickDecision {
    final int def;

    public GemStoneMagicStyle(int def) {
        this.def = def;
    }

    @Override
    public boolean evaluate() {
        // todo check for items that dont use air spell, eg trident, iban staff

        if (Skill.DEFENCE.getLevel() < def) {
            if (Magic.isAutocastDefensive()) {
                log("Set magic def autocast");
                Combat.setCombatStyle(CombatStyle.MAGIC_DEFENCE);
            }
        } else {
            if (Magic.isAutocasting()) {
                log("magic normal autocast");
                Combat.setCombatStyle(CombatStyle.MAGIC);
            }
        }

        if (Magic.getAutocastSpell() == null || !Magic.canCast(Magic.getAutocastSpell())) {
            log("Set autocast spell");
            Spell correctSpell = getSpell();
            Magic.setAutocastSpell(correctSpell);
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

        return Arrays.stream(allowed).filter(Magic::canCast).reduce((f, s) -> s).orElse(null);
    }
}

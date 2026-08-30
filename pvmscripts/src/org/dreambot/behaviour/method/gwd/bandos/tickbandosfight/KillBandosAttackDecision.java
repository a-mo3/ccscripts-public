package org.dreambot.behaviour.method.gwd.bandos.tickbandosfight;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.gwd.bandos.BandosConsts;
import org.dreambot.behaviour.method.gwd.zilyana.ZilyanaConsts;
import org.dreambot.fractals.TickDecision;

public class KillBandosAttackDecision extends TickDecision {
    int lastAttackTick = 0;

    @Override
    public boolean evaluate() {
        if (lastAttackTick != 0 && Client.getGameTick() - lastAttackTick < 5) return false;

        // todo fetch bandos

        if (Inventory.contains(x -> !Equipment.contains(x.getId()) && BandosConsts.primaryWeapons.contains(x.getId()))) {
            log("Equpping primary");
            Inventory.interact(x -> !Equipment.contains(x.getId()) && BandosConsts.primaryWeapons.contains(x.getId()));
            return false;
        }

        if (Combat.getCombatStyle() != CombatStyle.RANGED_RAPID) {
            log("Needs to set range rapid before attacking");
            Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
            return false;
        }

        // todo attack if acceptable

        return false;
    }

}

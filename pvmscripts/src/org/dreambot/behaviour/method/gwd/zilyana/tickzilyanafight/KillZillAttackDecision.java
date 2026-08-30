package org.dreambot.behaviour.method.gwd.zilyana.tickzilyanafight;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.gwd.zilyana.ZilyanaConsts;
import org.dreambot.fractals.TickDecision;

public class KillZillAttackDecision extends TickDecision {
    int lastAttackTick = 0;

    @Override
    public boolean evaluate() {
        if (lastAttackTick != 0 && Client.getGameTick() - lastAttackTick < 5) return false;
        NPC zil = NPCs.closest("Commander Zilyana");
        NPC starlight = NPCs.closest("Starlight");
        if (zil == null && starlight == null) return false;
        double zilDist = -1;
        double starDist = -1;
        if (Inventory.contains(x -> !Equipment.contains(x.getId()) && ZilyanaConsts.primaryWeapons.contains(x.getId()))) {
            log("Equpping primary");
            Inventory.interact(x -> !Equipment.contains(x.getId()) && ZilyanaConsts.primaryWeapons.contains(x.getId()));
            return false;
        }

        if (Combat.getCombatStyle() != CombatStyle.RANGED_RAPID) {
            log("Needs to set range rapid before attacking");
            Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
            return false;
        }

        if (zil == null) {
            log("No Zilyana present");
        } else {
            zilDist = zil.getServerTile().distance(Players.getLocal().getServerTile());
            log("Zilyana Dist: " + zilDist);
        }

        if (starlight == null) {
            log("No Starlight present");
        } else {
            starDist = starlight.getServerTile().distance(Players.getLocal().getServerTile());
            log("Starlight Dist: " + starDist);
        }

        log("Last attacked on " + lastAttackTick);
        if (zil != null
                && (starDist < 7 && starDist > 4)
                && (zilDist < 7 && zilDist > 4)) {
            log("Shoot!");
            lastAttackTick = Client.getGameTick();
            log("Atk zil");
            zil.interact("Attack");
            return true;
        }

        if ((starDist < 7 && starDist > 4) && zil == null) {
            lastAttackTick = Client.getGameTick();
            log("Attack starlight");
            starlight.interact("Attack");
            return true;
        }

        return false;
    }

}

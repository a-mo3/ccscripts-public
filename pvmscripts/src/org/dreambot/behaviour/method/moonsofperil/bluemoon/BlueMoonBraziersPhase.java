package org.dreambot.behaviour.method.moonsofperil.bluemoon;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

/**
 * in this phase you
 */
public class BlueMoonBraziersPhase extends TickDecision {
    // the cyclones that spawn in the brazier phase
    public static final int CYCLONE_NPC_ID = 13027;

    public BlueMoonBraziersPhase() {
        setSimpleName("Blue moon braziers");
    }

    // the tile to wait on after braziers are lit
    public static final Tile AWAIT_TILE = new Tile(1439, 9677);

    @Override
    public boolean evaluate() {
        boolean inPhase = NPCs.all().stream().anyMatch(x -> x.getRealID() == CYCLONE_NPC_ID);
        if (!inPhase) {
            return false;
        }

        Prayers.toggleQuickPrayer(false);

        if (Walking.getRunEnergy() > 10 && !Walking.isRunEnabled()) Walking.toggleRun();

        NPC shine = NPCs.closest(BlueMoonAttackPhase.ATK_PHASE_SHINE_NPC_ID);
        if (shine != null) {
            log("Brazier phase ending");
            if (!shine.getArea().contains(Players.getLocal().getServerTile())) {
                log("Walk onto shine");
                Walking.walk(shine.getTile());
            }
            return true;
        }

        // i dont think its worth trying to dodge them, you dont get hit very heavily
        // this is a no DPS phase, so lets eat to full
        int healAmount = (int) (Math.min(Skill.COOKING.getLevel(), Skill.FISHING.getLevel()) * 0.3);
        int missingHealth = Skill.HITPOINTS.getLevel() - Skill.HITPOINTS.getBoostedLevel();
        // you can walk and eat in the same tick so this should be fine
        if (healAmount <= missingHealth) {
            log("Brazier phase safe to eat");
            Inventory.interact(ItemID.COOKED_BREAM);
        }

        GameObject brazier = GameObjects.closest(x -> x.hasAction("Light"));
        if (brazier == null) {
            log("Return to center");
            Walking.walkExact(AWAIT_TILE);
        } else {
            log("Light brazier");
            brazier.interact("Light");
        }
        return true;
    }
}

package org.dreambot.behaviour.quests.ascentofarceuus;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.misc.CombatLoadouts;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class KillTrappedSoul extends Fractal {
    public KillTrappedSoul(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Kill Trapped Souls");
        setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_P2P);
        setInventoryLoadout(new InventoryLoadout()
                .addItem(ItemID.SHARK, 1, 16)
                .addItem(ItemVariants.SKILLS_NECKLACE)
        );
    }

    Tile t = new Tile(1283, 3728, 0);

    @Override
    public int onLoop() {
        if (!Combat.isAutoRetaliateOn()) {
            log("Turn on auto realiate");
            Combat.toggleAutoRetaliate(true);
            return ReactionGenerator.getNormal();
        }

        if (t.distance() > 10) {
            if (Walking.shouldWalk()) Walking.walk(t);
            return ReactionGenerator.getNormal();
        }

        // eat
        if (Combat.getHealthPercent() <= 40) {
            log("Eat Shark");
            Inventory.interact(ItemID.SHARK);
        }

        // fight souls
        if (!Players.getLocal().isInCombat()) {
            NPC soul = NPCs.closest("Trapped Soul");
            if (soul != null) {
                log("Attack soul");
                soul.interact("Attack");
                Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 4400);
            } else {
                log("Cant find soul");

                GameObject plant = GameObjects.closest(34625);
                if (plant != null) {
                    plant.interact("Inspect");
                    Sleep.sleep(1200);
                }
            }
        } else {
            log("in combat");
        }
        return ReactionGenerator.getNormal();
    }
}

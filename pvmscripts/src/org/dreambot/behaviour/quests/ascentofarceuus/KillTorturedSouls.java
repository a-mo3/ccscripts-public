package org.dreambot.behaviour.quests.ascentofarceuus;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
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
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class KillTorturedSouls extends Fractal {
    public KillTorturedSouls(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Kill Tortured Souls");
        setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_P2P);
        setInventoryLoadout(new InventoryLoadout()
                .addItem(ItemID.SHARK, 1, 16)
        );
    }

    Tile t = new Tile(1596, 3820, 0);

    @Override
    public int onLoop() {
        if (!Combat.isAutoRetaliateOn()) {
            log("Turn on auto realiate");
            Combat.toggleAutoRetaliate(true);
            return ReactionGenerator.getNormal();
        }

        if (!Client.isDynamicRegion()) {
            if (Dialogues.inDialogue()) {
                Dialog.solve("Yes");
                return ReactionGenerator.getNormal();
            }

            GameObject door = GameObjects.closest(33570);
            if (door != null) {
                log("Open door");
                door.interact("Open");
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
                return ReactionGenerator.getNormal();
            }

            log("Go to tower");
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
            NPC soul = NPCs.closest("Tormented Soul");
            if (soul != null) {
                log("Attack soul");
                soul.interact("Attack");
                Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 4400);
            } else {
                log("Cant find souls");
            }
        } else {
            log("in combat");
        }
        return ReactionGenerator.getNormal();
    }
}

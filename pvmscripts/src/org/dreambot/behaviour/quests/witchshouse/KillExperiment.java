package org.dreambot.behaviour.quests.witchshouse;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class KillExperiment extends Fractal {
    static Area INSIDE_SHED = new Area(2934, 3467, 2937, 3459);
    Tile safeSpot = new Tile(2936, 3459);

    public KillExperiment() {
        super(() -> INSIDE_SHED.contains(Players.getLocal()));
    }

    @Override
    public int onLoop() {
        if (!safeSpot.equals(Players.getLocal().getTile())) {
            if (Walking.shouldWalk()) Walking.walkExact(safeSpot);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            // level up dialogue
            Dialog.solve();
        }

        if (Combat.getHealthPercent() < 50 && Inventory.contains(ItemID.SALMON)) {
            Inventory.interact(ItemID.SALMON);
        }

        if (Magic.getAutocastSpell() != Normal.FIRE_BOLT) {
            log("Auto cast fire bolt");
            Magic.setAutocastSpell(Normal.FIRE_BOLT);
            return ReactionGenerator.getNormal();
        }

        if (Players.getLocal().getInteractingCharacter() == null) {
            NPC exper = NPCs.closest(x -> x.getName().contains("Witch's experiment"));
            if (exper == null) {
                log("Cant find npc, take ball");
                if (Inventory.contains("Ball")) {
                    log("Got ball TP out");
                    Inventory.interact(ItemID.FALADOR_TELEPORT);
                    return ReactionGenerator.getNormal();
                }

                GroundItem ball = GroundItems.closest("Ball");
                if (ball != null) ball.interact("Take");
                return ReactionGenerator.getNormal();
            }

            exper.interact("Attack");
        }
        return ReactionGenerator.getNormal();
    }
}

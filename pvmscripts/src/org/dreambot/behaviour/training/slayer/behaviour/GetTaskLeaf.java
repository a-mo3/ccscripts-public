package org.dreambot.behaviour.training.slayer.behaviour;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class GetTaskLeaf extends Fractal {
    private static final Area TURAEL_HOUSE = new Area(2930, 3538, 2933, 3535);
    public static final Area NIEVE = new Area(2430, 3426, 2434, 3421);

    @Override
    public boolean isValid() {
        return SlayerBranch.getSlayerTaskKey() <= 0 || SlayerBranch.getQuantityRemaining() <= 0;
    }

    @Override
    public int onLoop() {
        // gem and dialogue checks not needed updated to just use the varbits
        if (!getSlayerMasterArea().contains(Players.getLocal())) {
            log("Going to slayer master");
            if (Walking.shouldWalk(8)) Walking.walk(getSlayerMasterArea());
            return ReactionGenerator.getQuick();
        }

        if (Dialogues.inDialogue()) {
            log("Handle dialogue");
            Dialog.solve("What's a slayer?", "can you teach me?", "Okay, great!");
            Sleep.sleepTicks(2);
            if (PlayerSettings.getConfig(394) > 0) {
                Widgets.closeAll();
                Inventory.interact(ItemID.ENCHANTED_GEM, "Check"); // RangeSlayerBranch listener will update ur task
            }
            return ReactionGenerator.getQuick();
        }

        NPC turael = getSlayerMaster();
        if (!Dialogues.inDialogue() && turael != null && turael.interact("Assignment")) {
            log("Talk to turael");
            Sleep.sleepUntil(Dialogues::inDialogue, 5000);
        }
        return ReactionGenerator.getQuick();
    }

    private NPC getSlayerMaster() {
        return Combat.getCombatLevel() >= 85 ? NPCs.closest(x -> x.getName().contains("eve")) : NPCs.closest("Turael");
    }

    private Area getSlayerMasterArea() {
        return Combat.getCombatLevel() >= 85 ? NIEVE : TURAEL_HOUSE;
    }
}

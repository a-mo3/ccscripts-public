package org.dreambot.behaviour.training.doricsquest;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class DoricsQuest extends Fractal {
    public DoricsQuest() {
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.CLAY, 6)
                .addItem(ItemID.COPPER_ORE, 4)
                .addItem(ItemID.IRON_ORE, 2)
                .addItem(ItemID.FALADOR_TELEPORT, 1, 5)
        ;
    }

    @Override
    public boolean isValid() {
        return PlayerSettings.getConfig(31) != 100 && Skills.getRealLevel(Skill.MINING) < 10;
    }

    private final Area DORICSHOUSE = new Area(2950, 3452, 2953, 3449, 0);

    /**
     * item reqs all unnoted
     * 6 clay
     * 4 copper ore
     * 2 iron ore
     * varplayer is 31
     */
    @Override
    public int onLoop() {
        if (Inventory.contains(x -> x.getName().toLowerCase().contains("combat braclet"))) {
            Inventory.interact(x -> x.getName().toLowerCase().contains("skills necklace"), "Wear");
        }
        if (Inventory.contains(x -> x.getName().toLowerCase().contains("combat braclet"))) {
            Inventory.interact(x -> x.getName().toLowerCase().contains("combat braclet"), "Wear");
        }

        FractalAPI.status = "doings dorics quest";
        if (!DORICSHOUSE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(DORICSHOUSE.getRandomTile());
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("use your anvils", "yes", "the materials");
            return ReactionGenerator.getNormal();
        }

        NPC doric = NPCs.closest("Doric");
        if (doric != null && doric.interact()) {
            Sleep.sleepUntil(Dialogues::inDialogue, 5000);
        }
        return ReactionGenerator.getNormal();
    }
}

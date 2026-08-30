package org.dreambot.behaviour.method.lizardmen;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.PaintListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class ShayzienMedpacks extends Fractal {
    final Area INFIRMARY = new Area(1507, 3626, 1530, 3612);

    final int INJURED_POSE = 6282;

    public ShayzienMedpacks(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemVariants.SKILLS_NECKLACE)
                .addItem(ItemVariants.STAMINA_POTION, 1, 5);
        setSimpleName("Medpacks for favor");
    }

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            Dialog.solve("");
        }

        if (!INFIRMARY.contains(Players.getLocal())) {
            log("Go to infirmary");
            if (Walking.shouldWalk()) Walking.walk(INFIRMARY);
            return ReactionGenerator.getNormal();
        }

        // get med packss
        if (!Inventory.contains(ItemID.SHAYZIEN_MEDPACK)) {
            log("Get medpacks");
            GameObject medpacks = GameObjects.closest("Medpack Box");
            if (medpacks != null && medpacks.interact("Take-many")) {
                Sleep.sleepUntil(() -> Inventory.contains(ItemID.SHAYZIEN_MEDPACK), 2400);
            }
            return ReactionGenerator.getNormal();
        }


        NPC woundedSoldier = NPCs.closest(x -> x.getName().equals("Wounded soldier")
                && x.getAnimation() == INJURED_POSE);
        if (woundedSoldier != null) {
            log("Heal soldier");
            Inventory.get(ItemID.SHAYZIEN_MEDPACK).useOn(woundedSoldier);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}

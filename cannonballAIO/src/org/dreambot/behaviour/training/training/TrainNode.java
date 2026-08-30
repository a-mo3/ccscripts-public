package org.dreambot.behaviour.training.training;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.Smithing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

public class TrainNode extends Fractal {
    private final Area TRAINING_AREA = new Area(3190, 3424, 3181, 3440, 0);
    // safe area is used to bypass white wolf mountain so you dont die.
    private final Area SAFE_AREA = new Area(2998, 3530, 3305, 3370);

    @Override
    public boolean isValid() {
        return PlayerSettings.getConfig(0) == 11
                && Skills.getRealLevel(Skill.SMITHING) >= 29
                && Skills.getRealLevel(Skill.SMITHING) < 35;
    }

    public TrainNode() {
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.HAMMER)
                .addItem(ItemID.IRON_BAR, 3, 27).setRefill(400)
                .setStrict(true)
        ;
    }

    @Override
    public int onLoop() {
        FractalAPI.status = "Training to level 35, current lvl: " + Skills.getRealLevel(Skill.SMITHING);

        if (!TRAINING_AREA.contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(TRAINING_AREA.getCenter());
            return ReactionGenerator.getNormal();
        }

        if (!Smithing.isOpen()) {
            GameObject anvil = GameObjects.closest("Anvil");
            if (anvil != null && anvil.interact()) {
                Sleep.sleepUntil(Smithing::isOpen, 5000);
            }
            return ReactionGenerator.getNormal();
        }

        if (Smithing.makeAll("Iron 2h sword")) {
            Sleep.sleepUntil(() -> Inventory.count("Iron bar") < 3 || Dialogues.inDialogue(),
                    () -> Players.getLocal().isAnimating(),
                    4500, 100);
        }
        return ReactionGenerator.getNormal();
    }
}

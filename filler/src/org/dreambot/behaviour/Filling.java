package org.dreambot.behaviour;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class Filling extends Fractal {
    public Filling(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ScriptSettings.getSettingsData().fillMode.getId(), 1, 28)
                .setRefill(ScriptSettings.getSettingsData().restockQuantity)
        ;
    }

    final Area VARROCK_PUMP = new Area(2946, 3384, 2952, 3382);

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) Dialog.solve("");
        if (!Equipment.isEmpty()) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            Bank.depositAllEquipment();
            return ReactionGenerator.getNormal();
        }

        if (!VARROCK_PUMP.contains(Players.getLocal())) {
            if (Walking.shouldWalk(4)) Walking.walk(VARROCK_PUMP);
            return ReactionGenerator.getNormal();
        }

        GameObject pump = GameObjects.closest("Waterpump");
        Item fillable = Inventory.get(ScriptSettings.getSettingsData().fillMode.getId());

        if (fillable != null && pump != null) {
            fillable.useOn(pump);
            Sleep.sleepUntil(() -> !Inventory.contains(ScriptSettings.getSettingsData().fillMode.getId()),
                    () -> Players.getLocal().isAnimating(),
                    1200,
                    100
            );
        }

        return ReactionGenerator.getNormal();
    }
}

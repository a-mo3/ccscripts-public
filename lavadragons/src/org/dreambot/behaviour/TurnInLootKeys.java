package org.dreambot.behaviour;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

public class TurnInLootKeys extends Fractal {
    public static final Area FEROX_BANK = new Area(3129, 3632, 3132, 3629, 0);
    public static final Area CHEST = new Area(3138, 3628, 3143, 3626);

    public TurnInLootKeys() {
        this.acceptCondition = () -> ScriptSettings.getSettingsData().turnInKeys && Inventory.contains("Loot key") && !Combat.isInWild();
    }

    @Override
    public int onLoop() {
        if (!CHEST.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(CHEST);
            return ReactionGenerator.getNormal();
        }

        WidgetChild value = Widgets.get(x -> x.getText().contains("Value in chest:"));
        WidgetChild toBank = Widgets.get(742, 34);
        if (toBank != null) {
            toBank.interact();
            Sleep.sleep(3000);
            Widgets.closeAll();
            return ReactionGenerator.getNormal();
        }

        Item key = Inventory.get("Loot key");
        GameObject chest = GameObjects.closest("Loot Chest");
        if (key != null && chest != null) {
            key.useOn(chest);
            Sleep.sleep(2_000);
        }
        return ReactionGenerator.getNormal();
    }
}

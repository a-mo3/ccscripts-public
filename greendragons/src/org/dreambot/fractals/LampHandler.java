package org.dreambot.fractals;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;

// i pinch from youler
public class LampHandler extends Fractal {
    public static Skill skill = Skill.CRAFTING;
    int[] lampIds = new int[]{
            23072,
            21262,
            2528 // genie lamp
    };

    public LampHandler() {
        this.acceptCondition = () -> Inventory.contains(lampIds);
    }


    @Override
    public int onLoop() {
        WidgetChild wc = skillTextWidget();
        if (wc != null && wc.isVisible()) {
            WidgetChild c = getConfirmWidgetText();

            if (c != null && c.getText().toLowerCase().contains(skillToLevel())) {
                WidgetChild confirmButton = getConfirmButton();
                Logger.info("confirm button: " + confirmButton);
                if (confirmButton != null && confirmButton.interact()) {
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }

            WidgetChild w = Widgets.get(s -> s.getParentID() == 240 && s.hasAction(skillToLevel()));
            Logger.info("W = " + w);
            if (w != null && w.interact()) {
                return ReactionGenerator.getNormal();
            } else {
                Item lamp = Inventory.get(x -> Arrays.stream(lampIds).anyMatch(i -> i == x.getID()) || x.getName().toLowerCase().contains("lamp"));
                Logger.info("Rub Lamp");
                if (Inventory.interact(lamp)) {
                    return ReactionGenerator.getNormal();
                }
            }

            return ReactionGenerator.getNormal();
        }

        if (Widgets.isOpen()) {
            Logger.info("CloseAll");
            Widgets.closeAll();
        }

        Item lamp = Inventory.get(x -> Arrays.stream(lampIds).anyMatch(i -> i == x.getID()) || x.getName().toLowerCase().contains("lamp"));
        Logger.info("Rub Lamp");
        if (Inventory.interact(lamp)) {
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }

    private WidgetChild skillTextWidget() {
        return Widgets.get(240, 25);
    }

    private WidgetChild getConfirmWidgetText() {
        return Widgets.get(240, 26, 0);
    }

    private WidgetChild getConfirmButton() {
        return Widgets.get(s -> s.hasAction("Confirm"));
    }

    private String skillToLevel() {
        return skill.getName().toLowerCase();
    }

}

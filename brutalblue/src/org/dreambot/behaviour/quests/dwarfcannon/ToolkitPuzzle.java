package org.dreambot.behaviour.quests.dwarfcannon;


import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.quest.VarbitRequirement;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class ToolkitPuzzle extends Fractal {
    public ToolkitPuzzle(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    VarbitRequirement springFixed = new VarbitRequirement(2239, 1);
    VarbitRequirement safetyFixed = new VarbitRequirement(2238, 1);
    VarbitRequirement cannonFixed = new VarbitRequirement(2235, 1);

    // excuse the naming conventions idc
    VarbitRequirement PLIERS_SELECTED = new VarbitRequirement(2236, 1);
    VarbitRequirement HOOKED_SELECTED = new VarbitRequirement(2237, 1);
    VarbitRequirement WRENCH_SELECTED = new VarbitRequirement(2235, 1);

    final int PUZZLE_PARENT = 409;
    final int PLIERS_CHILD = 2;
    final int SWITCH_CHILD = 7; // you could and should use names here but i refuse

    final int HOOKED_TOOL_CHILD = 3;
    final int SPRING_CHILD = 8;

    final int WRENCH_CHILD = 1;
    final int GEAR_CHILD = 9;


    @Override
    public int onLoop() {
        if (Widgets.getWidget(PUZZLE_PARENT) == null || !Widgets.getWidget(PUZZLE_PARENT).isVisible()) {
            Item toolkit =  Inventory.get(ItemID.TOOLKIT);
            GameObject multicannon = GameObjects.closest("Broken multicannon");
            if (toolkit != null && multicannon != null) {
                toolkit.useOn(multicannon);
                Sleep.sleepUntil(() -> Widgets.getWidget(PUZZLE_PARENT) == null, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        if (!safetyFixed.isComplete()) {
            if (!PLIERS_SELECTED.isComplete()) {
                WidgetChild pliers = Widgets.get(PUZZLE_PARENT, PLIERS_CHILD);
                if (pliers != null) {
                    // fuck ya mudda
                    Mouse.click(pliers.getRectangle().getLocation());
                    Sleep.sleepUntil(PLIERS_SELECTED::isComplete, 4400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }
            WidgetChild safety = Widgets.get(PUZZLE_PARENT, SWITCH_CHILD);
            if (safety != null) {
                safety.interact("Safety switch");
                Sleep.sleepUntil(safetyFixed::isComplete, 4400);
            }
            return ReactionGenerator.getNormal();
        }

        if (!springFixed.isComplete()) {
            if (!HOOKED_SELECTED.isComplete()) {
                WidgetChild hooked = Widgets.get(PUZZLE_PARENT, HOOKED_TOOL_CHILD);
                if (hooked != null) {
                    // fuck ya mudda
                    Mouse.click(hooked.getRectangle().getLocation());
                    Sleep.sleepUntil(HOOKED_SELECTED::isComplete, 4400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }
            WidgetChild safety = Widgets.get(PUZZLE_PARENT, SPRING_CHILD);
            if (safety != null) {
                Mouse.click(safety.getRectangle().getLocation());
                Sleep.sleepUntil(springFixed::isComplete, 4400);
            }
            return ReactionGenerator.getNormal();
        }

        Logger.info("Doing gear");
        if (!WRENCH_SELECTED.isComplete()) {
            WidgetChild wrench = Widgets.get(PUZZLE_PARENT, WRENCH_CHILD);
            if (wrench != null) {
                // fuck ya mudda
                Mouse.click(wrench.getRectangle().getLocation());
                Sleep.sleepUntil(WRENCH_SELECTED::isComplete, 4400);
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }
        WidgetChild gear = Widgets.get(x -> x.getParentID() == PUZZLE_PARENT && x.getTooltip().contains("Gear"));
        Logger.info(gear);
        if (gear != null) {
            Mouse.click(gear.getRectangle().getLocation());
            Sleep.sleepUntil(cannonFixed::isComplete, 4400);
        }

        return ReactionGenerator.getNormal();
    }
}

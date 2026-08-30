package org.dreambot.behaviour.foundry.leafs;


import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.foundry.data.FoundryTask;
import org.dreambot.behaviour.foundry.data.Heat;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.function.Supplier;

public class DoFoundryLeaf extends Fractal {
    // start point of the quest, used to go back
    private double heatRangeRatio = 0;
    Supplier<Integer> heatAmount = () -> PlayerSettings.getBitValue(13948);
    Supplier<WidgetChild> helpfulWidget = () -> Widgets.get(754, 76); //
    Supplier<WidgetChild> activeTask = () -> Widgets.get(x -> helpfulWidget.get() != null
            && helpfulWidget.get().getRectangle().contains(x.getRectangle())
            // todo i dont know what the replacement for sprite id is on dreambot
            && (x.getTextureId() == FoundryTask.HAMMER.spriteID
            || x.getTextureId() == FoundryTask.GRINDSTONE.spriteID
            || x.getTextureId() == FoundryTask.POLISH.spriteID));
    private final int BLADE_COMPLETION = 13949;
    Supplier<Boolean> isSwordFinished = () -> PlayerSettings.getBitValue(BLADE_COMPLETION) == 1000;

    @Override
    public boolean isValid() {
        Logger.info("hitting do foundry");
        return true;
    }

    @Override
    public int onLoop() {
        setStatus("Doing foundry");
        Logger.info("Doing foundry");
        // go back from restoc

        if (Walking.getRunEnergy() > 30 && !Walking.isRunEnabled()) {
            Walking.toggleRun();
        }

        if (PlayerSettings.getBitValue(BLADE_COMPLETION) == 1000
                || PlayerSettings.getBitValue(BLADE_QUALITY) == 0) {
            if (Dialogues.inDialogue()) {
                Dialog.solve("Yes.");
                return ReactionGenerator.getNormal();
            }

            NPC kovac = NPCs.closest("Kovac");
            if (kovac != null && kovac.interact("Talk-to")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        Logger.info("Heat from math - " + getCurrentHeat().name() + " Current heat: " + heatAmount.get() + " " + Arrays.toString(getMedHeatRange()));

        if (activeTask == null) {
            Logger.info("Problem: active task is null");
            return ReactionGenerator.getNormal();
        }

        // DO THE FOUNDRY
        FoundryTask taskThisLoop = getCurrentTask(activeTask.get());
        if (getCurrentHeat().equals(getCurrentTask(activeTask.get()).heat)) {
            switch (getCurrentTask(activeTask.get())) {
                case HAMMER:
                    GameObject hammer = GameObjects.closest("Trip hammer");
                    if (hammer != null && hammer.interact("Use")) {
                        Sleep.sleepUntil(() -> (heatAmount.get() < getHighHeatRange()[0] + 20)
                                || !getCurrentTask(activeTask.get()).equals(taskThisLoop)
                                || isSwordFinished.get(), 50_000);
                    }
                    break;
                case GRINDSTONE:
                    GameObject grindStone = GameObjects.closest("Grindstone");
                    if (grindStone != null && grindStone.interact("Use")) {
                        Sleep.sleepUntil(() -> heatAmount.get() >= getMedHeatRange()[1] - 20
                                || !getCurrentTask(activeTask.get()).equals(taskThisLoop)
                                || isSwordFinished.get(), 60_000);
                    }
                    break;
                case POLISH:
                    GameObject polishingWheel = GameObjects.closest("Polishing wheel");
                    if (polishingWheel != null && polishingWheel.interact("Use")) {
                        Sleep.sleepUntil(() -> heatAmount.get() <= getLowHeatRange()[0] + 20
                                || !getCurrentTask(activeTask.get()).equals(taskThisLoop)
                                || isSwordFinished.get(), 30_000);
                    }
                    break;
            }
            return ReactionGenerator.getNormal();
        }

        // heat is not right, cool or heat the preform
        GameObject lavaPool = GameObjects.closest("Lava pool");
        GameObject waterfall = GameObjects.closest("Waterfall");
        switch (getCurrentTask(activeTask.get())) {
            case GRINDSTONE:
                // grind stone increase heat, so if higher that low / 0 waterfall until low
                // if lower than low lava.
                // todo review this
                if (heatAmount.get() >= getMedHeatRange()[0] + 20) {
                    if (waterfall != null && waterfall.interact("Quench-preform")) {
                        Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 4000);
                        Sleep.sleepUntil(() -> heatAmount.get() <= getMedHeatRange()[0], 30_000);
                    }
                } else {
                    if (lavaPool != null && lavaPool.interact("Dunk-preform")) {
                        Sleep.sleepUntil(() -> heatAmount.get() >= getMedHeatRange()[0], 30_000);
                    }
                }
                break;
            case POLISH:
                // heat up sword when it gets too low
                if (heatAmount.get() <= getLowHeatRange()[1]) {
                    if (lavaPool != null && lavaPool.interact("Dunk-preform")) {
                        Sleep.sleepUntil(() -> heatAmount.get() >= getLowHeatRange()[1], 30_000);
                    }
                } else {
                    if (waterfall != null && waterfall.interact("Quench-preform")) {
                        Sleep.sleepUntil(() -> heatAmount.get() <= getLowHeatRange()[1], 30_000);
                    }
                }
                break;
            case HAMMER:
                if (heatAmount.get() <= getHighHeatRange()[1] - 20) {
                    if (lavaPool != null && lavaPool.interact("Dunk-preform")) {
                        Sleep.sleepUntil(() -> heatAmount.get() >= getHighHeatRange()[1] - 20, 30_000);
                    }
                } else {
                    if (waterfall != null && waterfall.interact("Quench-preform")) {
                        Sleep.sleepUntil(() -> heatAmount.get() <= getHighHeatRange()[1] - 20, 30_000);
                    }
                }
                break;
        }

        return Calculations.random(100, 200);
    }

    private static final int WIDGET_MED_HEAT_PARENT = 49414164;
    private static final int WIDGET_HEAT_PARENT = 49414153;

    private double getHeatRangeRatio() {
        if (heatRangeRatio == 0) {
            WidgetChild heatWidget = Widgets.get(w -> w.getRealID() == WIDGET_HEAT_PARENT);
            WidgetChild medHeat = Widgets.get(w -> w.getRealID() == WIDGET_MED_HEAT_PARENT);
            if (medHeat == null || heatWidget == null) {
                return 600;
            }
            heatRangeRatio = medHeat.getWidth() / (double) heatWidget.getWidth();
        }
//        Logger.info("Setting heat range ratio = " + heatRangeRatio);
        return heatRangeRatio;
    }

    public int[] getHighHeatRange() {
        return new int[]{
                (int) ((5 / 6d - getHeatRangeRatio() / 2) * 1000),
                (int) ((5 / 6d + getHeatRangeRatio() / 2) * 1000),
        };
    }

    public int[] getLowHeatRange() {
        return new int[]{
                (int) ((1 / 6d - getHeatRangeRatio() / 2) * 1000),
                (int) ((1 / 6d + getHeatRangeRatio() / 2) * 1000),
        };
    }

    public int[] getMedHeatRange() {
        return new int[]{
                (int) ((3 / 6d - getHeatRangeRatio() / 2) * 1000),
                (int) ((3 / 6d + getHeatRangeRatio() / 2) * 1000),
        };
    }

    public Heat getCurrentHeat() {
        int heat = heatAmount.get();
        // need a buffer to stop it from going to straight up 0
        int buffer = 50;
        int lowBuffer = 20;

        int[] low = getLowHeatRange();
        if (heat > low[0] + buffer && heat < low[1]) {
            return Heat.LOW;
        }

        int[] med = getMedHeatRange();
        if (heat > med[0] + lowBuffer && heat < med[1] - buffer) {
            return Heat.MED;
        }

        int[] high = getHighHeatRange();
        if (heat > high[0] + buffer && heat < high[1] - 20) {
            return Heat.HIGH;
        }
        return Heat.NONE;
    }

    private int[] getHeatRange(Heat heat) {
        switch (heat) {
            case HIGH:
                return getHighHeatRange();
            case MED:
                return getMedHeatRange();
            case LOW:
                return getLowHeatRange();
        }
        return null;
    }

    private FoundryTask getCurrentTask(WidgetChild widgetChild) {
        if (widgetChild != null) {
            switch (widgetChild.getTextureId()) {
                case 4442:
                    return FoundryTask.HAMMER;
                case 4443:
                    return FoundryTask.GRINDSTONE;
                case 4444:
                    return FoundryTask.POLISH;
            }
        }
        return FoundryTask.NONE;
    }

    private final int BLADE_HEAT = 13948;
    private final int BLADE_QUALITY = 13939;

}

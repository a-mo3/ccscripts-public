package org.dreambot.behaviour.method.antipk;

import org.dreambot.api.Client;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.randoms.RandomManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * hop worlds to remove TB or when out of wild
 */
public class AntiPkWorldHop extends Fractal {
    boolean shouldReport = false;
    boolean hasReported = false;

    public AntiPkWorldHop(Supplier<Boolean> acceptCondition, boolean shouldReportPlayer) {
        super(acceptCondition);
        this.shouldReport = shouldReportPlayer;
    }

    @Override
    public int onLoop() {
        // i want to check wildy state before hopping so i dont check something while in connecting state
        if (!Combat.isInWild()) {
            if (Dialogues.inDialogue()) {
                Dialog.solve();
            }
            // i think for this to exec name would need to be null but i cant always be sure
            if (!hasReported && shouldReport && AntiPkBranch.getAttackerName() != null) {
                WidgetChild reportButton = Widgets.get(162, 31);
                WidgetChild nameBox = Widgets.get(875, 4);
                WidgetChild macroReasonButton = Widgets.get(875, 19, 2);
                if (macroReasonButton != null && macroReasonButton.isVisible()) {
                    log("Reason macro");
                    macroReasonButton.interact();
                    hasReported = true;
                    return ReactionGenerator.getNormal();
                }

                if (nameBox != null && nameBox.isVisible()) {
                    log("Type pker name " + AntiPkBranch.getAttackerName());
                    Keyboard.type(AntiPkBranch.getAttackerName(), true);
                    Sleep.sleepUntil(() -> Widgets.get(875, 19, 2) != null, 4000);
                    return ReactionGenerator.getNormal();
                }

                if (reportButton != null && reportButton.isVisible()) {
                    reportButton.interact();
                    Sleep.sleep(2000);
                }
                return ReactionGenerator.getNormal();
            }


            if (WorldHopper.hopWorld(Worlds.getRandomWorld(w -> w.isNormal() && w.isMembers() && w.getMinimumLevel() == 0))) {
                Logger.info("Not in wild teleblock reset");
                AntiPkBranch.setAttackerName(null);
                hasReported = false;
                CombatUtil.get().setTeleblockedWorld(-1);
            }
            return ReactionGenerator.getQuick();
        }


        log("Set idle timer");
        // im not sure if sleep interrupts script here to activate login handler
        RandomManager rm = Client.getInstance().getRandomManager();
        if (rm != null && !rm.isUsingCustomBreakSolver()) {
            Client.getInstance().getRandomManager().disableSolver(RandomEvent.LOGIN);
            Client.setIdleTime(30_000_000);
            Sleep.sleepUntil(() -> !Client.isLoggedIn(), 4000);
            WorldHopper.changeWorldDirect(Worlds.getRandomWorld(w -> w.isNormal() && w.isMembers() && w.getMinimumLevel() == 0));
            Client.getInstance().getRandomManager().enableSolver(RandomEvent.LOGIN);
        }

        Client.setIdleTime(30_000_000);
        return ReactionGenerator.getQuick();
    }
}

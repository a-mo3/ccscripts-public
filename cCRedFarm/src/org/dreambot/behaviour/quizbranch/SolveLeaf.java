package org.dreambot.behaviour.quizbranch;


import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.behaviour.training.HunterUtils;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class SolveLeaf extends Fractal {
    @Override
    public boolean isValid() {
        return PlayerSettings.getBitValue(3671) == 1
                && (PlayerSettings.getBitValue(3686) != 268435455); // this is for if the quiz is complete
    }

    @Override
    public int onLoop() {
        Widget mainWidget = Widgets.getWidget(HunterUtils.MAIN_WIDGET_ID);
        if (mainWidget != null && mainWidget.isVisible()) {
            Sleep.sleep(Calculations.random(150, 600));
            Logger.info("solve");
            HunterUtils.solve();
            return ReactionGenerator.getNormal();
        }

        for (Plaque p : Plaque.values()) {
            if (PlayerSettings.getBitValue(p.VARBIT) < 3) {
                Logger.info("p = " + p.name());
                GameObject plagueObj = GameObjects.closest(p.ID);
                if (plagueObj != null && plagueObj.distance(Players.getLocal()) < 10) {
                    plagueObj.interact("Study");
                    Sleep.sleepUntil(() -> Widgets.getWidget(HunterUtils.MAIN_WIDGET_ID) != null, 5000);
                    break;
                } else {
                    Logger.info("walking to " + p.name());
                    if (Walking.shouldWalk(6)) Walking.walk(p.TILE);
                    break;
                }
            }
        }
        return ReactionGenerator.getNormal();
    }
}

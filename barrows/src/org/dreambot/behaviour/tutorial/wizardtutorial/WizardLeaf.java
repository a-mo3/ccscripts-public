package org.dreambot.behaviour.tutorial.wizardtutorial;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutHelper;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class WizardLeaf extends Fractal {
    Area wizardHut = new Area(3140, 3091, 3143, 3084);
    Supplier<WidgetChild> oldSchoolMain = () -> Widgets.get(788, 40);
    Supplier<WidgetChild> oldSchoolMainConfirm = () -> Widgets.get(788, 15, 0);

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        WidgetChild confirm = oldSchoolMainConfirm.get();
        if (confirm != null && confirm.isVisible()) {
            confirm.interact(confirm.getActions()[0]);
            return ReactionGenerator.getNormal();
        }

        WidgetChild mainGameButton = oldSchoolMain.get();
        if (mainGameButton != null && mainGameButton.isVisible()) {
            Mouse.click(mainGameButton.getRectangle().getLocation());
            return ReactionGenerator.getNormal();
        }


        switch (MyVarps.getTutVarp()) {
            case 610:

            case 620:
            case 640:
            case 670:
                if (!wizardHut.contains(Players.getLocal())) {
                    if (Walking.shouldWalk()) Walking.walk(wizardHut.getCenter());
                    break;
                }

                if (TutHelper.inHumanDialogue()) {
                    Dialog.solve("No, I'm not planning to do that.", "Yes.");
                    return ReactionGenerator.getNormal();
                }

                NPC wizard = NPCs.closest("Magic Instructor");
                if (wizard != null && wizard.interact("Talk-to")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 8000);
                }
                break;
            case 630:
                Tabs.openWithMouse(Tab.MAGIC);
                break;
            case 650:
                Tile chickTile = new Tile(3141, 3090, 0);
                if (!Players.getLocal().getTile().equals(chickTile)) {
                    if (Walking.shouldWalk(6)) Walking.walk(chickTile);
                    return ReactionGenerator.getNormal();
                }

                NPC chicken = NPCs.closest("Chicken");
                if (chicken != null) {
                    Magic.castSpellOn(Normal.WIND_STRIKE, chicken);
                    return 6000; // lol
                }
                break;
        }
        return ReactionGenerator.getNormal();
    }
}

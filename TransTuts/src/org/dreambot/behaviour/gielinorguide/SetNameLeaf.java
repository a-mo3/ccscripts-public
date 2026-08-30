package org.dreambot.behaviour.gielinorguide;


import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.framework.Leaf;
import org.dreambot.util.MyVarps;
import org.dreambot.util.ScriptStage;

public class SetNameLeaf extends Leaf {
    private final String USERNAME = "penis";
    private final ScriptStage scriptStage = ScriptStage.getScriptStage();
    private int tryCounter = 0;
    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() == 1;
    }

    @Override
    public int onLoop() {
        scriptStage.setActiveLeaf("Setting username");
        WidgetChild enterName = Widgets.getMatchingWidget(x -> x.hasAction("Enter name"));
//        WidgetChild lookUpNameButton = Widgets.getWidgetChild(w -> w.containsText("Look up name"));
        WidgetChild suggestedNameOne = Widgets.getWidgetChild(558, 15);
        WidgetChild setNameButton = Widgets.getMatchingWidget(w -> w.hasAction("Set name") && w.isVisible());
        WidgetChild confirmButton = Widgets.getMatchingWidget(w -> w.isVisible() && w.hasAction("Confirm"));
        if (confirmButton != null && confirmButton.interact("Confirm")) {
            Logger.log("confirming appearance");
            return 1800;
        }


        // handle name setting
//        if (setNameButton != null && setNameButton.isVisible()) {
//            Logger.log("pressing " + setNameButton.getActions()[0]);
//            setNameButton.interact();
//            return 2400;
//        }
        if (setNameButton.interact()) {
                Logger.log("pressing set name button");
            tryCounter++;
            if (tryCounter > 10) {
                Tabs.logout();
                Logger.log("trying logout");
                tryCounter = 0;
            }
            return 2400;
        }
        if (suggestedNameOne != null && suggestedNameOne.isVisible()) {
            suggestedNameOne.interact();
            return 1800;
        }
//        if (new WidgetEvent(suggestedNameOne)
//                .executed()) {
//            tryCounter++;
//            Logger.log("Picking suggested name");
//            return 1800;
//        }

//        if (enterName != null && enterName.isVisible()) {
//            enterName.interact("Enter name");
//            MethodProvider.sleep(700);
//            Keyboard.sendKey('a');
//            MethodProvider.sleep(500);
//            Keyboard.pressEnter();
//            return 1800;
//        }
        if (enterName.interact()) {
            Sleep.sleep(700);
            Keyboard.type('a', true);
            Sleep.sleep(500);
            tryCounter++;
            return 1800;
        }
        return 0;
    }
}

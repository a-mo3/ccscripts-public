package org.dreambot.behaviour.tutorial.combattutorial;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutHelper;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class HandleCombatTabsLeaf extends Fractal {

    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() < 440;
    }

    @Override
    public int onLoop() {
        switch (MyVarps.getTutVarp()) {
            case 360: // open gate and enter combat area
                GameObject gate = GameObjects.closest("Gate");
                if (gate != null && gate.interact("Open")) {
                    Sleep.sleepUntil(() -> MyVarps.getTutVarp() != 360, 5000);
                }
                break;
            case 370: // talk to instructor
            case 410:
                NPC combatTrainer = NPCs.closest("Combat Instructor");
                if (TutHelper.inHumanDialogue()) {
                    Dialog.solve();
                    break;
                }
                if (combatTrainer != null && combatTrainer.interact("Talk-to")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 5000);
                }
                break;
            case 390: // open equipment tab
            case 400: // open interfact
            case 405: // equip dagger
                // YOLO!
                if (TutHelper.inHumanDialogue()) {
                    Dialog.solve();
                    break;
                }

                Tabs.openWithMouse(Tab.EQUIPMENT);
                Sleep.sleepUntil(() -> Tabs.isOpen(Tab.EQUIPMENT), 3000);

                WidgetChild openEquipmentInterface = Widgets.get(x -> x.hasAction("View equipment stats"));
                if (openEquipmentInterface != null && openEquipmentInterface.isVisible()) {
                    openEquipmentInterface.interact("View equipment stats");
                }

                Inventory.interact("Bronze dagger", "Equip");
                break;
            case 420: // equip sword and shield
                // YOLO!
                Inventory.interact("Bronze sword", "Wield");
                Inventory.interact("Wooden shield", "Wield");
                break;
            case 430:
                Tabs.openWithMouse(Tab.COMBAT);
                break;
        }
        return ReactionGenerator.getNormal();
    }
}

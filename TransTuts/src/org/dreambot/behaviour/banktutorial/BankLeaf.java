package org.dreambot.behaviour.banktutorial;


import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.framework.Leaf;
import org.dreambot.util.MyVarps;
import org.dreambot.util.ScriptStage;

public class BankLeaf extends Leaf {
    ScriptStage scriptStage = ScriptStage.getScriptStage();
    Area bank = new Area(3118, 3125, 3124, 3119);

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        scriptStage.setActiveLeaf("Bank");
        switch (MyVarps.getTutVarp()) {
            case 500:
                if (!bank.contains(Players.getLocal()) && Walking.shouldWalk(3)) {
                    Walking.walk(bank.getCenter());
                }
                break;
            case 510:
                GameObject bankBooth = GameObjects.closest("Bank booth");
                if (bankBooth != null && bankBooth.interact("Use")) {
                    Sleep.sleepUntil(Bank::isOpen, 8000);
                }
                break;
            case 520:
                if (Bank.isOpen() && Bank.close()) {
                    break;
                }
                GameObject voteBooth = GameObjects.closest("Poll booth");
                if (voteBooth != null && voteBooth.interact("Use")) {
                    Sleep.sleepUntil(Dialogues::inDialogue,8000); // cutscene here don't care for the varbits much honestly
                    Dialogues.chooseFirstOptionContaining();
                }
                break;
            case 525:
            case 530:
            case 532:
                NPC accGuide = NPCs.closest("Account Guide");
                if (accGuide == null) {
                    Logger.log("null guide");
                    break;
                }
                if (!accGuide.canReach() && Walking.shouldWalk(1)) {
                    Walking.walk(accGuide.getTile());
                    break;
                }
                if (accGuide.interact("Talk-to")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 3000);
                    Dialogues.chooseFirstOptionContaining();
                }
                break;
            case 531:
                Tabs.open(Tab.ACCOUNT_MANAGEMENT);
                break;
        }
        return 1200;
    }
}

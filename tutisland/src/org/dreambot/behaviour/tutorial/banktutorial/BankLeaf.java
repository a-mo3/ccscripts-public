package org.dreambot.behaviour.tutorial.banktutorial;


import org.dreambot.Dialog;
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
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutHelper;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class BankLeaf extends Fractal {
    Area bank = new Area(3118, 3125, 3124, 3119);

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
        switch (MyVarps.getTutVarp()) {
            case 500:
                if (!bank.contains(Players.getLocal()) && Walking.shouldWalk(3)) {
                    if (Players.getLocal().getY() > 9000) {
                        GameObject ladder = GameObjects.closest("Ladder");
                        if (ladder != null && ladder.interact("Climb-up")) {
                            Sleep.sleepUntil(() -> Players.getLocal().getY() < 9000, 8000);
                        }
                        return ReactionGenerator.getNormal();
                    }

                    Walking.clickTileOnMinimap(bank.getRandomTile());
                }
                break;
            case 510:
                if (!bank.contains(Players.getLocal())) {
                    if (Walking.shouldWalk()) Walking.clickTileOnMinimap(bank.getRandomTile());
                    return ReactionGenerator.getNormal();
                }

                GameObject bankBooth = GameObjects.closest("Bank booth");
                if (bankBooth != null && bankBooth.interact("Use")) {
                    Sleep.sleepUntil(Bank::isOpen, 8000);
                }
                break;
            case 520:
                if (Bank.isOpen() && Bank.close()) {
                    break;
                }

                if (TutHelper.inHumanDialogue()) {
                    Dialog.solve();
                    return ReactionGenerator.getNormal();
                }

                GameObject voteBooth = GameObjects.closest("Poll booth");
                if (voteBooth != null && voteBooth.interact("Use")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 8000); // cutscene here don't care for the varbits much honestly
                }
                break;
            case 525:
            case 530:
            case 532:
                NPC accGuide = NPCs.closest("Account Guide");
                if (accGuide == null) {
                    Logger.info("null guide");
                    break;
                }

                if (!accGuide.canReach() && Walking.shouldWalk(1)) {
                    if (Walking.shouldWalk(6)) Walking.walk(accGuide.getTile());
                    break;
                }

                if (TutHelper.inHumanDialogue()) {
                    Dialog.solve();
                    return ReactionGenerator.getNormal();
                }

                if (accGuide.interact("Talk-to")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 3000);
                }
                break;
            case 531:
                if (TutHelper.inHumanDialogue()) {
                    Dialog.solve();
                    return ReactionGenerator.getNormal();
                }

                Tabs.openWithMouse(Tab.ACCOUNT_MANAGEMENT);
                break;
        }
        return ReactionGenerator.getNormal();
    }
}

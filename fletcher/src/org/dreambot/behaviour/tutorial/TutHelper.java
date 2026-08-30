package org.dreambot.behaviour.tutorial;

import org.dreambot.api.methods.dialogues.Dialogues;

public class TutHelper {
    public static boolean inHumanDialogue() {
        return Dialogues.areOptionsAvailable() || Dialogues.isProcessing() || Dialogues.canContinue();
    }
}

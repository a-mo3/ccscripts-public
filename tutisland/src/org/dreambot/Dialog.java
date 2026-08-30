package org.dreambot;

import org.dreambot.api.methods.dialogues.Dialogues;

public class Dialog {
    public static boolean solve(String... options) {
        if (Dialogues.canContinue()) {
            Dialogues.continueDialogue();
            return true;
        }

        return Dialogues.chooseFirstOptionContaining(options);
    }

    // tries to ignore tut island tutorial
    public static boolean inHumanDialogue() {
        return Dialogues.areOptionsAvailable() || Dialogues.isProcessing() || Dialogues.canContinue();
    }
}

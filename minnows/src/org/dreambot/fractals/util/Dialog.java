package org.dreambot.fractals.util;

import org.dreambot.api.methods.dialogues.Dialogues;

public class Dialog {
    public static boolean solve(String... options) {
        if (Dialogues.canContinue()) {
            Dialogues.continueDialogue();
            return true;
        }

        return Dialogues.chooseFirstOptionContaining(options);
    }
}

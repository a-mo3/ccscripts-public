package org.dreambot.utility;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.utilities.Logger;

import java.util.Arrays;

public class Dialog {
    public static boolean solve(String... options) {
        Logger.info("Solve dialogue: " + Arrays.toString(options));
        if (Dialogues.canContinue()) {
            Dialogues.continueDialogue();
            return true;
        }

        return Dialogues.chooseFirstOptionContaining(options);
    }
}

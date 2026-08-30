package org.dreambot.generics;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.fractals.IronFractal;
import org.dreambot.utility.Dialog;

import java.util.function.BooleanSupplier;

// just doesnt print the fractal onloop, could add "antiban" in here
@Setter @Accessors(chain = true)
public class SleepFractal extends IronFractal {
    // for quests
    boolean handleDialogue;
    String[] options = new String[0];

    public SleepFractal(BooleanSupplier acceptCondition) {
        super(acceptCondition);
    }

    @Override
    protected int onLoop() {
        if (handleDialogue && Dialogues.inDialogue()) {
            log("Handle dialogue");
            Dialog.solve(options);
        }
        return sleep();
    }
}

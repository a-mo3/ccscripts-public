package org.dreambot.gui.factory;

import org.dreambot.fractals.IronFractal;

import javax.swing.*;
import java.util.function.BooleanSupplier;

/**
 * Hans scripts have a nice feature where there's a tab to generate a "task" of a certain type
 * like select bank/not, location, tree type, start level, and add that to a global task list
 * <p>
 * this serves as a way to do that.
 * we have a method that makes the JPanel, the jpanel should have some action that creates tasks and adds
 * them as children.
 * <p>
 * when we parse the script tree to make the UI any of these will make a tab with their panel.
 * <p>
 * after writing that i realise theres nothing factory about this thats just how ill be using it, renamed to JPaneFractal
 *
 */
public abstract class JPaneFractal extends IronFractal {
    public JPaneFractal(BooleanSupplier acceptCondition) {
        super(acceptCondition);
    }

    public abstract JPanel makePane();
}

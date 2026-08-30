package org.dreambot.scripts;

import lombok.extern.slf4j.Slf4j;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.fractals.IronFractal;

import javax.swing.*;

@Slf4j
public class UITestScript extends PseudoScript {

    @Override
    protected String scriptName() {
        return "UITest";
    }

    @Override
    public void init(IronFractal tree, String[] args) {
        Client.getInstance().getRandomManager().disableSolver(RandomEvent.LOGIN);
        tree.addChildren(
//                new WoodcutTraining(() -> true).setSimpleName("Woodcut")
        ).setSimpleName("Ui test");

        SwingUtilities.invokeLater(tree::makeUI);
    }

    @Override
    public boolean onLoop() {
        return true;
    }
}

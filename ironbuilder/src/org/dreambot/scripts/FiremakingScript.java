package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.behaviour.firemaking.FireMakingDTO;
import org.dreambot.fractals.IronFractal;
import org.dreambot.gui.factory.FractalFactory;
import org.dreambot.loadouts.behavior.RestockStackFractal;
import org.dreambot.paint.PaintButton;
import org.dreambot.utility.PaintUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class FiremakingScript extends PseudoScript {
    @Override
    protected String scriptName() {
        return "ImFiremaking";
    }

    IronFractal tree;

    @Override
    public void init(IronFractal tree, String[] args) {
        tree.setSimpleName(scriptName());
        this.tree = tree;
        tree.addChildren(
                new FractalFactory(new FireMakingDTO(), "firemaking")
                        .setSimpleName("Training")
        );
    }

    PaintButton button = new PaintButton().setLabel("Open GUI").setOnClick(
            c -> SwingUtilities.invokeLater(tree::makeUI)
    );

    @Override
    public void onPaint(Graphics g) {
        button.paintButton(g);
        PaintUtil.paint(g, new String[]{
                IronFractal.decisionPath.toString(),
                String.valueOf(RestockStackFractal.getRestockTasks().size()),
                Arrays.toString(RestockStackFractal.getRestockTasks().toArray())
        });
    }
}

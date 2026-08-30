package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.utilities.Timer;
import org.dreambot.behaviour.fishing.FishingDTO;
import org.dreambot.behaviour.mining.MiningDTO;
import org.dreambot.fractals.IronFractal;
import org.dreambot.gui.factory.FractalFactory;
import org.dreambot.loadouts.behavior.RestockStackFractal;
import org.dreambot.paint.PaintButton;
import org.dreambot.utility.PaintUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class IronFishing extends PseudoScript {
    private IronFractal tree;

    @Override
    protected String scriptName() {
        return "Im Fishing";
    }

    @Override
    public void init(IronFractal tree, String[] args) {
        this.tree = tree;
        tree.addChildren(
                new FractalFactory(new FishingDTO(), "fishing")
                        .setSimpleName("Training")
        ).setSimpleName("Iron Fishing");
    }


    PaintButton button = new PaintButton().setLabel("Open GUI").setOnClick(
            c -> SwingUtilities.invokeLater(tree::makeUI)
    );

    Timer runtime = new Timer();
    @Override
    public void onPaint(Graphics g) {
        button.paintButton(g);
        PaintUtil.paint(g, new String[]{
                runtime.formatTime(),
                IronFractal.decisionPath.toString(),
                String.valueOf(RestockStackFractal.getRestockTasks().size()),
                Arrays.toString(RestockStackFractal.getRestockTasks().toArray()),
                IronFractal.mouseFeatureFlag.toString()
        });
    }
}

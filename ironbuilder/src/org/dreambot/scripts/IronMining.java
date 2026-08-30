package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.analytics.impl.AnalyticsReporter;
import org.dreambot.behaviour.firemaking.FireMakingDTO;
import org.dreambot.behaviour.mining.MiningDTO;
import org.dreambot.fractals.IronFractal;
import org.dreambot.gui.factory.FractalFactory;
import org.dreambot.loadouts.behavior.RestockStackFractal;
import org.dreambot.paint.PaintButton;
import org.dreambot.utility.PaintUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class IronMining extends PseudoScript {
    private IronFractal tree;

    @Override
    protected String scriptName() {
        return "Im Mining";
    }

    @Override
    public void init(IronFractal tree, String[] args) {
        this.tree = tree;
        tree.addChildren(
                new FractalFactory(new MiningDTO(), "mining")
                        .setSimpleName("Training")
        ).setSimpleName("Iron Mining");
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

//        g.drawString(AnalyticsReporter.fractalDensities.size() + "", 50, 200);
    }
}

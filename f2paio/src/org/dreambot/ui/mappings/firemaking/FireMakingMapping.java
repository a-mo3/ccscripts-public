package org.dreambot.ui.mappings.firemaking;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.firemaking.FireMakingFractal;
import org.dreambot.fractals.Fractal;
import org.dreambot.ui.Base64Util;
import org.dreambot.ui.components.DreamPanel;
import org.dreambot.ui.components.DreamToggleButton;
import org.dreambot.ui.mappings.AbstractSkillMapping;

import javax.swing.*;
import java.awt.*;

public class FireMakingMapping extends AbstractSkillMapping<FireMakingData> {
    private static FireMakingMapping instance = null;

    public static FireMakingMapping getInstance() {
        if (instance == null) {
            instance = new FireMakingMapping();
        }
        return instance;
    }

    private DreamPanel fireMakingPanel = null;

    DreamToggleButton logButton;
    DreamToggleButton oakButton;
    DreamToggleButton willowButton;
    DreamToggleButton mapleButton;
    DreamToggleButton yewButton;


    @Override
    public DreamPanel makePanel() {
        if (fireMakingPanel != null) {
            return fireMakingPanel;
        }

        fireMakingPanel = new DreamPanel();
        SpringLayout layout = new SpringLayout();
        fireMakingPanel.setLayout(layout);


        logButton = new DreamToggleButton(
                new ImageIcon(Base64Util.decodeToImage(FireMakingIcons.LOGS)),
                true,
                Logger::info
        );
        logButton.setPreferredSize(new Dimension(50, 50));
        fireMakingPanel.add(logButton);
        layout.putConstraint(SpringLayout.NORTH, logButton, 10, SpringLayout.NORTH, fireMakingPanel);
        layout.putConstraint(SpringLayout.WEST, logButton, 10, SpringLayout.WEST, fireMakingPanel);

        oakButton = addToggleButton(Base64Util.decodeToImage(FireMakingIcons.OAK_LOGS), logButton, layout);
        willowButton = addToggleButton(Base64Util.decodeToImage(FireMakingIcons.WILLOW_LOGS), oakButton, layout);
        mapleButton = addToggleButton(Base64Util.decodeToImage(FireMakingIcons.MAPLE_LOGS), willowButton, layout);
        yewButton = addToggleButton(Base64Util.decodeToImage(FireMakingIcons.YEW_LOGS), mapleButton, layout);

        return fireMakingPanel;
    }


    private DreamToggleButton addToggleButton(Image img, Component alignTo, SpringLayout layout) {
        if (img == null) {
            Logger.warn("Null img");
        }
        DreamToggleButton toggleButton = new DreamToggleButton(
                new ImageIcon(img),
                true
        );
        toggleButton.setPreferredSize(new Dimension(50, 50));
        fireMakingPanel.add(toggleButton);
        layout.putConstraint(SpringLayout.NORTH, toggleButton, 10, SpringLayout.SOUTH, alignTo);
        layout.putConstraint(SpringLayout.WEST, toggleButton, 0, SpringLayout.WEST, alignTo);
        return toggleButton;
    }

    @Override
    public FireMakingData generateDataclass() {
        return new FireMakingData()
                .setTargetLevel(getTargetLevel())
                .setBurnLogs(logButton.isSelected())
                .setBurnOaks(oakButton.isSelected())
                .setBurnWillow(willowButton.isSelected())
                .setBurnMaple(mapleButton.isSelected())
                .setBurnYew(yewButton.isSelected())
                .setCollectAshes(false) // todo impl this
                ;
    }

    @Override
    public Fractal makeFractal(FireMakingData data) {
        final int LOGS = 1511;
        final int YEW_LOGS = 1515;
        final int MAPLE_LOGS = 1517;
        final int WILLOW_LOGS = 1519;
        final int OAK_LOGS = 1521;


        return new Fractal(() -> data.getTargetLevel() > Skills.getRealLevel(Skill.FIREMAKING))
                .setSimpleName("FireMaking")
                .addChildren(
                        new FireMakingFractal(() -> Skills.getRealLevel(Skill.FIREMAKING) >= 60 && data.isBurnYew(), YEW_LOGS)
                                .setSimpleName("Yews"),
                        new FireMakingFractal(() -> Skills.getRealLevel(Skill.FIREMAKING) >= 45 && data.isBurnMaple(), MAPLE_LOGS)
                                .setSimpleName("Maple"),
                        new FireMakingFractal(() -> Skills.getRealLevel(Skill.FIREMAKING) >= 30 && data.isBurnWillow(), WILLOW_LOGS)
                                .setSimpleName("Willows"),
                        new FireMakingFractal(() -> Skills.getRealLevel(Skill.FIREMAKING) >= 15 && data.isBurnOaks(), OAK_LOGS)
                                .setSimpleName("Oaks"),
                        new FireMakingFractal(() -> Skills.getRealLevel(Skill.FIREMAKING) >= 1 && data.isBurnLogs(), LOGS)
                                .setSimpleName("Logs")
                );
    }
}

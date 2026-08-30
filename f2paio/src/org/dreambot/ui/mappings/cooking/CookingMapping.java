package org.dreambot.ui.mappings.cooking;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.cooking.CookingFractal;
import org.dreambot.fractals.Fractal;
import org.dreambot.ui.Base64Util;
import org.dreambot.ui.components.DreamPanel;
import org.dreambot.ui.components.DreamToggleButton;
import org.dreambot.ui.mappings.AbstractSkillMapping;
import org.dreambot.ui.mappings.fishing.FishingIcons;

import javax.swing.*;
import java.awt.*;

public class CookingMapping extends AbstractSkillMapping<CookingData> {
    private static CookingMapping instance = null;

    public static CookingMapping getInstance() {
        if (instance == null) {
            instance = new CookingMapping();
        }
        return instance;
    }

    private DreamPanel cookingPanel = null;

    DreamToggleButton shrimpButton;
    DreamToggleButton salmonButton;
    DreamToggleButton lobsterButton;
    DreamToggleButton swordFishButton;

    @Override
    public DreamPanel makePanel() {
        if (cookingPanel != null) {
            return cookingPanel;
        }

        cookingPanel = new DreamPanel();
        SpringLayout layout = new SpringLayout();
        cookingPanel.setLayout(layout);


        shrimpButton = new DreamToggleButton(
                new ImageIcon(Base64Util.decodeToImage(FishingIcons.SHRIMP)),
                true
        );
        shrimpButton.setPreferredSize(new Dimension(50, 50));
        cookingPanel.add(shrimpButton);
        layout.putConstraint(SpringLayout.NORTH, shrimpButton, 10, SpringLayout.NORTH, cookingPanel);
        layout.putConstraint(SpringLayout.WEST, shrimpButton, 10, SpringLayout.WEST, cookingPanel);

        salmonButton = addToggleButton(Base64Util.decodeToImage(FishingIcons.SALMON), shrimpButton, layout);
        lobsterButton = addToggleButton(Base64Util.decodeToImage(FishingIcons.LOBSTER), salmonButton, layout);
        swordFishButton = addToggleButton(Base64Util.decodeToImage(FishingIcons.SWORDFISH), lobsterButton, layout);
        return cookingPanel;
    }


    private DreamToggleButton addToggleButton(Image img, Component alignTo, SpringLayout layout) {
        DreamToggleButton toggleButton = new DreamToggleButton(
                new ImageIcon(img),
                true
        );
        toggleButton.setPreferredSize(new Dimension(50, 50));
        cookingPanel.add(toggleButton);
        layout.putConstraint(SpringLayout.NORTH, toggleButton, 10, SpringLayout.SOUTH, alignTo);
        layout.putConstraint(SpringLayout.WEST, toggleButton, 0, SpringLayout.WEST, alignTo);
        return toggleButton;
    }

    @Override
    public CookingData generateDataclass() {
        return new CookingData()
                .setTargetLvl(getTargetLevel())
                .setCookShrimp(shrimpButton.isSelected())
                .setCookSalmon(salmonButton.isSelected())
                .setCookLobster(lobsterButton.isSelected())
                .setCookSwordfish(swordFishButton.isSelected())
                ;
    }

    @Override
    public Fractal makeFractal(CookingData data) {
        final int RAW_SWORDFISH = 371;
        final int RAW_LOBSTER = 377;
        final int RAW_SALMON = 331;
        final int RAW_SHRIMPS = 317;

        return new Fractal(() -> data.getTargetLvl() > Skills.getRealLevel(Skill.COOKING))
                .setSimpleName("Cooking")
                .addChildren(
                        new CookingFractal(() -> Skills.getRealLevel(Skill.COOKING) >= 45 && data.isCookSwordfish(), RAW_SWORDFISH)
                                .setSimpleName("Swordfish"),
                        new CookingFractal(() -> Skills.getRealLevel(Skill.COOKING) >= 40 && data.isCookLobster(), RAW_LOBSTER)
                                .setSimpleName("Lobster"),
                        new CookingFractal(() -> Skills.getRealLevel(Skill.COOKING) >= 25 && data.isCookSalmon(), RAW_SALMON)
                                .setSimpleName("Salmon"),
                        new CookingFractal(() -> Skills.getRealLevel(Skill.COOKING) >= 45 && data.isCookShrimp(), RAW_SHRIMPS)
                                .setSimpleName("Shrimp")
                );
    }
}

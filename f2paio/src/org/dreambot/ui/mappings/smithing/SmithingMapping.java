package org.dreambot.ui.mappings.smithing;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.smithing.SmithBarsFractal;
import org.dreambot.fractals.Fractal;
import org.dreambot.ui.Base64Util;
import org.dreambot.ui.components.DreamPanel;
import org.dreambot.ui.components.DreamToggleButton;
import org.dreambot.ui.mappings.AbstractSkillMapping;

import javax.swing.*;
import java.awt.*;

public class SmithingMapping extends AbstractSkillMapping<SmithingData> {

    private static SmithingMapping instance = null;

    public static SmithingMapping getInstance() {
        if (instance == null) {
            instance = new SmithingMapping();
        }
        return instance;
    }

    private DreamPanel smithingPanel = null;

    DreamToggleButton bronzeBar;
    DreamToggleButton ironBar;
    DreamToggleButton silverBar;
    DreamToggleButton steelBar;
    DreamToggleButton goldBar;
    DreamToggleButton mithrilBar;
    DreamToggleButton adamantiteBar;
    DreamToggleButton runiteBar;

    @Override
    public DreamPanel makePanel() {
        if (smithingPanel != null) {
            return smithingPanel;
        }

        smithingPanel = new DreamPanel();
        SpringLayout layout = new SpringLayout();
        smithingPanel.setLayout(layout);


        bronzeBar = new DreamToggleButton(
                new ImageIcon(Base64Util.decodeToImage(SmithingIcons.BRONZE_BAR)),
                true
        );
        bronzeBar.setPreferredSize(new Dimension(50, 50));
        smithingPanel.add(bronzeBar);
        layout.putConstraint(SpringLayout.WEST, bronzeBar, 15, SpringLayout.WEST, smithingPanel);
        layout.putConstraint(SpringLayout.NORTH, bronzeBar, 15, SpringLayout.WEST, smithingPanel);

        ironBar = addToggleButton(Base64Util.decodeToImage(SmithingIcons.IRON_BAR), bronzeBar, layout);
        silverBar = addToggleButton(Base64Util.decodeToImage(SmithingIcons.SILVER_BAR), ironBar, layout);
        steelBar = addToggleButton(Base64Util.decodeToImage(SmithingIcons.STEEL_BAR), silverBar, layout);
        goldBar = addToggleButton(Base64Util.decodeToImage(SmithingIcons.GOLD_BAR), steelBar, layout);
        mithrilBar = addToggleButton(Base64Util.decodeToImage(SmithingIcons.MITHRIL_BAR), goldBar, layout);
        adamantiteBar = addToggleButton(Base64Util.decodeToImage(SmithingIcons.ADAMANTITE_BAR), mithrilBar, layout);
        runiteBar = addToggleButton(Base64Util.decodeToImage(SmithingIcons.RUNITE_BAR), adamantiteBar, layout);

        return this.smithingPanel;
    }

    private DreamToggleButton addToggleButton(Image img, Component alignTo, SpringLayout layout) {
        DreamToggleButton toggleButton = new DreamToggleButton(
                new ImageIcon(img),
                true
        );
        toggleButton.setPreferredSize(new Dimension(50, 50));
        smithingPanel.add(toggleButton);
        layout.putConstraint(SpringLayout.NORTH, toggleButton, 10, SpringLayout.SOUTH, alignTo);
        layout.putConstraint(SpringLayout.WEST, toggleButton, 0, SpringLayout.WEST, alignTo);
        return toggleButton;
    }

    @Override
    public SmithingData generateDataclass() {
        return new SmithingData()
                .setTargetLvl(getTargetLevel())
                .setSmithBronze(bronzeBar.isSelected())
                .setSmithIron(ironBar.isSelected())
                .setSmithSilver(silverBar.isSelected())
                .setSmithSteel(steelBar.isSelected())
                .setSmithGold(goldBar.isSelected())
                .setSmithMithril(mithrilBar.isSelected())
                .setSmithAdamant(adamantiteBar.isSelected())
                .setSmithRunite(runiteBar.isSelected())
                ;
    }

    @Override
    public Fractal makeFractal(SmithingData data) {
        final int COPPER_ORE = 436;
        final int TIN_ORE = 438;
        final int IRON_ORE = 440;
        final int SILVER_ORE = 442;
        final int GOLD_ORE = 444;
        final int MITHRIL_ORE = 447;
        final int ADAMANTITE_ORE = 449;
        final int RUNITE_ORE = 451;
        final int COAL = 453;

        return new Fractal(() -> data.getTargetLvl() >= Skills.getRealLevel(Skill.SMITHING))
                .setSimpleName("Smithing")
                .addChildren(
                        new SmithBarsFractal(() -> Skills.getRealLevel(Skill.SMITHING) >= 85 && data.isSmithRunite(),
                                RUNITE_ORE, 3, COAL, 24, "Runite bar")
                                .setSimpleName("Runite"),
                        new SmithBarsFractal(() -> Skills.getRealLevel(Skill.SMITHING) >= 70 && data.isSmithAdamant(),
                                ADAMANTITE_ORE, 4, COAL, 24, "Adamantite bar")
                                .setSimpleName("Adamantite"),
                        new SmithBarsFractal(() -> Skills.getRealLevel(Skill.SMITHING) >= 50 && data.isSmithMithril(),
                                MITHRIL_ORE, 5, COAL, 20, "Mithril bar")
                                .setSimpleName("Mithril"),
                        new SmithBarsFractal(() -> Skills.getRealLevel(Skill.SMITHING) >= 40 && data.isSmithGold(),
                                GOLD_ORE, 28, COAL, 0, "Gold bar")
                                .setSimpleName("Gold"),
                        new SmithBarsFractal(() -> Skills.getRealLevel(Skill.SMITHING) >= 30 && data.isSmithSteel(),
                                IRON_ORE, 8, COAL, 16, "Steel bar")
                                .setSimpleName("Steel"),
                        new SmithBarsFractal(() -> Skills.getRealLevel(Skill.SMITHING) >= 20 && data.isSmithSilver(),
                                SILVER_ORE, 28, COAL, 0, "Silver bar")
                                .setSimpleName("Silver"),
                        new SmithBarsFractal(() -> Skills.getRealLevel(Skill.SMITHING) >= 15 && data.isSmithIron(),
                                IRON_ORE, 28, COAL, 0, "Iron bar")
                                .setSimpleName("Iron"),
                        new SmithBarsFractal(() -> Skills.getRealLevel(Skill.SMITHING) >= 1 && data.isSmithBronze(),
                                TIN_ORE, 14, COPPER_ORE, 14, "Bronze bar")
                                .setSimpleName("Bronze")
                );
    }
}

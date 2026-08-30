package org.dreambot.ui.mappings.woodcutting;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.woodcutting.WoodcuttingFractal;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemID;
import org.dreambot.ui.Base64Util;
import org.dreambot.ui.components.DreamCheckBox;
import org.dreambot.ui.components.DreamLabel;
import org.dreambot.ui.components.DreamPanel;
import org.dreambot.ui.components.DreamToggleButton;
import org.dreambot.ui.mappings.AbstractSkillMapping;
import org.dreambot.ui.mappings.firemaking.FireMakingIcons;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WoodCuttingMapping extends AbstractSkillMapping<WoodCuttingData> {
    private static WoodCuttingMapping instance = null;

    private WoodCuttingMapping() {
    }

    public static WoodCuttingMapping getInstance() {
        if (instance == null) instance = new WoodCuttingMapping();
        return instance;
    }


    private DreamPanel fireMakingPanel = null;

    DreamCheckBox bankLootCheckBox;

    DreamToggleButton logButton;
    DreamToggleButton oakButton;
    DreamToggleButton willowButton;
    //    DreamToggleButton mapleButton;
    DreamToggleButton yewButton;

    @Override
    public DreamPanel makePanel() {
        if (fireMakingPanel != null) {
            return fireMakingPanel;
        }

        DreamPanel panel = new DreamPanel();
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        DreamLabel bankLootLabel = new DreamLabel("Bank loot:");
        panel.add(bankLootLabel);
        layout.putConstraint(SpringLayout.WEST, bankLootLabel, 15, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, bankLootLabel, 15, SpringLayout.NORTH, panel);

        bankLootCheckBox = new DreamCheckBox();
        panel.add(bankLootCheckBox);
        layout.putConstraint(SpringLayout.WEST, bankLootCheckBox, 15, SpringLayout.EAST, bankLootLabel);
        layout.putConstraint(SpringLayout.NORTH, bankLootCheckBox, 0, SpringLayout.NORTH, bankLootLabel);

        fireMakingPanel = panel;
        logButton = addToggleButton(Base64Util.decodeToImage(FireMakingIcons.LOGS), bankLootLabel, layout, Logger::info);
        oakButton = addToggleButton(Base64Util.decodeToImage(FireMakingIcons.OAK_LOGS), logButton, layout, Logger::info);
        willowButton = addToggleButton(Base64Util.decodeToImage(FireMakingIcons.WILLOW_LOGS), oakButton, layout, Logger::info);
//        mapleButton = addToggleButton(Base64Util.decodeToImage(FireMakingIcons.MAPLE_LOGS), mapleButton, layout, Logger::info);
        yewButton = addToggleButton(Base64Util.decodeToImage(FireMakingIcons.YEW_LOGS), willowButton, layout, Logger::info);

        fireMakingPanel = panel;
        return fireMakingPanel;
    }

    private DreamToggleButton addToggleButton(Image img, Component alignTo, SpringLayout layout, Consumer<Boolean> selectedHandler) {
        if (img == null) {
            Logger.warn("Null img");
        }
        DreamToggleButton toggleButton = new DreamToggleButton(
                new ImageIcon(img),
                true,
                selectedHandler
        );
        toggleButton.setPreferredSize(new Dimension(50, 50));
        fireMakingPanel.add(toggleButton);
        layout.putConstraint(SpringLayout.NORTH, toggleButton, 10, SpringLayout.SOUTH, alignTo);
        layout.putConstraint(SpringLayout.WEST, toggleButton, 0, SpringLayout.WEST, alignTo);
        return toggleButton;
    }

    @Override
    public WoodCuttingData generateDataclass() {
        return new WoodCuttingData()
                .setShouldBank(bankLootCheckBox.isSelected())
                .setChopLogs(logButton.isSelected())
                .setChopOaks(oakButton.isSelected())
//                .setChopMaple(mapleButton.isSelected())
                .setChopYew(yewButton.isSelected())
                .setTargetLevel(getTargetLevel())
                ;
    }

    @Override
    public Fractal makeFractal(WoodCuttingData data) {
        Area[] treeAreas = new Area[]{
                new Area(2988, 3263, 3007, 3246), // west port sarim collection of trees
                new Area(3033, 3276, 3044, 3259), // northen port sarim
                //            new Area(3121, 3219, 3140, 3207), // north wizards tower
                new Area(3266, 3343, 3281, 3335) // south varrock sheep pen, near quest start
        };

        Area[] oakAreas = new Area[]{
                new Area(3022, 3280, 3047, 3267), // north of port sarim
                new Area(3189, 3465, 3196, 3457), // palace 3 oaks
                new Area(3276, 3434, 3285, 3411), // varrock west
                new Area(3098, 3255, 3108, 3240), // draynor east of bank
                new Area(2995, 3369, 3007, 3354), // falador east bank
                new Area(2946, 3417, 2956, 3397) // falador north of west bank
        };

        Area[] willowAreas = new Area[]{
                new Area(2960, 3199, 2974, 3189),//rimmington
                new Area(2985, 3190, 2992, 3183), // rimmington east
                new Area(2995, 3171, 3008, 3162), // south port sarim
                new Area(3056, 3255, 3064, 3251) // port sarim
        };

        Area[] yewAreas = new Area[]{
                new Area(2928, 3236, 2940, 3224), // rimmington yews
                new Area(new Tile(3022, 3322, 0),
                        new Tile(3024, 3315, 0),
                        new Tile(3013, 3312, 0),
                        new Tile(3000, 3303, 0),
                        new Tile(2992, 3313, 0),
                        new Tile(3007, 3320, 0)), // south of falador yews and oaks
                new Area(3047, 3275, 3060, 3264), // port sarim yews and oaks
                new Area(3085, 3482, 3089, 3468), // classic edgeville yews
                new Area(
                        new Tile(3201, 3501, 0),
                        new Tile(3207, 3501, 0),
                        new Tile(3207, 3498, 0),
                        new Tile(3212, 3498, 0),
                        new Tile(3213, 3506, 0),
                        new Tile(3202, 3506, 0)) // close to GE yews
        };

        final int RUNE_AXE = 1359;
        final int MITHRIL_AXE = 1355;
        final int BRONZE_AXE = 1351;
        final Supplier<Integer> getAppropriateAxe = () -> {
            int lvl = Skills.getRealLevel(Skill.WOODCUTTING);
            if (lvl >= 41) return RUNE_AXE;
            if (lvl >= 21) return MITHRIL_AXE;
            return BRONZE_AXE;
        };

        InventoryLoadout axeLoadout = new InventoryLoadout()
                .strictIgnore(ItemID.LOGS, ItemID.WILLOW_LOGS, ItemID.MAPLE_LOGS, ItemID.YEW_LOGS, ItemID.OAK_LOGS)
                .addItem(getAppropriateAxe, 1);

        return new Fractal(() -> data.getTargetLevel() > Skills.getRealLevel(Skill.WOODCUTTING))
                .setSimpleName("WoodCutting")
                .addChildren(
                        new WoodcuttingFractal(() -> data.isChopYew() && Skills.getRealLevel(Skill.WOODCUTTING) >= 60,
                                yewAreas[Calculations.random(0, yewAreas.length)],
                                () -> GameObjects.closest("Yew"))
                                .setShouldBank(data.isShouldBank())
                                .setInventoryLoadout(axeLoadout.setStrict(true))
                                .setSimpleName("Yews"),
                        new WoodcuttingFractal(() -> data.isChopWillow() && Skills.getRealLevel(Skill.WOODCUTTING) >= 30,
                                willowAreas[Calculations.random(0, willowAreas.length)],
                                () -> GameObjects.closest("Willow"))
                                .setShouldBank(data.isShouldBank())
                                .setInventoryLoadout(axeLoadout)
                                .setSimpleName("Willow"),
                        new WoodcuttingFractal(() -> data.isChopOaks() && Skills.getRealLevel(Skill.WOODCUTTING) >= 15,
                                oakAreas[Calculations.random(0, oakAreas.length)],
                                () -> GameObjects.closest("Oak"))
                                .setShouldBank(data.isShouldBank())
                                .setInventoryLoadout(axeLoadout)
                                .setSimpleName("Oaks"),
                        new WoodcuttingFractal(data::isChopLogs,
                                treeAreas[Calculations.random(0, treeAreas.length)],
                                () -> GameObjects.closest("Tree"))
                                .setShouldBank(data.isShouldBank())
                                .setInventoryLoadout(axeLoadout)
                                .setSimpleName("Logs")
                );
    }
}

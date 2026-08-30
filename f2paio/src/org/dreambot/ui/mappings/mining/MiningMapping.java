package org.dreambot.ui.mappings.mining;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.mining.MiningFractal;
import org.dreambot.behaviour.mining.Rock;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.ui.Base64Util;
import org.dreambot.ui.components.DreamCheckBox;
import org.dreambot.ui.components.DreamLabel;
import org.dreambot.ui.components.DreamPanel;
import org.dreambot.ui.components.DreamToggleButton;
import org.dreambot.ui.mappings.AbstractSkillMapping;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class MiningMapping extends AbstractSkillMapping<MiningData> {
    private MiningMapping() {
        makePanel();
    }

    private static MiningMapping instance = null;

    public static AbstractSkillMapping<MiningData> getInstance() {
        if (instance == null) {
            instance = new MiningMapping();
        }
        return instance;
    }

    private DreamPanel miningPanel = null;

    private DreamToggleButton copperButton;
    private DreamToggleButton ironButton;
    private DreamToggleButton coalButton;
    private DreamToggleButton goldButton;
    private DreamToggleButton mithrilButton;
    private DreamToggleButton adamantButton;
    private DreamCheckBox bankLootCheckBox;

    @Override
    public DreamPanel makePanel() {
        if (miningPanel != null) {
            return miningPanel;
        }

        DreamPanel panel = new DreamPanel();
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        miningPanel = panel;
        DreamLabel bankLootLabel = new DreamLabel("Bank loot:");
        panel.add(bankLootLabel);
        layout.putConstraint(SpringLayout.WEST, bankLootLabel, 15, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, bankLootLabel, 15, SpringLayout.NORTH, panel);

        bankLootCheckBox = new DreamCheckBox();
        bankLootCheckBox.addActionListener(e -> {
            boolean shouldBank = bankLootCheckBox.isSelected();
            Logger.info("[MINING] - bank loot " + shouldBank);
//            builder.setShouldBank(shouldBank);
        });
        panel.add(bankLootCheckBox);
        layout.putConstraint(SpringLayout.WEST, bankLootCheckBox, 15, SpringLayout.EAST, bankLootLabel);
        layout.putConstraint(SpringLayout.NORTH, bankLootCheckBox, 0, SpringLayout.NORTH, bankLootLabel);

        // todo replace consumers with supplier fields to check if buttons have been selected
        copperButton = addToggleButton(Base64Util.decodeToImage(MiningIcons.COPPER), bankLootLabel, layout);
        ironButton = addToggleButton(Base64Util.decodeToImage(MiningIcons.IRON), copperButton, layout);
        coalButton = addToggleButton(Base64Util.decodeToImage(MiningIcons.COAL), ironButton, layout);
        goldButton = addToggleButton(Base64Util.decodeToImage(MiningIcons.GOLD), coalButton, layout);
        mithrilButton = addToggleButton(Base64Util.decodeToImage(MiningIcons.MITHRIL), goldButton, layout);
        adamantButton = addToggleButton(Base64Util.decodeToImage(MiningIcons.ADAMANTITE), mithrilButton, layout);


        Logger.info("Returning panel");
        miningPanel = panel;
        return miningPanel;
    }

    private DreamToggleButton addToggleButton(Image img, Component alignTo, SpringLayout layout) {
        DreamToggleButton toggleButton = new DreamToggleButton(
                new ImageIcon(img),
                true
//                selectedHandler
        );
        toggleButton.setPreferredSize(new Dimension(50, 50));
        miningPanel.add(toggleButton);
        layout.putConstraint(SpringLayout.NORTH, toggleButton, 10, SpringLayout.SOUTH, alignTo);
        layout.putConstraint(SpringLayout.WEST, toggleButton, 0, SpringLayout.WEST, alignTo);
        return toggleButton;
    }

    @Override
    public MiningData generateDataclass() {
        return new MiningData()
                .setTargetLevel(this.getTargetLevel())
                .setMineCopper(copperButton.isSelected())
                .setMineIron(ironButton.isSelected())
                .setMineCoal(coalButton.isSelected())
                .setMineGold(goldButton.isSelected())
                .setMineMithril(mithrilButton.isSelected())
                .setMineAdamant(adamantButton.isSelected())
                .setShouldBank(bankLootCheckBox.isSelected())
                ;
    }

    @Override
    public Fractal makeFractal(MiningData data) {
        Logger.info("Making mining fract");
        Area[] copperRocks = new Area[]{
                new Area(2974, 3250, 2981, 3244), // rimmington
                new Area(3226, 3149, 3231, 3143), // swamp
                new Area(3283, 3367, 3290, 3360), // south varrock
        };

        Area[] ironRocks = new Area[]{
                new Area(2967, 3243, 2972, 3236), // rimmington left
                new Area(2979, 3236, 2983, 3231), // rimmington right
                new Area(3283, 3371, 3289, 3367), // south east varrock
                new Area(3170, 3370, 3180, 3364), // south west varrock
        };

        Area[] coalRocks = new Area[]{
                new Area(3143, 3155, 3146, 3144), // south west lummy
                new Area(3399, 3172, 3405, 3167), // clan wars
        };

        Area[] goldRocks = new Area[]{
                new Area(2973, 3235, 2978, 3231), // rimmington
        };

        Area[] mithrilRocks = new Area[]{
                new Area(3142, 3148, 3149, 3143), // west lummmy
        };

        Area[] adamantRocks = new Area[]{
                new Area(3142, 3148, 3149, 3143), // west lummmy
        };

        final int RUNE_PICKAXE = 1275;
        final int MITHRIL_PICKAXE = 1273;
        final int BRONZE_PICKAXE = 1265;
        final Supplier<Integer> appropriatePickaxe = () -> {
            int mineLvl = Skills.getRealLevel(Skill.MINING);
            if (mineLvl >= 41) return RUNE_PICKAXE;
            if (mineLvl >= 21) return MITHRIL_PICKAXE;
            return BRONZE_PICKAXE;
        };

        InventoryLoadout miningLoadout = new InventoryLoadout()
                .addItem(appropriatePickaxe, 1);

        boolean shouldBank = data.isShouldBank();
        return new Fractal(() -> data.getTargetLevel() > Skills.getRealLevel(Skill.MINING)).setSimpleName("Mining")
                .addChildren(
                        new MiningFractal(() -> Skills.getRealLevel(Skill.MINING) >= 70 && data.isMineAdamant(),
                                adamantRocks[Calculations.random(0, adamantRocks.length)], Rock.ADAMANTITE)
                                .setShouldBank(shouldBank)
                                .setInventoryLoadout(miningLoadout)
                                .setSimpleName("Adamant"),
                        new MiningFractal(() -> Skills.getRealLevel(Skill.MINING) >= 55 && data.isMineMithril(),
                                mithrilRocks[Calculations.random(0, mithrilRocks.length)], Rock.MITHRIL)
                                .setShouldBank(shouldBank)
                                .setInventoryLoadout(miningLoadout)
                                .setSimpleName("Mithril"),
                        new MiningFractal(() -> Skills.getRealLevel(Skill.MINING) >= 40 && data.isMineGold(),
                                goldRocks[Calculations.random(0, goldRocks.length)], Rock.GOLD)
                                .setShouldBank(shouldBank)
                                .setInventoryLoadout(miningLoadout)
                                .setSimpleName("Gold"),
                        new MiningFractal(() -> Skills.getRealLevel(Skill.MINING) >= 30 && data.isMineCoal(),
                                coalRocks[Calculations.random(0, coalRocks.length)], Rock.COAL)
                                .setShouldBank(shouldBank)
                                .setInventoryLoadout(miningLoadout)
                                .setSimpleName("Coal"),
                        new MiningFractal(() -> Skills.getRealLevel(Skill.MINING) >= 15 && data.isMineIron(),
                                ironRocks[Calculations.random(0, ironRocks.length)], Rock.IRON)
                                .setShouldBank(shouldBank)
                                .setInventoryLoadout(miningLoadout)
                                .setSimpleName("Iron"),
                        // maybe copper should always be true so its impossible for sures to make a config that just gets stuck.
                        new MiningFractal(() -> Skills.getRealLevel(Skill.MINING) >= 1 && data.isMineCopper(),
                                copperRocks[Calculations.random(0, copperRocks.length)], Rock.COPPER)
                                .setShouldBank(shouldBank)
                                .setInventoryLoadout(miningLoadout)
                                .setSimpleName("Copper")
                );
    }
}

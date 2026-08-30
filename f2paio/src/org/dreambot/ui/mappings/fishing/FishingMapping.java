package org.dreambot.ui.mappings.fishing;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.fishing.FishingFractal;
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

public class FishingMapping extends AbstractSkillMapping<FishingData> {
    private static FishingMapping instance = null;

    private FishingMapping() {
    }

    public static FishingMapping getInstance() {
        if (instance == null) instance = new FishingMapping();
        return instance;
    }

    private DreamPanel fishingPanel = null;

    DreamToggleButton shrimpButton;
    DreamToggleButton salmonButton;
    DreamToggleButton lobsterButton;
    DreamToggleButton swordFishButton;

    DreamCheckBox bankLootCheckBox;

    @Override
    public DreamPanel makePanel() {
        if (fishingPanel != null) {
            return fishingPanel;
        }
        Logger.info("Building fish panel");

        fishingPanel = new DreamPanel();
        SpringLayout layout = new SpringLayout();
        fishingPanel.setLayout(layout);

        DreamLabel bankLootLabel = new DreamLabel("Bank loot:");
        fishingPanel.add(bankLootLabel);
        layout.putConstraint(SpringLayout.WEST, bankLootLabel, 15, SpringLayout.WEST, fishingPanel);
        layout.putConstraint(SpringLayout.NORTH, bankLootLabel, 15, SpringLayout.NORTH, fishingPanel);

        bankLootCheckBox = new DreamCheckBox();
        bankLootCheckBox.addActionListener(e -> {
            boolean shouldBank = bankLootCheckBox.isSelected();
            Logger.info("[FISHING] - bank loot " + shouldBank);
        });

        fishingPanel.add(bankLootCheckBox);
        layout.putConstraint(SpringLayout.WEST, bankLootCheckBox, 15, SpringLayout.EAST, bankLootLabel);
        layout.putConstraint(SpringLayout.NORTH, bankLootCheckBox, 0, SpringLayout.NORTH, bankLootLabel);

        shrimpButton = addToggleButton(Base64Util.decodeToImage(FishingIcons.SHRIMP), bankLootLabel, layout);
        salmonButton = addToggleButton(Base64Util.decodeToImage(FishingIcons.SALMON), shrimpButton, layout);
        lobsterButton = addToggleButton(Base64Util.decodeToImage(FishingIcons.LOBSTER), salmonButton, layout);
        swordFishButton = addToggleButton(Base64Util.decodeToImage(FishingIcons.SWORDFISH), lobsterButton, layout);


        return this.fishingPanel;
    }

    private DreamToggleButton addToggleButton(Image img, Component alignTo, SpringLayout layout) {
        DreamToggleButton toggleButton = new DreamToggleButton(
                new ImageIcon(img),
                true
        );
        toggleButton.setPreferredSize(new Dimension(50, 50));
        fishingPanel.add(toggleButton);
        layout.putConstraint(SpringLayout.NORTH, toggleButton, 10, SpringLayout.SOUTH, alignTo);
        layout.putConstraint(SpringLayout.WEST, toggleButton, 0, SpringLayout.WEST, alignTo);
        return toggleButton;
    }

    @Override
    public FishingData generateDataclass() {
        return new FishingData()
                .setTargetLevel(getTargetLevel())
                .setCatchShrimp(shrimpButton.isSelected())
                .setCatchSalmon(salmonButton.isSelected())
                .setCatchLobster(lobsterButton.isSelected())
                .setCatchSwordfish(swordFishButton.isSelected())
                .setBankLoot(bankLootCheckBox.isSelected())
                ;
    }

    @Override
    public Fractal makeFractal(FishingData data) {

        Area swordFish = new Area(2922, 3181, 2927, 3176);
        Area shrimp = new Area(3240, 3159, 3246, 3151);
        Area flyFishing = new Area(3107, 3436, 3111, 3430);

        return new Fractal(() -> data.getTargetLevel() > Skills.getRealLevel(Skill.FISHING))
                .setSimpleName("Fishing")
                .addChildren(
                        new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) >= 50 && data.isCatchSwordfish(),
                                swordFish, () -> NPCs.closest(n -> n.hasAction("Harpoon") && swordFish.contains(n)))
                                .setShouldBank(data.isBankLoot())
                                .setInteraction("Harpoon")
                                .setSimpleName("Swordfish")
                                .setInventoryLoadout(new InventoryLoadout().addItem(FishingFractal.HARPOON, 1)),

                        new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) >= 40 && data.isCatchLobster(),
                                swordFish, () -> NPCs.closest(n -> n.hasAction("Cage") && swordFish.contains(n)))
                                .setShouldBank(data.isBankLoot())
                                .setInteraction("Cage")
                                .setSimpleName("Lobster")
                                .setInventoryLoadout(new InventoryLoadout().addItem(FishingFractal.LOBSTER_POT, 1)),

                        new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) >= 20 && data.isCatchSalmon(),
                                flyFishing, () -> NPCs.closest(n -> n.hasAction("Lure") && flyFishing.contains(n)))
                                .setShouldBank(data.isBankLoot())
                                .setInteraction("Lure")
                                .setSimpleName("Salmon/Trout")
                                .setInventoryLoadout(new InventoryLoadout()
                                        .addItem(FishingFractal.FLY_FISHING_ROD, 1)
                                        .addItem(FishingFractal.FEATHER, 1, 5000)),

                        new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) >= 1 && data.isCatchShrimp(),
                                shrimp, () -> NPCs.closest(n -> n.hasAction("Net") && shrimp.contains(n)))
                                .setShouldBank(data.isBankLoot())
                                .setInteraction("Net")
                                .setSimpleName("Shrimp")
                                .setInventoryLoadout(new InventoryLoadout().addItem(FishingFractal.SMALL_FISHING_NET, 1))
                );
    }
}

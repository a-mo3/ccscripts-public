package org.dreambot.ui;


import org.dreambot.api.utilities.Logger;
import org.dreambot.ui.components.DreamButton;
import org.dreambot.ui.components.DreamLabel;
import org.dreambot.ui.components.DreamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class SkillComponent extends DreamPanel implements MouseListener {

    //    BufferedImage craftingIcon = Base64Util.decodeToImage(SkillIcon.CRAFTING_ICON);
    public SkillIcon icon;

    public SkillComponent(SkillIcon skillIcon) {
        addMouseListener(this);
        icon = skillIcon;
        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);

        BufferedImage img = Base64Util.decodeToImage(skillIcon.getBase64());
        JLabel iconLabel = new JLabel();
        if (img != null) {
            ImageIcon imgIcon = new ImageIcon(img);
            iconLabel = new JLabel(imgIcon);
            add(iconLabel);
        }

        String name = skillIcon.name().toLowerCase();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        DreamLabel skillLabel = new DreamLabel(name);
        add(skillLabel);

        DreamButton upButton = new DreamButton("+");
        DreamButton downButton = new DreamButton("-");
        upButton.addActionListener(e -> AIOMenu.getInstance().moveSkillComponentUp(icon));
        downButton.addActionListener(e -> AIOMenu.getInstance().moveSkillComponentDown(icon));
        upButton.setPreferredSize(new Dimension(25, 22));
        downButton.setPreferredSize(new Dimension(25, 22));

        add(upButton);
        add(downButton);

        SpinnerNumberModel numberModel = new SpinnerNumberModel(0, 0, 99, 1);

        JSpinner targetLvlSpinner = new JSpinner(numberModel);
        targetLvlSpinner.addChangeListener(e -> {
            int value = (Integer) targetLvlSpinner.getValue();
            Logger.info(String.format("Updating target level for %s to %d", icon.name(), value));
            icon.getUiMapping().updateTargetLevel(value);
        });
        add(targetLvlSpinner);

//        targetLvlSpinner.setAlignmentX(RIGHT_ALIGNMENT);

        setMaximumSize(new Dimension(600, 30));

        layout.getConstraints(targetLvlSpinner).setWidth(Spring.constant(25, 40, 50));
        layout.putConstraint(SpringLayout.WEST, skillLabel, 35, SpringLayout.WEST, iconLabel);
        layout.putConstraint(SpringLayout.WEST, targetLvlSpinner, 185, SpringLayout.WEST, skillLabel);
        layout.putConstraint(SpringLayout.EAST, targetLvlSpinner, 70, SpringLayout.WEST, targetLvlSpinner);
        layout.putConstraint(SpringLayout.EAST, downButton, 125, SpringLayout.WEST, skillLabel);
        layout.putConstraint(SpringLayout.WEST, upButton, 5, SpringLayout.EAST, downButton);
//        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Mouse clicked - " + icon.name());
        // todo switch to icons skill UI panel;
        AIOMenu instance = AIOMenu.getInstance();
        instance.removeHighlights();

        instance.updateSkillSettings(icon.getUiMapping().makePanel());

        setBorder(BorderFactory.createLoweredBevelBorder());
        SwingUtilities.updateComponentTreeUI(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

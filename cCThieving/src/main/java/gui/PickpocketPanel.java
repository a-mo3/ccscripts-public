package gui;

import config.Config;
import enums.TargetNPC;
import gui.components.*;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PickpocketPanel extends DreamPanel {
    private DreamPanel content;
    private DreamFrame frame;
    Config config = Config.getConfig();
    public PickpocketPanel(DreamFrame frame) {
        this.frame = frame;
        this.setBorder(new EmptyBorder(7, 8, 7, 8));
        this.add(content = new DreamPanel(), BorderLayout.NORTH);
        content.setLayout(new GridLayout(0, 2));

        content.add(new DreamLabel("Pickpocket Target: "));
        DreamComboBox<TargetNPC> targetComboBox = new DreamComboBox<>(TargetNPC.values());
        content.add(targetComboBox);

        content.add(new DreamLabel("Food amount:"));
        DreamTextField foodAmountField = new DreamTextField();
        foodAmountField.setText("12");
        content.add(foodAmountField);

        content.add(new DreamLabel("Necklace amount:"));
        DreamTextField necklaceAmountField = new DreamTextField();
        necklaceAmountField.setText("3");
        content.add(necklaceAmountField);

        // banking toggle box
        DreamCheckBox bankingBox = new DreamCheckBox("Enable banking");
        content.add(bankingBox);

        DreamCheckBox foodBox = new DreamCheckBox("Eat food?");
        content.add(foodBox);

        DreamCheckBox necklaceBox = new DreamCheckBox("Use dodgy necklaces?");
        content.add(necklaceBox);

        DreamButton startButton = new DreamButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setPickpocketing(true);
                config.setPickpocketTarget((TargetNPC) targetComboBox.getSelectedItem());
                config.setBankingMode(bankingBox.getModel().isSelected());
                config.setEatFood(foodBox.getModel().isSelected());
                config.setUseNecklace(necklaceBox.getModel().isSelected());
                config.setFoodAmount(Integer.parseInt(foodAmountField.getText()));
                config.setNecklaceAmount(Integer.parseInt(necklaceAmountField.getText()));
                frame.dispose();
            }
        });
        content.add(startButton);

    }

    public static void main(String[] args) {

    }
}

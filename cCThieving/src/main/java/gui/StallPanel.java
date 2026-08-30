package gui;

import config.Config;
import enums.Stall;
import gui.components.*;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StallPanel extends DreamPanel {
    Config config = Config.getConfig();
    private DreamPanel content;
    private DreamFrame frame;
    public StallPanel(DreamFrame frame) {
        this.frame = frame;
        this.setBorder(new EmptyBorder(7, 8, 7, 8));
        this.add(content = new DreamPanel(), BorderLayout.NORTH);
        content.setLayout(new GridLayout(0, 2));

        content.add(new DreamLabel("Stall target"));
        DreamComboBox<Stall> stallComboBox = new DreamComboBox<>(Stall.values());
        content.add(stallComboBox);

        // banking toggle box
        DreamCheckBox bankingBox = new DreamCheckBox("Enable banking");
        content.add(bankingBox);

        DreamButton startButton = new DreamButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setStallMode(true);
                config.setStallTarget((Stall) stallComboBox.getSelectedItem());
                config.setBankingMode(bankingBox.getModel().isSelected());
                frame.dispose();
            }
        });
        content.add(startButton);
    }
}

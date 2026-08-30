package gui;

import config.Config;
import gui.components.*;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChestPanel extends DreamPanel{
    private DreamPanel content;
    private DreamFrame frame;
    Config config = Config.getConfig();
    public ChestPanel(DreamFrame frame) {
        this.frame = frame;
        this.setBorder(new EmptyBorder(7, 8, 7, 8));
        this.add(content = new DreamPanel(), BorderLayout.NORTH);
        content.setLayout(new GridLayout(0, 2));

        content.add(new DreamLabel("Chest respawn time(seconds): "));
        DreamTextField respawnField = new DreamTextField();
        respawnField.setText("15");
        content.add(respawnField);

        DreamButton startButton = new DreamButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setChestMode(true);
                int time = Integer.parseInt(respawnField.getText()) * 1000;
                config.setChestTimerRespawn(time);
                frame.dispose();
            }
        });
        content.add(startButton);
    }
}

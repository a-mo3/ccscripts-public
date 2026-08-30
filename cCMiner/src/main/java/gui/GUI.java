package gui;

import config.Config;
import config.MineLocation;
import config.Rock;
import gui.components.*;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.BankLocation;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends DreamFrame {

    private DreamPanel body, content;
    private DreamPanel antiBanBody, antiBanContent;
    Config config = Config.getConfig();

    public GUI() {
        super("cCMiner");
        DreamTabbedPane tabbedPane = new DreamTabbedPane();
        tabbedPane.add("Settings", body = new DreamPanel());
        tabbedPane.add("Anti-ban", antiBanBody = new DreamPanel());
        setResizable(true);
        setSize(500,400);
        add(tabbedPane, BorderLayout.CENTER);
        body.setBorder(new EmptyBorder(7,8,7,8));
        body.add(content = new DreamPanel(), BorderLayout.NORTH);
        antiBanBody.setBorder(new EmptyBorder(7,8,7,8));
        antiBanBody.add(antiBanContent = new DreamPanel(), BorderLayout.NORTH);
        GridLayout grid = new GridLayout(0,2);
        grid.setVgap(5);
        content.setLayout(grid);
        antiBanContent.setLayout(grid);

        content.add(new DreamLabel("Mining location: "));
        DreamComboBox<MineLocation> mineLocBox = new DreamComboBox<>(MineLocation.values());
        content.add(mineLocBox);

        // todo add rock list to minelocation and populate rockbox off that list

        content.add(new DreamLabel("Rock type: "));
        DreamComboBox<Rock> rockBox = new DreamComboBox<>(Rock.values());
        content.add(rockBox);

        content.add(new DreamLabel("Bank ores: "));
        DreamCheckBox bankOreCheckBox = new DreamCheckBox();
        bankOreCheckBox.getModel().setSelected(true);
        content.add(bankOreCheckBox);

        content.add(new DreamLabel("Progressive pickaxes: "));
        DreamCheckBox progressiveBox = new DreamCheckBox();
        progressiveBox.getModel().setSelected(true);
        content.add(progressiveBox);

        content.add(new DreamLabel("Use custom bank location: "));
        DreamCheckBox customBankLoc = new DreamCheckBox();
        content.add(customBankLoc);

        content.add(new DreamLabel("Custom bank location: "));
        DreamComboBox<BankLocation> bankLocBox = new DreamComboBox<>(BankLocation.values());
        content.add(bankLocBox);

        // ANTI BAN PANEL
        DreamLabel sleepLowLabel = new DreamLabel("Sleep low (ms): ");
        antiBanContent.add(sleepLowLabel);

        DreamTextField sleepLowTextField = new DreamTextField();
        sleepLowTextField.setText(String.valueOf(Calculations.random(175, 250)));
        antiBanContent.add(sleepLowTextField);

        DreamLabel sleepHighLabel = new DreamLabel("Sleep high (ms):");
        antiBanContent.add(sleepHighLabel);

        DreamTextField sleepHighTextField = new DreamTextField();
        sleepHighTextField.setText(String.valueOf(Calculations.random(278, 360)));
        antiBanContent.add(sleepHighTextField);


        DreamButton startButton = new DreamButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setRockType((Rock) rockBox.getSelectedItem());
                config.setMineLocation((MineLocation) mineLocBox.getSelectedItem());
                config.setShouldBank(bankOreCheckBox.getModel().isSelected());
                config.setProgression(progressiveBox.getModel().isSelected());
                config.setCustomBank(customBankLoc.getModel().isSelected());
                config.setBankLocation((BankLocation) bankLocBox.getSelectedItem());
                config.setSleepLow(Integer.parseInt(sleepLowTextField.getText()));
                config.setSleepHigh(Integer.parseInt(sleepHighTextField.getText()));
                config.setRunning(true);
                dispose();
            }
        });
        content.add(startButton);


    }

    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.setVisible(true);
    }
}

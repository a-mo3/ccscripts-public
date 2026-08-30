package gui;
import config.Config;
import org.dreambot.api.Client;
import org.dreambot.api.methods.grandexchange.LivePrices;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author camalCase
 * @version 1
 * 25th aug refactor
 * desc: basic gui to select container name.
 */
public class GUI {
    Config config = Config.getConfig();


    public void createGUI() {
        JFrame frame = new JFrame();
        frame.setTitle("camalCaseFiller");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(Client.getInstance().getApplet());
        frame.setPreferredSize(new Dimension(300, 100));
        frame.getContentPane().setLayout(new BorderLayout());
        // components
        JPanel settingPanel = new JPanel();
        settingPanel.setLayout((new GridLayout(0, 2)));
        // end
        JLabel containerNameLabel = new JLabel();
        containerNameLabel.setText("Container name:");
        settingPanel.add(containerNameLabel);

        JComboBox<String> containerComboBox = new JComboBox<>(new String[]{
                "Jug", "Bucket", "Bowl", "Vial"});
        settingPanel.add(containerComboBox);
        config.setContainer(containerComboBox.getSelectedItem().toString());
        containerComboBox.addActionListener(
                e -> config.setContainer(containerComboBox.getSelectedItem().toString())
        );
        frame.getContentPane().add(settingPanel, BorderLayout.CENTER);
        // ^ upper v lower

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton button = new JButton();
        button.setText("Start script");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                    config.setScriptRunning(true);
                if (containerComboBox.getSelectedItem().toString().equals("Bucket")) {
                    config.setProfit(LivePrices.get("Bucket of water") - LivePrices.get("Bucket"));
                } else if (containerComboBox.getSelectedItem().toString().equals("Jug")) {
                    config.setProfit(LivePrices.get("Jug of water") - LivePrices.get("Jug"));
                } else if (containerComboBox.getSelectedItem().toString().equals("Bowl")) {
                    config.setProfit(LivePrices.get("Bowl of water") - LivePrices.get("Bowl"));
                } else if (containerComboBox.getSelectedItem().toString().equals("Vial")) {
                    config.setProfit(LivePrices.get("Vial of water") - LivePrices.get("Vial"));
                }
                    frame.dispose();

            }
        });
        buttonPanel.add(button);


        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }

}

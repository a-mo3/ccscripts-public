package org.dreambot.settings.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.settings.BondSettings;
import org.dreambot.settings.SettingsLoader;
import org.dreambot.settings.timing.ReactionGenerator;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Gui extends JFrame {
    public Gui(Object dataClass) {
        Field[] scriptSettings = dataClass.getClass().getDeclaredFields();
        Field[] reactionSettings = ReactionGenerator.getReactionSettings().getClass().getDeclaredFields();
        SettingsLoader<BondSettings> bondLoader = new SettingsLoader<>(BondSettings.class);
        BondSettings b = bondLoader.loadFile("bondSettings.json", new BondSettings());
        Field[] bondSettings = b.getClass().getDeclaredFields();

        setSize(600, 500);
        setTitle(ScriptManager.getScriptManager().getCurrentScript().getSDNName());
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Script Settings", makeSettingsList(
                scriptSettings,
                "settings.json",
                dataClass,
                "Settings relating to the script logic."
        ));
        tabs.add("Reaction Times", makeSettingsList(
                reactionSettings,
                "reactionTime.json",
                ReactionGenerator.getReactionSettings(),
                "High and low of the range your bot will rest between actions, quick is used for deadly situations long is used when you will be waiting on things"
                ));
        tabs.add("Bond Settings", makeSettingsList(bondSettings, "bondSettings.json", b, "The price to buy a bond"));
//        tabs.add("Muling", makeSettingsList(muleSettings, "muleSettings.json", m, "Mule address and port, only needed if you are muling across networks."));
        add(tabs);
        setVisible(true);
    }

    JPanel makeSettingsList(Field[] fields, String fileName, Object instance, String helpMessage) {
        JPanel panel = new JPanel();
        JPanel scrollPanel = new JPanel();
        BoxLayout listLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(listLayout);

        BoxLayout scrollableListLayout = new BoxLayout(scrollPanel, BoxLayout.Y_AXIS);
        scrollPanel.setLayout(scrollableListLayout);

        JScrollPane scrollPane = new JScrollPane(scrollPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(7);
        for (Field f : fields) {
            scrollPanel.add(new GuiOption(f, instance));
        }

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(a -> {
            Gson pretty = new GsonBuilder().setPrettyPrinting().create();
            String json = pretty.toJson(instance);

            try {
                Logger.info(String.format("Saved file: %s", fileName));
                Files.write(Paths.get(SettingsLoader.scriptPath + fileName), json.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        panel.add(scrollPane);
        if (!helpMessage.isEmpty()) {
            JTextPane help = new JTextPane();
            help.setText(helpMessage);
            panel.add(help);
        }

        panel.add(saveButton);
        return panel;
    }

    static int trX = 513;
    static int trY = 5;
    static int height = 30;
    static int width = 100;

    public static void paintButton(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(trX, trY, width, height);
        g.setColor(Color.white);
        g.drawRect(trX, trY, width, height);
        g.drawString("Open Settings", trX + 10, trY + 20);
    }

    public static boolean wasButtonClicked(Point point) {
        boolean clickedX = point.x >= trX && point.x <= trX + width;
        boolean clickedY = point.y >= trY && point.y <= trY + height;
        Logger.info(clickedY + " x: " + clickedX);
        return clickedY && clickedX;
    }

    static int discordX = 513;
    static int discordY = trY + height + 10;

    public static void paintDiscordButton(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(discordX, discordY, width, height);
        g.setColor(Color.white);
        g.drawRect(discordX, discordY, width, height);
        g.drawString("Join Discord", discordX + 10, discordY + 20);
    }

    public static boolean wasDiscordButtonClicked(Point point) {
        boolean clickedX = point.x >= discordX && point.x <= discordX + width;
        boolean clickedY = point.y >= discordY && point.y <= discordY + height;
        Logger.info(clickedY + " x: " + clickedX);
        return clickedY && clickedX;
    }
}

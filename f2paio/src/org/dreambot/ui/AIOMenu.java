package org.dreambot.ui;


import org.dreambot.AIOMain;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.Fractal;
import org.dreambot.ui.components.DreamButton;
import org.dreambot.ui.components.DreamPanel;
import org.dreambot.ui.components.DreamScrollPane;
import org.dreambot.ui.components.DreamTabbedPane;
import org.dreambot.ui.mappings.AbstractSkillMapping;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class AIOMenu extends JFrame {
    private static AIOMenu aioMenu = null;
    LinkedList<SkillComponent> skillComponents = new LinkedList<>();
    DreamPanel listPanel = new DreamPanel();
    // this is the panel that holds list | skillConfigPanel, the tab in the tabbed pane
    DreamPanel settingsPanel = new DreamPanel();
    // for the per skill org.dreambot.settings
    DreamPanel skillConfigPanel = new DreamPanel();

    public static AIOMenu getInstance() {
        if (aioMenu == null) {
            aioMenu = new AIOMenu(SkillIcon.values());
        }
        return aioMenu;
    }

    private AIOMenu(SkillIcon... icons) {

        setTitle("cCAIO");
        setName("cCAIO");

        DreamTabbedPane dreamTabbedPane = new DreamTabbedPane();

        settingsPanel = new DreamPanel();
        BorderLayout settingsPanelLayout = new BorderLayout();
        settingsPanel.setLayout(settingsPanelLayout);

        skillConfigPanel = new DreamPanel();
        skillConfigPanel.setPreferredSize(new Dimension(1920, 400));

        BoxLayout listLayout = new BoxLayout(listPanel, BoxLayout.Y_AXIS);

        listPanel.setPreferredSize(new Dimension(350, 400));
        listPanel.setLayout(listLayout);
        DreamScrollPane scrollPane = new DreamScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        for (SkillIcon icon : icons) {
            SkillComponent skillComponent = new SkillComponent(icon);
            listPanel.add(skillComponent);
            skillComponents.add(skillComponent);
        }

        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

        settingsPanel.add(scrollPane, BorderLayout.LINE_START);
        settingsPanel.add(skillConfigPanel, BorderLayout.LINE_END);
        settingsPanel.add(makeButtonPanel(), BorderLayout.PAGE_END);


        // add selected org.dreambot.settings window to the right
        dreamTabbedPane.add("Skill org.dreambot.settings", settingsPanel);
        add(dreamTabbedPane);

        setMinimumSize(new Dimension(900, 500));
        setVisible(true);
    }

    private DreamPanel makeButtonPanel() {
        DreamPanel buttonPanel = new DreamPanel();
        buttonPanel.setLayout(new FlowLayout());

        DreamButton startButton = new DreamButton("Start");
        buttonPanel.add(startButton);
        startButton.addActionListener(e -> {
            Logger.info("Starting script");

            for (SkillComponent skill : skillComponents) {
                AbstractSkillMapping mapping = skill.icon.getUiMapping();
                mapping.makePanel();
                Fractal f = mapping.makeFractal(mapping.generateDataclass());
                if (f != null) {
                    Logger.info("adding child " + f.getSimpleName());
                    AIOMain.addChildren(f);
                }
            }

            this.setVisible(false);
            SwingUtilities.updateComponentTreeUI(this);
        });


        DreamButton humbleButton = new DreamButton("Buy Proxies");
        humbleButton.addActionListener(e -> {
            Logger.info("Attempting to open https://discord.gg/jzKKjVezPD - HumbleHub discord");
            openURL("https://discord.gg/jzKKjVezPD");
        });
        buttonPanel.add(humbleButton);

        DreamButton bugReportButton = new DreamButton("Bug report / Help");
        humbleButton.addActionListener(e -> {
            Logger.info("Attempting to open https://discord.com/ZMW7jWnCCM - camalCase discord");
            openURL("https://discord.com/ZMW7jWnCCM");
        });
        buttonPanel.add(bugReportButton);
        return buttonPanel;
    }

    private void openURL(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null) {
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                for (StackTraceElement ele : e.getStackTrace()) {
                    Logger.warn(ele);
                }
            }
        } else {
            Logger.info("Desktop is not supported. https://discord.gg/C2uwHcfCaV for get proxies.");
        }
    }

    public void moveSkillComponentUp(SkillIcon s) {
        OptionalInt index = IntStream.range(0, skillComponents.size())
                .filter(i -> skillComponents.get(i).icon == s).findFirst();
        if (!index.isPresent()) {
            System.out.println("Failed to find skill component to move up");
            return;
        }
        int i = index.getAsInt();
        System.out.println("index: " + i);

        Collections.swap(skillComponents, i, i - 1);
        updateListPanel();
    }

    public void moveSkillComponentDown(SkillIcon s) {
        OptionalInt index = IntStream.range(0, skillComponents.size())
                .filter(i -> skillComponents.get(i).icon == s).findFirst();
        if (!index.isPresent()) {
            System.out.println("Failed to find skill component to move up");
            return;
        }
        int i = index.getAsInt();
        System.out.println("index: " + i);

        Collections.swap(skillComponents, i, i + 1);
        updateListPanel();
    }

    private void updateListPanel() {
        listPanel.removeAll();

        for (SkillComponent component : skillComponents) {
            listPanel.add(component);
        }

        SwingUtilities.updateComponentTreeUI(this);
    }

    public void removeHighlights() {
        for (SkillComponent component : skillComponents) {
            component.setBorder(null);
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    public void updateSkillSettings(DreamPanel panel) {
        settingsPanel.remove(skillConfigPanel);
        skillConfigPanel = panel;
        Logger.info("Updating Skill UI");
        settingsPanel.add(skillConfigPanel);
        SwingUtilities.updateComponentTreeUI(this);
    }


}

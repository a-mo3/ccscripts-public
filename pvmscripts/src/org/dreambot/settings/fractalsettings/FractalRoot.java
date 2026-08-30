package org.dreambot.settings.fractalsettings;

import lombok.SneakyThrows;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.ui.GuiOption;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * FractalRoot will replace the default fractal that is used to form a tree in each script
 * FractalRoot takes an instance of a data class & a scriptname, sets the settingloader path to the script name
 *
 * @param <T> the default instance of the script settings
 */
public class FractalRoot<T> extends Fractal implements ConfigurableFractal<T> {
    T instance;
    final String scriptName;

    public FractalRoot(T settingsFile, String scriptName) {
        this.scriptName = scriptName;
        instance = settingsFile;
        // set the settin
        SettingsRepository.changePath(settingName());
        SettingsRepository.getSetting(settingName(), settingsFile);
    }

    public FractalRoot() {
        this.scriptName = "Root";
        log("F root " + SettingsRepository.scriptPath);
    }

    @Override
    public T getSettings() {
        return SettingsRepository.getSetting(scriptName, instance);
    }

    @Override
    public String settingName() {
        return scriptName;
    }

    @SneakyThrows
    public void makeGUI() {
        JFrame baseFrame = new JFrame("cCSettings");
        baseFrame.setSize(600, 500);

        JTabbedPane tabs = new JTabbedPane();

        // add the tab for this scripts settings, it should be assumed root would always have a scripts settings in it
        if (instance != null) {
            tabs.add("Script", makeSettingsList(
                    this.getSettings().getClass().getDeclaredFields(),
                    this.scriptName,
                    this.getSettings(),
                    ""
            ));
        }

        log("Generating UI");
        //
        // when >1 of the same setting fractal exist in a tree use this to prevent tab duplication
        Set<String> alreadyMade = new HashSet<>();
        // add other tabs for every fractal that is configurable
        // todo fully traverse tree not just branches
        try  {
            searchTree(this, tabs, alreadyMade);
        } catch (Exception e) {
            log("Exception");
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log(stackTraceElement.toString());
            }
        }
        log("");
        baseFrame.add(tabs);
        baseFrame.setVisible(true);
        log("Open UI");
    }

    private void searchTree(Fractal f, JTabbedPane tabs, Set<String> alreadyMade) {
        if (!f.getChildren().isEmpty()) {
            f.getChildren().forEach(x -> searchTree(x, tabs, alreadyMade));
        }

        if (!(f instanceof ConfigurableFractal)) return;
        ConfigurableFractal cf = (ConfigurableFractal) f;
        if (alreadyMade.contains(cf.settingName())) return;
        if (cf.getSettings() == null) return;
        tabs.add(cf.settingName(), makeSettingsList(
                cf.getSettings().getClass().getDeclaredFields(),
                cf.settingName(),
                cf.getSettings(),
                ""));
        alreadyMade.add(cf.settingName());
    }

    private JPanel makeSettingsList(Field[] fields, String fileName, Object instance, String helpMessage) {
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
            SettingsRepository.serializeToFile(instance, fileName);
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
}

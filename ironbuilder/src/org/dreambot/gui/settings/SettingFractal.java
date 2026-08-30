package org.dreambot.gui.settings;

import org.dreambot.gui.SettingsUtil;
import org.dreambot.gui.factory.JPaneFractal;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.function.BooleanSupplier;

public abstract class SettingFractal<T> extends JPaneFractal {
    public SettingFractal(BooleanSupplier acceptCondition) {
        super(acceptCondition);
    }

    public T getSettings() {
        return SettingsRepository.getSetting(settingName(), defaultSettings());
    }

    public abstract String settingName();

    // return a default instance of the setting pojo bceause you cant call the constructor of a generic
    public abstract T defaultSettings();

    @Override
    public JPanel makePane() {
        JPanel panel = new JPanel();
        BoxLayout listLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(listLayout);

        T instance = getSettings();
        if (instance == null) {
            return panel; // this would make a blank tab and fuck up the ui but thats alr
        }
        Field[] f = instance.getClass().getFields();
        JPanel optionList = SettingsUtil.makeSettingsList(f, instance);
        panel.add(optionList);

        // save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(a -> SettingsRepository.serializeToFile(instance, settingName()));
        panel.add(saveButton);

        return panel;
    }
}

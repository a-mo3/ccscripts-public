package org.dreambot.gui;

import org.dreambot.api.utilities.Logger;
import org.dreambot.gui.option.GuiOption;
import org.dreambot.gui.option.RequiredCategory;
import org.dreambot.gui.option.UIOptionCategory;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * utility class to make the list of serialized name | selection component
 * this is used for regular script settings and for DTOs in factory widgets
 */
public class SettingsUtil {

    /**
     * 13/04/26 im expanding this to have a category combobox that decides what fields are shown in an object,
     * this replies on annotations UIOptionCategory & RequiredCategory
     * this is for things like in the GUI factory a DTO that encodes all the data for training slayer in
     * wilderness or normal mode, that would have different options
     * or hunter at kebbits / lizards / chins.
     *
     * @param fields     fields of the object
     * @param instance   instance of the settings model
     * @param extraLogic extra logic to be run by the GuiOption when setting the field
     *                   (used for updating fractal children in factory ui)
     * @return JPanel with a vertical GuiOptions
     */
    public static JPanel makeSettingsList(Field[] fields, Object instance, Consumer<Object> extraLogic) {
        Logger.info("Generate setting list " + fields.length);
        JPanel p = new JPanel();

        BoxLayout scrollableListLayout = new BoxLayout(p, BoxLayout.Y_AXIS);
        p.setLayout(scrollableListLayout);

        JScrollPane scrollPane = new JScrollPane(p);
        scrollPane.getVerticalScrollBar().setUnitIncrement(7);
        AtomicReference<Object> categoryValue = new AtomicReference<>();
        addItems(fields, instance, extraLogic, p, categoryValue);
        return p;
    }

    private static void addItems(Field[] fields, Object instance, Consumer<Object> extraLogic, JPanel panel, AtomicReference<Object> categoryValue) {
        for (Field f : fields) {
            // we assume options are always the first field
            UIOptionCategory o = f.getAnnotation(UIOptionCategory.class);
            if (o != null) {
                try {
                    categoryValue.set(f.get(instance));
                } catch (IllegalAccessException e) {
                    Logger.info("Failed to grab category value");
                    throw new RuntimeException(e);
                }

                Logger.info("Option Field UI " + f.getName());
                GuiOption optionCategoryField = new GuiOption(f, instance, v -> {
                    Logger.info("Option Field UI change " + v);
                    categoryValue.set(v);
                    panel.removeAll();
                    addItems(fields, instance, extraLogic, panel, categoryValue);
                    SwingUtilities.updateComponentTreeUI(panel);
                });
                panel.add(optionCategoryField);
                continue;
            }

            RequiredCategory requiredCatagory = f.getAnnotation(RequiredCategory.class);
            if (requiredCatagory != null) {
                Logger.info("Required category on field " + f.getName() + " " + Arrays.toString(requiredCatagory.value()) + " " + categoryValue.get());
//                if (requiredCatagory.value().equals(categoryValue.get().toString())) {
                    if (Arrays.stream(requiredCatagory.value()).anyMatch(x -> categoryValue.get().equals(x))) {
                    Logger.log("Required match");
                    panel.add(new GuiOption(f, instance, extraLogic));
                }
                continue;
            }
            Logger.info("Field UI " + f.getName());
            panel.add(new GuiOption(f, instance, extraLogic));
        }
    }


    /**
     * @param fields   fields of the object
     * @param instance instance of the settings model
     * @return JPanel with a vertical GuiOptions
     */
    public static JPanel makeSettingsList(Field[] fields, Object instance) {
        return makeSettingsList(fields, instance, null);
    }
}

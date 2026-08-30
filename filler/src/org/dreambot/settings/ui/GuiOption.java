package org.dreambot.settings.ui;

import com.google.gson.annotations.SerializedName;
import org.dreambot.api.utilities.Logger;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class GuiOption extends JPanel {

    public GuiOption(Field field, Object instance) {
        SerializedName serialName = field.getAnnotation(SerializedName.class);
        if (serialName != null) {
            add(new JLabel(" " + serialName.value()));
        }
        field.setAccessible(true);

        Type type = field.getType();
        // handle numbers
        if (type == long.class || type == int.class || type == float.class) {
            JSpinner spinner = new JSpinner();
            try {
                spinner.setValue(field.get(instance));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            spinner.addChangeListener(e -> {
                try {
                    Logger.info("Change " + spinner.getValue());
                    field.set(instance, spinner.getValue());
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            });
            add(spinner);
        }

        // handle strings
        if (field.getType() == String.class) {
            // make textfield
            JTextField textField = null;
            try {
                textField = new JTextField((String) field.get(instance));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (textField != null) {
                JTextField finalTextField = textField;
                textField.addActionListener(e -> {
                    try {
                        field.set(instance, finalTextField.getText());
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
            add(textField);
        }

        // handle enums
        if (field.getType().isEnum()) {
            // make selection box
            JComboBox comboBox = new JComboBox(field.getType().getEnumConstants());
            try {
                comboBox.setSelectedItem(field.get(instance));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            comboBox.addActionListener(e -> {
                Logger.info("Combo box action");
                try {
                    field.set(instance, comboBox.getSelectedItem());
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            });
            add(comboBox);
        }

        // handle boolean
        if (field.getType() == boolean.class) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.addChangeListener(e -> {
                try {
                    field.set(instance, checkBox.isSelected());
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            });
            try {
                checkBox.setSelected((Boolean) field.get(instance));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            add(checkBox);
        }
    }
}

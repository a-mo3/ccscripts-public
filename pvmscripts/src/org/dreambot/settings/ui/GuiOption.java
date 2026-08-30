package org.dreambot.settings.ui;

import com.google.gson.annotations.SerializedName;
import org.dreambot.api.utilities.Logger;
import org.dreambot.settings.ui.nui.UIExplanation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class GuiOption extends JPanel {

    public GuiOption(Field field, Object instance) {
        SerializedName serialName = field.getAnnotation(SerializedName.class);
        if (serialName != null) {
            JLabel label = new JLabel(" " + serialName.value());
            UIExplanation explanation = field.getAnnotation(UIExplanation.class);
            if (explanation != null) label.setToolTipText(explanation.value());
            add(label);
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
//            textField.setMaximumSize(new Dimension(20, 20));
            textField.setColumns(15);

            if (textField != null) {
                JTextField finalTextField = textField;
                textField.getDocument().addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                try {
//                                    Logger.info("Set text to " + finalTextField.getText());
                                    field.set(instance, finalTextField.getText());
                                } catch (IllegalAccessException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                try {
//                                    Logger.info("Set text to " + finalTextField.getText());
                                    field.set(instance, finalTextField.getText());
                                } catch (IllegalAccessException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }

                            @Override
                            public void changedUpdate(DocumentEvent e) {
                            }
                        }
                );
            }
            add(textField);
        }

        // handle equipment
        if (field.getType() == EquipmentSelections.class) {
            // add a button
            JButton button = new JButton("CONFIGURE EQUIPMENT");
            button.addActionListener(a -> {
                try {
                    EquipmentSelections equipmentSelections = (EquipmentSelections) field.get(instance);
                    equipmentSelections.makeUI();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });

            add(button);
            return;
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

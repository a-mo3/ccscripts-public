package org.dreambot.gui.option;

import com.google.gson.annotations.SerializedName;
import org.dreambot.api.utilities.Logger;
import org.dreambot.gui.UIExplanation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Consumer;

/**
 * A Gui option is the visual representation of a field in an object, a bunch of these makes up standard cc UIs
 * <p></p>
 */
public class GuiOption extends JPanel {
    final Consumer<Object> extraLogic;
    final Field field;
    final Object instance;

    public GuiOption(Field field, Object instance, Consumer<Object> extraLogic) {
        this.extraLogic = extraLogic;
        this.field = field;
        this.instance = instance;
        try {
            init();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    void init() throws IllegalAccessException {
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
            spinner.setValue(field.get(instance));
            spinner.addChangeListener(e -> setField(spinner.getValue()));
            add(spinner);
        }

        // handle strings
        if (field.getType() == String.class) {
            // make textfield
            JTextField textField = null;
            textField = new JTextField((String) field.get(instance));
//            textField.setMaximumSize(new Dimension(20, 20));
            textField.setColumns(15);

            JTextField finalTextField = textField;
            textField.getDocument().addDocumentListener(
                    new DocumentListener() {
                        @Override
                        public void insertUpdate(DocumentEvent e) {
                            setField(finalTextField.getText());
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                            setField(finalTextField.getText());
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                        }
                    }
            );
            add(textField);
        }

        if (field.getType().isArray()) {
            // todo we just assume this is an array of enums, might not.
            // probably should just make it an object, nothing special about enums for this.
            RuntimeEnumJListModel a = null;
            a = new RuntimeEnumJListModel((Enum<?>[]) field.get(instance), e -> {
                try {
                    field.set(instance, e);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            });
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            add(new JScrollPane(a));
            return;
        }

        if (field.getType().isEnum()) {
            // make selection box
            JComboBox comboBox = new JComboBox(field.getType().getEnumConstants());
            comboBox.setSelectedItem(field.get(instance));

            comboBox.addActionListener(e -> {
                Logger.info("Combo box action");
                setField(comboBox.getSelectedItem());
            });
            add(comboBox);
        }

        // handle boolean
        if (field.getType() == boolean.class) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.addChangeListener(e -> {
                setField(checkBox.isSelected());
            });
            checkBox.setSelected((Boolean) field.get(instance));
            add(checkBox);
        }
    }

    private void setField(Object value) {
        Logger.info("UI Field change " + value);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (extraLogic != null) extraLogic.accept(value);
    }
}

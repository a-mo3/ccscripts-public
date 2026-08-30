package org.dreambot.ui.components;

import javax.swing.*;
import java.util.function.Consumer;

public class DreamToggleButton extends JToggleButton {

    public DreamToggleButton(Icon icon, boolean selected, Consumer<Boolean> handleToggle) {
        super(icon, selected);
        setBackground(isSelected() ? UIColours.BUTTON_COLOUR : UIColours.TEXTFIELD_COLOR);
        this.addActionListener(e -> {
            setBackground(isSelected() ? UIColours.BUTTON_COLOUR : UIColours.TEXTFIELD_COLOR);
            handleToggle.accept(this.isSelected());
        });
    }

    public DreamToggleButton(Icon icon, boolean selected) {
        super(icon, selected);
        setBackground(isSelected() ? UIColours.BUTTON_COLOUR : UIColours.TEXTFIELD_COLOR);
        this.addActionListener(e -> {
            setBackground(isSelected() ? UIColours.BUTTON_COLOUR : UIColours.TEXTFIELD_COLOR);
        });
    }
}

package org.dreambot.ui.components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;

public class DreamSpinnerUI extends BasicSpinnerUI {

    @Override
    protected Component createNextButton() {
        JButton button = (JButton) super.createNextButton();
        button.setBackground(UIColours.BUTTON_COLOUR);
        button.setForeground(UIColours.TEXT_COLOR);
        return button;
    }

    @Override
    protected Component createPreviousButton() {
        JButton button = (JButton) super.createPreviousButton();
        button.setBackground(UIColours.BUTTON_COLOUR);
        button.setForeground(UIColours.TEXT_COLOR);
        return button;
    }
}

package org.dreambot.ui.components;

import javax.swing.*;

public class DreamLabel extends JLabel {

    public DreamLabel() {
        this("");
    }

    public DreamLabel(String text) {
        super(text);
        setOpaque(true);
        setForeground(UIColours.TEXT_COLOR);
        setBackground(UIColours.BODY_COLOUR);
    }
}

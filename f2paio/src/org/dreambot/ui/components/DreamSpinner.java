package org.dreambot.ui.components;

import javax.swing.*;
import java.awt.*;

public class DreamSpinner extends JSpinner {
    public DreamSpinner(SpinnerModel model) {
        super(model);
        setUI(new DreamSpinnerUI());
        JFormattedTextField tf = ((JSpinner.DefaultEditor) getEditor()).getTextField();
        setPreferredSize(new Dimension(200, 30));
        tf.setColumns(3);
    }

    public DreamSpinner() {
        setUI(new DreamSpinnerUI());
    }
}

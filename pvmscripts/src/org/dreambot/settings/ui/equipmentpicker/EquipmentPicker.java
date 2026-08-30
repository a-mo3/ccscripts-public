package org.dreambot.settings.ui.equipmentpicker;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.settings.ui.equipmentpicker.itemsearch.ItemComponent;
import org.dreambot.settings.ui.equipmentpicker.itemsearch.ItemSearch;
import org.dreambot.settings.ui.equipmentpicker.slots.EquipmentSlotPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.function.Consumer;

public class EquipmentPicker extends JFrame {
    JPanel searchPanel = new JPanel();
    EquipmentSlotPanel equipmentSlotPanel;

    Consumer<JPanel> searchSetter = j -> {
        remove(searchPanel);
        searchPanel = j;
        SwingUtilities.updateComponentTreeUI(this);
        Logger.info("Updated tree");
        add(searchPanel);
    };

    public EquipmentPicker(Map<EquipmentSlot, ItemSearch> map, ActionListener onSave) {
        setLayout(new BorderLayout());
//        GridBagConstraints c = new GridBagConstraints();

        equipmentSlotPanel = new EquipmentSlotPanel(searchSetter, map);

        JButton saveButton = new JButton("Save equipment");
        JButton load = new JButton("Load equipment");

        equipmentSlotPanel.setPreferredSize(new Dimension(220, 1));
        add(equipmentSlotPanel, BorderLayout.WEST);
        add(searchPanel, BorderLayout.EAST);

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 2));
        buttons.add(saveButton);
        if (onSave != null) saveButton.addActionListener(onSave);
        buttons.add(load);

        buttons.setPreferredSize(new Dimension(10, 50));
        add(buttons, BorderLayout.SOUTH);

        setTitle("Equipment picker");
        setSize(600, 600);
        setVisible(true);
    }

    public Map<EquipmentSlot, ItemComponent> exportEquipment() {
        return equipmentSlotPanel.getSelectedItems();
    }

    public EquipmentLoadout exportLoadout() {
        return equipmentSlotPanel.makeLoadout();
    }
}

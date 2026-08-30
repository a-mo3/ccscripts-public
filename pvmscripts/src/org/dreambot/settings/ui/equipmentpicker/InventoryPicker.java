package org.dreambot.settings.ui.equipmentpicker;

import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.ui.equipmentpicker.itemsearch.ItemComponent;
import org.dreambot.settings.ui.equipmentpicker.itemsearch.ItemSearch;
import org.dreambot.settings.ui.equipmentpicker.slots.ClickListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class InventoryPicker extends JFrame {
    JLabel selectedItemLabel = new JLabel("No item selected");
    ItemComponent selectedFromSearch;
    List<InventoryItemComponent> inventoryItemList = new ArrayList<>();
    JPanel invPanel = new JPanel();

    public void setSelectedFromSearch(ItemComponent selectedFromSearch) {
        selectedItemLabel.setText("Selected: " + selectedFromSearch.getItem().getName());
        this.selectedFromSearch = selectedFromSearch;
    }

    private void updateInventoryItemList() {
        invPanel.removeAll();
        inventoryItemList.forEach(e -> {
            invPanel.add(new JLabel(String.format(
                    "%s Min: %d Max: %d Restock: %d",
                    e.getItem().getName(), e.getMin(), e.getMax(), e.getRefill()
            )));
        });
        SwingUtilities.updateComponentTreeUI(this);
    }

    public InventoryLoadout getInventoryLoadout() {
        InventoryLoadout loadout = new InventoryLoadout();
        inventoryItemList.forEach(x -> {
            if (x.getVariant() != null) {
                loadout.addItem(x.getVariant(), x.getMin(), x.getMax())
                        .setRefill(x.getRefill());
                return;
            }
            loadout.addItem(x.getItem().getId(), x.getMin(), x.getMax())
                    .setRefill(x.getRefill());

        });
        return loadout;
    }

    public InventoryPicker(ItemSearch itemSearch, ActionListener onSave) {

        // display of all the items we have selected
        invPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
//        invPanel.setLayout(new BoxLayout(invPanel, BoxLayout.Y_AXIS));

        add(invPanel);

        // save button and add items
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(1, 2));
        settingsPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        ItemSearch search = new ItemSearch()
                .addItems(ItemID.SHARK)
                .build();

        search.setComponentClickListener(this::setSelectedFromSearch);
        settingsPanel.add(search);

        // panel for - + and refill configurations for the selected item
        JPanel itemConfigPanel = new JPanel();
        itemConfigPanel.setLayout(new BoxLayout(itemConfigPanel, BoxLayout.Y_AXIS));
        itemConfigPanel.add(selectedItemLabel);

        JPanel spinnersPanel = new JPanel();

        spinnersPanel.add(new JLabel("Min"));
        JSpinner minSpinner = new JSpinner();
        spinnersPanel.add(minSpinner);


        spinnersPanel.add(new JLabel("Max"));
        JSpinner maxSpinner = new JSpinner();
        spinnersPanel.add(maxSpinner);

        spinnersPanel.add(new JLabel("Restock"));
        JSpinner restockSpinner = new JSpinner();
        spinnersPanel.add(restockSpinner);

        itemConfigPanel.add(spinnersPanel);
        Button addItemButton = new Button("Add Item");

        addItemButton.addMouseListener(new ClickListener(e -> {
            if (selectedFromSearch == null) {
                Logger.error("Cant add a null item to inventory.");
                return;
            }

            if (selectedFromSearch.getVariant() != null) {
                inventoryItemList.add(new InventoryItemComponent(selectedFromSearch.getVariant())
                        .setMin((Integer) minSpinner.getValue())
                        .setMax((Integer) maxSpinner.getValue())
                        .setRefill((Integer) restockSpinner.getValue())
                );
            } else {
                inventoryItemList.add(new InventoryItemComponent(selectedFromSearch.getItem())
                        .setMin((Integer) minSpinner.getValue())
                        .setMax((Integer) maxSpinner.getValue())
                        .setRefill((Integer) restockSpinner.getValue())
                );
            }
            updateInventoryItemList();
        }));

        itemConfigPanel.add(addItemButton);


        Button saveInvButton = new Button("Save inventory");
        saveInvButton.addActionListener(onSave);
        itemConfigPanel.add(saveInvButton);

        settingsPanel.add(itemConfigPanel);

        add(settingsPanel);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(600, 600);
        setVisible(true);
    }
}

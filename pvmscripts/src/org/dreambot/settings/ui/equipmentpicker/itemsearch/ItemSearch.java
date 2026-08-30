package org.dreambot.settings.ui.equipmentpicker.itemsearch;

import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.settings.ui.equipmentpicker.trie.Trie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemSearch extends JPanel implements ActionListener {
    //    List<ItemComponent> supportedItems = new ArrayList<>();
    // we dont want to make and mount 1800 UI components for all the items in the game for a slot, use ids
    List<Integer> supportedItemIDs = new ArrayList<>();
    JTextField searchField;
    JPanel itemListPanel;
    Trie t = new Trie();

    public ItemSearch() {
    }

    public ItemSearch addItems(Integer... ids) {
        Arrays.stream(ids).forEach(e -> {
            t.insert(new Item(e, 0).getName());
            supportedItemIDs.add(e);
        });
        return this;
    }

    public ItemSearch addItemVariants(ItemVariant... variants) {
        Arrays.stream(variants).forEach(e -> {
//            supportedItems.add(new ItemComponent(e));
        });
        return this;
    }

    public ItemSearch build() {
        this.setLayout(new BorderLayout());

        searchField = new JTextField();
        searchField.addActionListener(this);
        add(searchField, BorderLayout.PAGE_START);

        itemListPanel = new JPanel();
        itemListPanel.setLayout(new BoxLayout(itemListPanel, BoxLayout.Y_AXIS));
        for (int i : supportedItemIDs.stream().limit(20).collect(Collectors.toList())) {
            ItemComponent itemComponent = new ItemComponent(new Item(i, 1));
            itemListPanel.add(itemComponent);
        }
        JScrollPane scrollPane = new JScrollPane(itemListPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        add(scrollPane, BorderLayout.CENTER);
        setSize(300, 500);
//        setVisible(true);
        return this;
    }

    String lastSearch = "";

    @Override
    public void actionPerformed(ActionEvent e) {
        Logger.info("Action performed.");
        String text = searchField.getText();
        Logger.info("Searching for text " + text);
        itemListPanel.removeAll();
        if (text.isEmpty()) {
            Logger.info("Search empty");
            // add back the first 20 items
            for (int i : supportedItemIDs.stream().limit(20).collect(Collectors.toList())) {
                ItemComponent itemComponent = new ItemComponent(new Item(i, 1));
                itemListPanel.add(itemComponent);
            }
            lastSearch = text;
            SwingUtilities.updateComponentTreeUI(this);
            SwingUtilities.updateComponentTreeUI(itemListPanel);
            itemListPanel.updateUI();
            return;
        }

        if (!lastSearch.equals(text)) {
            LinkedList<String> validAutocompletedNames = t.autocomplete(text);
            Logger.info(validAutocompletedNames);
            // add first 20 that start with the search string
            for (int i : supportedItemIDs.stream()
                    .filter(x -> new Item(x, 0).getName().startsWith(text))
                    .limit(20).collect(Collectors.toList())) {
                ItemComponent itemComponent = new ItemComponent(new Item(i, 1));
                itemListPanel.add(itemComponent);
            }

        }
        lastSearch = text;
        itemListPanel.updateUI();
        SwingUtilities.updateComponentTreeUI(this);
        SwingUtilities.updateComponentTreeUI(itemListPanel);
    }

    public void setComponentClickListener(Consumer<ItemComponent> idListener) {
    }
}

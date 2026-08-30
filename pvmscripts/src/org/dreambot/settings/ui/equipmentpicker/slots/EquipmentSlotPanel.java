package org.dreambot.settings.ui.equipmentpicker.slots;

import lombok.Getter;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.settings.ui.equipmentpicker.itemsearch.ItemComponent;
import org.dreambot.settings.ui.equipmentpicker.itemsearch.ItemSearch;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EquipmentSlotPanel extends JPanel {
    JPanel itemListPanel;

    Map<EquipmentSlot, ItemSearch> searchPanelMap;
    @Getter
    Map<EquipmentSlot, ItemComponent> selectedItems = new HashMap<>(); // todo need defaults

    public EquipmentSlotPanel(Consumer<JPanel> searchSetter, Map<EquipmentSlot, ItemSearch> map) {
        searchPanelMap = map;
        this.setLayout(new BorderLayout());

        itemListPanel = new JPanel();
        itemListPanel.setLayout(new BoxLayout(itemListPanel, BoxLayout.Y_AXIS));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
//            if (Arrays.stream(blacklistedSlot).anyMatch(x -> x == slot)) continue;
            if (!searchPanelMap.containsKey(slot)) continue;
            Item item = new Item(0, 0);
            EquipmentSlotComponent slotComponent = new EquipmentSlotComponent(item, slot);

            slotComponent.addMouseListener(new ClickListener(e -> {
                Logger.info("Clicked: " + slot);
//                searchSetter.accept(searchPanelMap.get(slot));
            }));

            searchPanelMap.get(slot).setComponentClickListener(itemComponent -> {
                Logger.info("Selected " + itemComponent + " Slot " + slot);
                selectedItems.put(slot, itemComponent);
                slotComponent.setItemComponent(itemComponent);
            });

//            if (i % 2 != 0) itemComponent.setBackground(UIColours.BODY_COLOUR.brighter());
//            itemComponentList.add(itemComponent);

            itemListPanel.add(slotComponent);
        }
        JScrollPane scrollPane = new JScrollPane(itemListPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        add(scrollPane, BorderLayout.CENTER);
        setSize(300, 500);
        setVisible(true);
    }

    public EquipmentSlotPanel addItems(EquipmentSlot slot, Integer... ids) {
        searchPanelMap.put(slot, new ItemSearch().addItems(ids));
        return this;
    }

    public EquipmentLoadout makeLoadout() {
        EquipmentLoadout loadout = new EquipmentLoadout();

        selectedItems.forEach((slot, itemComponent) -> {
            if (itemComponent.getVariant() != null) {
                loadout.addItem(slot, new EquipmentLoadoutItem(itemComponent.getVariant()))
                        // todo maybe min max, not needed for most of equipment other than ammo, but there isnt ammo that is a variant
                        .setRefill(itemComponent.getRefill());
                return;
            }
            loadout.addItem(slot, new EquipmentLoadoutItem(itemComponent.getItem().getId(), itemComponent.getMin(), itemComponent.getMax()));
        });

        return loadout;
    }
}

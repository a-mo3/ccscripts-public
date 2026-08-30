package org.dreambot.settings.ui.equipmentpicker.slots;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.settings.ui.equipmentpicker.itemsearch.ItemComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * component for an equipment item in any given slot
 */
public class EquipmentSlotComponent extends JPanel {
    Item item;
    ImageIcon icon;
    // all the stuff needed for making the equipment item
    ItemComponent itemComponent;


    public EquipmentSlotComponent(Item item, EquipmentSlot slot) {
        this.item = item;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        BufferedImage sprite = item.getImage();
        icon = new ImageIcon(sprite);
        JLabel spriteLabel = new JLabel(icon);
        add(spriteLabel);

        JLabel itemName = new JLabel(slot.toString());
        add(itemName);
    }

    public void setItemComponent(ItemComponent itemComponent) {
        icon.setImage(itemComponent.getItem().getImage());
        SwingUtilities.updateComponentTreeUI(this);
        this.itemComponent = itemComponent;
    }


}

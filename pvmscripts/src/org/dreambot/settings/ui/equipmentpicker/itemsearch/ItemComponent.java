package org.dreambot.settings.ui.equipmentpicker.itemsearch;

import lombok.Getter;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.loadout.ItemVariant;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
public class ItemComponent extends JPanel {
    Item item;

    // all the stuff needed for making the equipment item
    ItemVariant variant;
    int min = 1;
    int max = 1;
    int refill = 1;

    public ItemComponent(ItemVariant variant) {
        this.item = new Item(variant.getBaseId(), 0);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        BufferedImage sprite = item.getImage();
        ImageIcon icon = new ImageIcon(sprite);
        JLabel spriteLabel = new JLabel(icon);
        add(spriteLabel);

        JLabel itemName = new JLabel(item.getName());
        add(itemName);
    }

    public ItemComponent(Item item) {
        this.item = item;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        BufferedImage sprite = item.getImage();
        ImageIcon icon = new ImageIcon(sprite);
        JLabel spriteLabel = new JLabel(icon);
        add(spriteLabel);

        JLabel itemName = new JLabel(item.getName());
        add(itemName);
    }
}

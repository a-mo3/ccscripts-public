package org.dreambot.settings.ui.equipmentpicker;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.loadout.ItemVariant;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@Accessors(chain = true)
@Setter
@Getter
public class InventoryItemComponent extends JPanel {
    Item item;

    // all the stuff needed for making the equipment item
    ItemVariant variant;
    int min = 1;
    int max = 1;
    int refill = 1;

    public InventoryItemComponent(ItemVariant variant) {
        this.item = new Item(variant.getBaseId(), 0);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        BufferedImage sprite = item.getImage();
        ImageIcon icon = new ImageIcon(sprite);
        JLabel spriteLabel = new JLabel(icon);
        add(spriteLabel);

        // todo add a remove button

        JLabel itemName = new JLabel(item.getName());
        add(itemName);
    }

    public InventoryItemComponent(Item item) {
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

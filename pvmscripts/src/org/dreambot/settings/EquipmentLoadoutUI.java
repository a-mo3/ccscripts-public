package org.dreambot.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.SneakyThrows;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.LoadoutMapEntry;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.ui.equipmentpicker.itemsearch.ItemSearch;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class EquipmentLoadoutUI extends JFrame {
    // holds the active item search
    JPanel searchParent = new JPanel();
    ItemSearch lastSearch = null;
    Map<EquipmentSlot, JButton> slotJButtonMap = new HashMap<>();
    EquipmentLoadout equipmentLoadout = new EquipmentLoadout();

    void setSearch(ItemSearch itemSearch) {
        if (lastSearch != null) searchParent.remove(lastSearch);
        lastSearch = itemSearch;
        searchParent.add(itemSearch);
        SwingUtilities.updateComponentTreeUI(this);

    }

    Dimension buttonSize = new Dimension(80, 80); // Square buttons
    Insets buttonPadding = new Insets(10, 5, 10, 5); // Padding around buttons
    // Create buttons for each equipment slot
    JButton helmet = createButton("Helmet", buttonSize, buttonPadding, new ItemSearch().addItems(ItemID.ADAMANT_FULL_HELM).build());
    JButton cape = createButton("Cape", buttonSize, buttonPadding, new ItemSearch().addItems().build());
    JButton necklace = createButton("Necklace", buttonSize, buttonPadding, new ItemSearch().addItems(ItemID.AMULET_OF_AVARICE).build());
    JButton arrows = createButton("Arrows", buttonSize, buttonPadding, new ItemSearch().addItems(ItemID.ADAMANT_ARROW).build());
    JButton weapon = createButton("Weapon", buttonSize, buttonPadding, new ItemSearch().addItems(ItemID.SARADOMIN_SWORD).build());
    JButton bodyArmor = createButton("Body Armor", buttonSize, buttonPadding, new ItemSearch().addItems(ItemID.ARMADYL_CHESTPLATE).build());
    JButton shield = createButton("Shield", buttonSize, buttonPadding, new ItemSearch().addItems(ItemID.CRYSTAL_SHIELD).build());
    JButton legsArmor = createButton("Legs Armor", buttonSize, buttonPadding, new ItemSearch().addItems(ItemID.CRYSTAL_LEGS).build());
    JButton gloves = createButton("Gloves", buttonSize, buttonPadding, new ItemSearch().addItems(ItemID.ICE_GLOVES).build());
    JButton boots = createButton("Boots", buttonSize, buttonPadding, new ItemSearch().addItems(ItemID.BOOTS_OF_STONE).build());
    JButton ring = createButton("Ring", buttonSize, buttonPadding, new ItemSearch().addItems(ItemID.RING_OF_DUELING1).build());

    /**
     * location to save custom loadouts to
     * full path would be something like
     * DreamBot/Scripts/cCGDK/NMZ/custom-party-loadout (json)
     * in that example "NMZ" would be the equipmentSubFolder
     */
    final String equipmentSubFolder;
    // whats saved if theres none saved
    final EquipmentLoadout defaultLoadout;

    @SneakyThrows
    /**
     * if the loadouts are dependant on suppliers they will break
     */
    public EquipmentLoadoutUI(String equipmentSubFolder, EquipmentLoadout defaultLoadout, EquipmentLoadoutUISelectionItem... loadoutOptions) {
        this.defaultLoadout = defaultLoadout;
        this.equipmentSubFolder = equipmentSubFolder;
        slotJButtonMap.put(EquipmentSlot.HAT, helmet);
        slotJButtonMap.put(EquipmentSlot.CAPE, cape);
        slotJButtonMap.put(EquipmentSlot.AMULET, necklace);
        slotJButtonMap.put(EquipmentSlot.ARROWS, arrows);
        slotJButtonMap.put(EquipmentSlot.WEAPON, weapon);
        slotJButtonMap.put(EquipmentSlot.CHEST, bodyArmor);
        slotJButtonMap.put(EquipmentSlot.SHIELD, shield);
        slotJButtonMap.put(EquipmentSlot.LEGS, legsArmor);
        slotJButtonMap.put(EquipmentSlot.HANDS, gloves);
        slotJButtonMap.put(EquipmentSlot.FEET, boots);
        slotJButtonMap.put(EquipmentSlot.RING, ring);
        setTitle("Equipment Interface");
        setSize(600, 800);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the left panel with equipment interface
        JPanel equipmentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Define button size and padding


        // Position buttons in the grid
        gbc.insets = buttonPadding;

        // Row 0
        gbc.gridx = 1;
        gbc.gridy = 0;
        equipmentPanel.add(helmet, gbc);

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        equipmentPanel.add(cape, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        equipmentPanel.add(weapon, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        equipmentPanel.add(necklace, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        equipmentPanel.add(arrows, gbc);

        // Row 3
        gbc.gridx = 1;
        gbc.gridy = 2;
        equipmentPanel.add(bodyArmor, gbc);

        // Row 4
        gbc.gridx = 0;
        gbc.gridy = 4;
        equipmentPanel.add(gloves, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        equipmentPanel.add(legsArmor, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        equipmentPanel.add(shield, gbc);

        // Row 5
        gbc.gridx = 1;
        gbc.gridy = 4;
        equipmentPanel.add(boots, gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        equipmentPanel.add(ring, gbc);

        // Create the right panel with a blue border
//        searchParent.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
//        searchParent.add(new ItemSearch().addItems(ItemID.ADAMANT_2H_SWORD, ItemID.SARADOMIN_SWORD).build());

        // Use a JSplitPane to divide the left and right panels
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, equipmentPanel, searchParent);
//        splitPane.setDividerLocation(350);

        // Add the split pane to the frame
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));


        JButton loadButton = new JButton("Load current equipment");

        // Create a selection menu (JComboBox)

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // load custom equipments
        Files.createDirectories(Paths.get(SettingsRepository.scriptPath + "/" + equipmentSubFolder));
        List<EquipmentLoadoutUISelectionItem> customOptions = Files.list(Paths.get(SettingsRepository.scriptPath + "/" + equipmentSubFolder))
                .map(file -> {
                    try {
                        return new EquipmentLoadoutUISelectionItem(file.toFile().getName(), gson.fromJson(new String(Files.readAllBytes(file.toAbsolutePath())), EquipmentLoadout.class));
                    } catch (IOException ignored) {
                        Logger.warn("Failed to load a custom loadout " + file.getFileName());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        ArrayList<EquipmentLoadoutUISelectionItem> allOptions = new ArrayList<>(Arrays.asList(loadoutOptions));
        allOptions.addAll(customOptions);

        JComboBox<EquipmentLoadoutUISelectionItem> selectionMenu = new JComboBox<>(allOptions.toArray(new EquipmentLoadoutUISelectionItem[0]));
        selectionMenu.addActionListener(a -> {
            EquipmentLoadoutUISelectionItem selectedLoadout = (EquipmentLoadoutUISelectionItem) selectionMenu.getSelectedItem();
            if (selectedLoadout == null) {
                Logger.warn("Selected loadout is null?");
                return;
            }
            equipmentLoadout = selectedLoadout.equipmentLoadout;
            Logger.info("Saving last loadout");
            setLastUsed(equipmentLoadout);
            // empty the UI
            slotJButtonMap.values().forEach(x -> {
                x.setText("");
                x.setIcon(null);
            });
            for (LoadoutMapEntry entry : equipmentLoadout.getLoadoutList()) {
                JButton but = slotJButtonMap.get(entry.getSlot());
                but.setIcon(new ImageIcon(new Item(entry.getItem().getBaseID(), 0).getImage()));
            }
        });

        // todo check foward and blackslash OS compatibility
        JButton saveButton = new JButton("Save custom loadout");
        saveButton.addActionListener(a -> {
            // ensure sub folder exists
            try {
                Files.createDirectories(Paths.get(SettingsRepository.scriptPath + "/" + equipmentSubFolder));
            } catch (IOException ignored) {
                Logger.warn("Failed to write equipment directory");
            }
            // prompt user for name of new loadout
            String name = JOptionPane.showInputDialog(null, "Give this loadout a name", "Input Prompt", JOptionPane.QUESTION_MESSAGE);
            if (name == null) {
                Logger.error("No name given not saving.");
                return;
            }

            // save the loadout.
            Logger.info("Attempting to save loadout");
            try {
                File f = new File(SettingsRepository.scriptPath + "/" + equipmentSubFolder + "/" + name);
                if (!f.exists()) {
                    f.getParentFile().mkdirs();
                    Logger.info(String.format("Failed to load file %s, made default and trying again.", name));
                }

                Files.write(f.toPath(), new Gson().toJson(equipmentLoadout).getBytes());
                Logger.info("Saved");
                setLastUsed(equipmentLoadout);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // todo something for the option to show up instantly
        });

        // on load button, iterate through your item slots, check for item variants, and set up a loadout
        loadButton.addActionListener(x -> {
            equipmentLoadout = new EquipmentLoadout();
            slotJButtonMap.values().forEach(j -> {
                j.setText("");
                j.setIcon(null);
            });
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                Item itemInSlot = Equipment.getItemInSlot(slot);
                if (itemInSlot == null) continue;


                JButton button = slotJButtonMap.get(slot);
                if (button == null) {
                    Logger.warn("Slot null button " + slot);
                    slotJButtonMap.keySet().forEach(Logger::info);
                    continue;
                }

                // check for vaiants, since this is an infrequent operation i dont care if this is inefficient
                EquipmentLoadoutVariants variant = Arrays.stream(EquipmentLoadoutVariants.values())
                        .filter(j -> Arrays.asList(j.itemVariant.getIds()).contains(itemInSlot.getId()))
                        .findFirst().orElse(null);
                if (variant != null) {
                    Logger.info(String.format("Acknowledged Variant in slot %s - %s", slot, variant));
                    // theres no support for itemvariant quantities because i dont think theres any variant ammo/weapon in the game.
                    equipmentLoadout.addItem(slot, variant.itemVariant);
                    button.setIcon(new ImageIcon(itemInSlot.getImage()));
                    button.setText("");
                    SwingUtilities.updateComponentTreeUI(this);
                    continue;
                }

                if (itemInSlot.isStackable()) {
                    // 1 might be too low a minimum,
                    // especially in scripts that follow the pattern of not enforcing loadout once you are in the combat area
                    equipmentLoadout.addItem(slot, new EquipmentLoadoutItem(itemInSlot.getId(), 1, itemInSlot.getAmount()));
                } else {
                    equipmentLoadout.addItem(slot, itemInSlot.getId());
                }
                button.setIcon(new ImageIcon(itemInSlot.getImage()));
                button.setText("");
                SwingUtilities.updateComponentTreeUI(this);
            }
            setLastUsed(equipmentLoadout);
        });

        // todo load whatever the last selected equipment loadout was and set it as the current loadout

        // Add buttons and selection menu to the bottom panel
        equipmentLoadout = getLastUsed();
        slotJButtonMap.values().forEach(x -> {
            x.setText("");
            x.setIcon(null);
        });
        for (LoadoutMapEntry entry : equipmentLoadout.getLoadoutList()) {
            JButton but = slotJButtonMap.get(entry.getSlot());
            but.setIcon(new ImageIcon(new Item(entry.getItem().getBaseID(), 0).getImage()));
        }
        bottomPanel.add(saveButton);
        bottomPanel.add(loadButton);
        bottomPanel.add(selectionMenu);
        add(equipmentPanel);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private EquipmentLoadout getLastUsed() {
        String lastUserPath = SettingsRepository.scriptPath + "/" + equipmentSubFolder + "/lastLoadout";
        if (!Files.exists(Paths.get(lastUserPath))) {
            Logger.info("No file");
            setLastUsed(defaultLoadout);
            return defaultLoadout;
        }

        try (FileReader reader = new FileReader(lastUserPath)) {
            EquipmentLoadout load = new GsonBuilder()
                    .create()
                    .fromJson(reader, EquipmentLoadout.class);
            return load;
        } catch (IOException e) {
            Logger.error("Error reading the file: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            Logger.error("Error parsing JSON: " + e.getMessage());
        }
        return null;
    }

    private void setLastUsed(EquipmentLoadout loadout) {
        SettingsRepository.serializeToFile(loadout, equipmentSubFolder + "/lastLoadout");
    }

    // Helper method to create buttons with padding and size
    private JButton createButton(String text, Dimension size, Insets padding, ItemSearch itemSearch) {
        JButton button = new JButton(text);
        button.setPreferredSize(size);
        button.setMargin(padding);
        button.addActionListener(e -> setSearch(itemSearch));
        return button;
    }
}
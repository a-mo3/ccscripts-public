package org.dreambot.settings.ui;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.training.nmz.NMZEquipment;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.settings.EquipmentLoadoutUI;
import org.dreambot.settings.EquipmentLoadoutUISelectionItem;
import org.dreambot.settings.fractalsettings.SettingsRepository;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public enum EquipmentSelections {
    NMZ("NMZ",
            NMZEquipment.OBSIDIAN.getLoadout(),
            new EquipmentLoadoutUISelectionItem("OBSIDIAN", NMZEquipment.OBSIDIAN.getLoadout()),
            new EquipmentLoadoutUISelectionItem("FULL_OBSIDIAN", NMZEquipment.FULL_OBSIDIAN.getLoadout())
    ),

    RANGE_NMZ("RANGE_NMZ",
            NMZEquipment.OBSIDIAN.getLoadout(),
            new EquipmentLoadoutUISelectionItem("OBSIDIAN", NMZEquipment.OBSIDIAN.getLoadout()),
            new EquipmentLoadoutUISelectionItem("FULL_OBSIDIAN", NMZEquipment.FULL_OBSIDIAN.getLoadout())
    ),

    ;

    final String path;
    final EquipmentLoadout defaultLoadout;
    final EquipmentLoadoutUISelectionItem[] presets;

    EquipmentSelections(String path, EquipmentLoadout defaultLoadout, EquipmentLoadoutUISelectionItem... presets) {
        this.path = path;
        this.defaultLoadout = defaultLoadout;
        this.presets = presets;
    }

    public void makeUI() {
        Logger.info("Making UI for " + path);
        new EquipmentLoadoutUI(path, defaultLoadout, presets);
    }

    public EquipmentLoadout getLoadout() {
        // load, deal with null case somehow
        String lastUserPath = SettingsRepository.scriptPath + "/" + path + "/lastLoadout";
        if (!Files.exists(Paths.get(lastUserPath))) {
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
}

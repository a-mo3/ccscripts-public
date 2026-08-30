package org.dreambot.behaviour.training.nmz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CollectHerbBoxes extends Fractal implements ConfigurableFractal<DailiesSettings>, ChatListener {
    int cachedDay = -3;

    public CollectHerbBoxes() {
        Client.getInstance().addEventListener(this);

        this.paintArraySupplier = () -> new String[]{
                "Cached Day " + cachedDay,
                "Current Day " + currentDay()
        };
    }

    @Override
    public boolean isValid() {
        if (NightmareZone.getNMZPoints() < 9500 || !getSettings().collectHerbBoxes) return false;
        if (cachedDay < -1) cachedDay = loadDay();
        return (currentDay() != cachedDay);
    }

    public static final Tile CHEST_TILE = new Tile(2609, 3119);

    @Override
    public int onLoop() {
        if (Inventory.contains("Herb box")) {
            log("Bank all herb box");
            Inventory.interact("Herb box", "Bank-all");
            Sleep.sleepUntil(() -> !Inventory.contains("Herb box"), 2400);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.emptySlotCount() < 15) {
            log("Need inventory space");
            new BankAllInventoryEvent().execute();
            return ReactionGenerator.getNormal();
        }

        if (CHEST_TILE.distance() > 10) {
            log("Walk to rewards chest");
            if (Walking.shouldWalk()) Walking.walk(CHEST_TILE);
            return ReactionGenerator.getNormal();
        }

        Widget parent = Widgets.getWidget(206);
        if (parent == null || !parent.isVisible()) {
            GameObject rewardChest = GameObjects.closest("Rewards chest");
            if (rewardChest == null) {
                log("Failed to find reward chest");
                return ReactionGenerator.getNormal();
            }

            log("Search reward chest");
            rewardChest.interact("Search");
            Sleep.sleepUntil(Widgets::isOpen, 4400);
            return ReactionGenerator.getNormal();
        }

        WidgetChild openBenefitTabButton = Widgets.get(x -> x.hasAction("Resources"));
        if (openBenefitTabButton != null) {
            log("Open resources tab");
            openBenefitTabButton.interact("Resources");
            return ReactionGenerator.getNormal() + 1200;
        }

        // buy the herb boxes
        WidgetChild buyHerb = Widgets.get(x -> x.getName().contains("Herb box") && x.hasAction("Buy-50"));
        if (buyHerb == null) {
            log("Failed to find buy herb box button");
            return ReactionGenerator.getNormal();
        } else {
            log("Buy 50 of dem jaunts");
            buyHerb.interact("Buy-50");
            return ReactionGenerator.getNormal();
        }
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message.getMessage().contains("Please try again tomorrow")) {
            saveDay(currentDay());
            cachedDay = currentDay();
        }
    }

    // herb boxes reset at UTC 00 00
    private int currentDay() {
        return (int) (System.currentTimeMillis() / 84_600_000);
    }

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String FILE_PATH = System.getProperty("scripts.path") + "/cCDailies/" + ScriptManager.getScriptManager().getAccountNickname() + "/" + "data.json";

    // Save an int to a file
    public void saveDay(int value) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(value, writer);
            System.out.println("Saved value: " + value);
        } catch (IOException e) {
            log("Io exception ");
            e.printStackTrace();
        }
    }

    // Load an int from a file, or save a default value if the file doesn't exist
    public int loadDay() {
        Path path = Paths.get(FILE_PATH);

        // If the file doesn't exist, save a default value (e.g., 0) and return it
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log("File does not exist. Creating file with default value.");
            saveDay(1); // Save default value
            return currentDay(); // Return default value
        }

        // If the file exists, read the value
        try (FileReader reader = new FileReader(FILE_PATH)) {
            int value = gson.fromJson(reader, int.class);
            log("Loaded day value: " + value);
            return value;
        } catch (IOException ignored) {
            log("Failed to load day return today");
            return currentDay(); // Return -1 or handle the error as needed
        }
    }

    @Override
    public DailiesSettings getSettings() {
        return SettingsRepository.getSetting(settingName(), new DailiesSettings());
    }

    @Override
    public String settingName() {
        return "Dailies";
    }
}

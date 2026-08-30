package org.dreambot.gui.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.dreambot.api.utilities.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Object store for settings instances, should not have more than 1 setting instance
 */
public class SettingsRepository {
    // String filename, Object settingsInstance
    private static final Map<String, Object> settingsRegistry = new HashMap<>();
    public static String scriptPath = System.getProperty("scripts.path");

    public static void changePath(String scriptName) {
        scriptPath = System.getProperty("scripts.path") + scriptName + "/";
    }

    public static <T> T getSetting(String filename, T defaultInstance) {
        if (settingsRegistry.containsKey(filename)) return (T) settingsRegistry.get(filename);
        Gson gson = new Gson();
        String fullFilePath = scriptPath + filename;

        if (!Files.exists(Paths.get(fullFilePath))) {
            serializeToFile(defaultInstance, filename);
            return defaultInstance;
        }

        try (FileReader reader = new FileReader(fullFilePath)) {
            T load = gson.fromJson(reader, (Class<T>) defaultInstance.getClass());
            settingsRegistry.put(filename, load);
            return load;
        } catch (IOException e) {
            Logger.error("Error reading the file: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            Logger.error("Error parsing JSON: " + e.getMessage());
        }
        return null;
    }

    public static <T> void serializeToFile(T object, String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String fullFilePath = scriptPath + filename;
        settingsRegistry.put(filename, object);
        File parentDir = new File(fullFilePath).getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                System.err.println("Failed to create parent directories for: " + filename);
                return;
            }
        }


        try (FileWriter writer = new FileWriter(fullFilePath)) {
            Logger.info("Saving...");
            gson.toJson(object, writer);
        } catch (IOException e) {
            Logger.error("Error writing to file: " + e.getMessage());
        }
    }
}
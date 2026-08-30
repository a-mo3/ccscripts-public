package org.dreambot.settings.fractalsettings;

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
 * save and load files, but also keep a map of filenames and instances so fractals wont read disk when checking settings
 */
public class SettingsRepository {
    // String filename, Object settingsInstance
    private static Map<String, Object> settingsRegistry = new HashMap();
    public static String scriptPath = System.getProperty("scripts.path");

    public static void changePath(String scriptName) {
        scriptPath = System.getProperty("scripts.path") + scriptName + "/";
    }

    public static <T> T getSetting(String filename, T defaultInstance) {
        if (defaultInstance == null) {
            Logger.info("Default instance null failed to load settings " + filename);
            return null;
        }
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

    public static <T> boolean serializeToFile(T object, String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String fullFilePath = scriptPath + filename;
        settingsRegistry.put(filename, object);
        File parentDir = new File(fullFilePath).getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                System.err.println("Failed to create parent directories for: " + filename);
                return false;
            }
        }


        try (FileWriter writer = new FileWriter(fullFilePath)) {
            Logger.info("Saving...");
            gson.toJson(object, writer);
            return true;
        } catch (IOException e) {
            Logger.error("Error writing to file: " + e.getMessage());
        }
        return false;
    }

    /**
     * to replace where we are accessing a scripts settings files without dep injection in hopes this is better than with strings
     * we should replace this with dep injection every loop we are wasting memory by making new settings classes
     *
     * @param clazz an instance of the settings file you're trying to check
     * @param <T>
     * @return
     */
    public static <T> T findInstanceOf(T clazz) {
        // questionable
        T a = (T) settingsRegistry.values().stream()
                .filter(x -> x.getClass().equals(clazz.getClass()))
                .findFirst()
                .orElse(null);
        if (a == null) {
            Logger.info("Setting load failed to find same type " + clazz.getClass());
            return clazz;
        }
        return a;
    }

}

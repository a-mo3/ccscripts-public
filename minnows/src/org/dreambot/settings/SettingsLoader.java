package org.dreambot.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dreambot.api.utilities.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SettingsLoader<SettingsClass> {
    public static final String scriptPath = System.getProperty("scripts.path") + "/cCMinnows/";
    private final Class<SettingsClass> type;

    public SettingsLoader(Class<SettingsClass> tClass) {
         type = tClass;
    }

    /**
     * @param fileName file of name you want to load
     * @param defaultSave default data to save if the file doesnt exist
     * @return the object loaded
     */
    public SettingsClass loadFile(String fileName, SettingsClass defaultSave) {
        File f = new File(scriptPath + fileName);
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            saveFile(fileName, defaultSave);
            Logger.info(String.format("Failed to load file %s, made default and trying again.", fileName));
            return loadFile(fileName, defaultSave);
        }

        try {
            String settings = new String(Files.readAllBytes(Paths.get(scriptPath + fileName)));
            Logger.info(String.format("Loaded file: %s", fileName));
            return new Gson().fromJson(settings, type);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * saves an object as pretty json
     * @param fileName file you want to save
     * @param data the object you want to save
     */
    public void saveFile(String fileName, SettingsClass data) {
        Gson pretty = new GsonBuilder().setPrettyPrinting().create();
        String json = pretty.toJson(data);

        try {
            Logger.info(String.format("Saved file: %s", fileName));
            Files.write(Paths.get(scriptPath + fileName), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

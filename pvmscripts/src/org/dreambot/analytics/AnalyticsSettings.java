package org.dreambot.analytics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.dreambot.api.utilities.Logger;
import org.dreambot.settings.fractalsettings.SettingsRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AnalyticsSettings {
    private static AnalyticsSettings instance;

    private AnalyticsSettings() {
    }

    public static boolean isAnalyticsOn() {
        return false;
//        if (instance == null) {
//            instance = loadFile("analyticsSettings.json", new AnalyticsSettings());
//            if (instance == null) {
//                Logger.error("Failed to load analytics settings");
//                instance = new AnalyticsSettings();
//            }
//        }
//        return instance.analyticsOn;
    }

    // returns what it was set to
    public static boolean toggleAnalytics() {
        if (instance == null) {
            instance = SettingsRepository.getSetting("analyticsSettings.json", new AnalyticsSettings());
            if (instance == null) {
                Logger.error("Failed to load analytics settings");
                instance = new AnalyticsSettings();
            }
        }

        instance.analyticsOn = !instance.analyticsOn;
        Logger.info("Saving file");
        saveFile("analyticsSettings.json", instance);
        return instance.analyticsOn;
    }


    @SerializedName("analyticsOn")
    public boolean analyticsOn = false;


    public static final String scriptPath = System.getProperty("scripts.path") + "/";

    private static AnalyticsSettings loadFile(String fileName, AnalyticsSettings defaultSave) {
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
            return new Gson().fromJson(settings, AnalyticsSettings.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void saveFile(String fileName, AnalyticsSettings data) {
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

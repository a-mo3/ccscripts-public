package org.dreambot.scout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dreambot.api.Client;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Panopticon implements SpawnListener {
    private static Panopticon singleton;
    public static final String path = System.getProperty("scripts.path") + "cCScout.json";

    public static Panopticon start() {
        PanopticonSettings settings = loadFile();

        if (settings == null || !settings.isEnabled()) {
            return null;
        }

        if (singleton == null) singleton = new Panopticon();
        Client.getInstance().addEventListener(singleton);
        return singleton;
    }

    @Override
    public void onPlayerSpawn(Player entity) {
        new PlayerScout(entity);


    }

    public static PanopticonSettings loadFile() {
        File f = new File(path);
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            saveFile(new PanopticonSettings());
            Logger.info(String.format("Failed to load file %s, made default and trying again.", path));
            return loadFile();
        }

        try {
            String settings = new String(Files.readAllBytes(Paths.get(path)));
            Logger.info(String.format("Loaded file: %s", path));
            return new Gson().fromJson(settings, PanopticonSettings.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void saveFile(PanopticonSettings data) {
        Gson pretty = new GsonBuilder().setPrettyPrinting().create();
        String json = pretty.toJson(data);

        try {
            Logger.info(String.format("Saved file: %s", path));
            Files.write(Paths.get(path), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

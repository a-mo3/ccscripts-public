package com.ccscripts;

import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.ScriptManifest;

import java.awt.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * loads scripts from private sdn & the associated ontology data
 * todo request ontology data
 * todo send analytics
 * todo handle auto updates
 * todo handle command parsing
 * todo load script jar
 */
@ScriptManifest(category = Category.MISC, name = "Privileged", author = "Keith Co", version = 0.0)
public class Loader extends AbstractScript {
    AbstractScript loadedScript;

    @Override
    public void onStart() {
        try {
            loadScript(Path.of(
                    "C:/Users/TopDawg/DreamBot/Scripts/pvmscripts.jar"
            ));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        if (loadedScript != null) loadedScript.onStart();
    }

    @Override
    public int onLoop() {
        if (loadedScript == null) {
            log("No script loaded");
            return 1000;
        }
        log("Running loaded script " + loadedScript.getManifest().name());
        return loadedScript.onLoop();
    }

    void loadScript(Path jarPath) throws Exception {
        URLClassLoader loader = new URLClassLoader(
                new URL[]{jarPath.toUri().toURL()},
                AbstractScript.class.getClassLoader()
        );

        try (JarFile jar = new JarFile(jarPath.toFile())) {
            for (var entry : jar.stream().filter(e -> e.getName().endsWith(".class")).collect(Collectors.toList())) {
                String name = entry.getName().replace('/', '.').replaceAll("\\.class$", "");
                Class<?> type = loader.loadClass(name);

                if (AbstractScript.class.isAssignableFrom(type)
                        && type != AbstractScript.class
                        && !java.lang.reflect.Modifier.isAbstract(type.getModifiers())) {
                    loadedScript = type.asSubclass(AbstractScript.class)
                            .getDeclaredConstructor()
                            .newInstance();
                    return;
                }
            }
        }

        throw new ClassNotFoundException("No AbstractScript implementation found");
    }

    @Override
    public void onExit() {
        loadedScript.onExit();
        loadedScript = null;
    }

    @Override
    public void onPaint(Graphics graphics) {
        if (loadedScript != null) loadedScript.onPaint(graphics);
    }

    @Override
    public void onPaint(Graphics2D graphics) {
        if (loadedScript != null) loadedScript.onPaint(graphics);
    }
}

package org.dreambot;

import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.scripts.*;
import org.dreambot.settings.SettingsLoader;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettings;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * doing this to reduce the amount of commits hashtag has to read recompile and approve
 * only for scripts that have similar codebases
 */
@ScriptManifest(category = Category.MONEYMAKING, name = "FtpScripts", author = "cc", version = 0.0)
public class FtpMain extends AbstractScript {
    PseudoScript activeScript = null;
    Map<String, Supplier<PseudoScript>> scriptMap = new HashMap<>();

    @Override
    public void onStart(String... params) {
        selectScript();
        activeScript.onStart(params);

        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));

        Logger.info("Timings " + ReactionGenerator.getReactionSettings().toString());
    }

    @Override
    public void onStart() {
        selectScript();
        activeScript.onStart();

        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));

        Logger.info("Timings " + ReactionGenerator.getReactionSettings().toString());
    }

    private void selectScript() {
        scriptMap.put("log", FtpLogs::new);
        scriptMap.put("craft", FtpCrafting::new);
        scriptMap.put("bronze", FtpBronze::new);
        scriptMap.put("clay", FtpClay::new);
        scriptMap.put("dwar", FtpChaosDwarves::new);
        scriptMap.put("wizards", FtpWizards::new);

        String scriptName = ScriptManager.getScriptManager().getCurrentScript().getSDNName();

        scriptMap.forEach((name, supp) -> {
            if (scriptName.toLowerCase().contains(name)) {
                activeScript = supp.get();
            }
        });




        if (activeScript == null) activeScript = new FtpCrafting();
//        if (activeScript == null) activeScript = new FtpVault();
        if (activeScript == null) activeScript = new FtpChaosDwarves();
//        if (activeScript == null) activeScript = new FtpWizards();
//        if (activeScript == null) activeScript = new FtpClay();
//        if (activeScript == null) activeScript = new FtpTutorial();
//        if (activeScript == null) activeScript = new FtpLogs();
    }

    @Override
    public int onLoop() {
        return activeScript.onLoop();
    }

    @Override
    public void onPaint(Graphics graphics) {
        if (activeScript != null) activeScript.onPaint(graphics);
    }
}

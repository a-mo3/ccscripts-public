package org.dreambot;


import org.dreambot.analytics.DisclaimerScreen;
import org.dreambot.analytics.DisclaimerState;
import org.dreambot.analytics.impl.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.IronFractal;
import org.dreambot.scripts.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@ScriptManifest(category = Category.MISC, name = "IronScripts", author = "cc", version = 0.1)
public class IronMain extends AbstractScript implements HumanMouseListener {
    PseudoScript activeScript = null;
    Map<String, Supplier<PseudoScript>> scriptMap = new HashMap<>();
    IronFractal tree = new IronFractal(() -> true)
            .setSimpleName("Unavailable");

    @Override
    public void onStart(String... params) {
        selectScript();
        if (activeScript == null) {
            log("Script not available");
            return;
        }
        activeScript.onStart(tree, params);
    }

    @Override
    public void onStart() {
        selectScript();
        activeScript.onStart(tree, new String[]{});
    }

    private void selectScript() {
        scriptMap.put("mining", IronMining::new);
        scriptMap.put("fish", IronFishing::new);
        scriptMap.put("fire", FiremakingScript::new);
        scriptMap.put("beta", IronBuilder::new);
        scriptMap.put("data", IronBuilder::new);

        String scriptName = ScriptManager.getScriptManager().getCurrentScript().getSDNName().toLowerCase();
        scriptMap.forEach((name, supp) -> {
            if (scriptName.contains(name)) {
                activeScript = supp.get();
            }
        });

        if (activeScript == null) {
            Logger.warn("SCRIPT WAS NOT ADDED TO LIST " + scriptName);
        }

        if (ScriptManager.getScriptManager().getCurrentScript().getScriptId() <= 0) {
            // PENIS (so i can ctrl f here)
            if (activeScript == null) activeScript = new TestScript();
        }
    }

    @Override
    public int onLoop() {
        if (!passedDisclaimer) return 300;
        if (activeScript.onLoop()) {
            return 100;
        }
        return tree.run();
    }

    boolean passedDisclaimer = DisclaimerState.loadAccepted();

    @Override
    public void onPaint(Graphics graphics) {
        // scripts paint

        if (!passedDisclaimer)
            DisclaimerScreen.getInstance().draw(graphics, Client.getViewportWidth(), Client.getViewportHeight());
        if (activeScript != null) activeScript.onPaint(graphics);
        // todo global paint
    }

    @Override
    public void onExit() {
        Logger.info("Iron on exit");
        AnalyticsReporter.shutDown();
        if (activeScript != null) activeScript.onExit();
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (!passedDisclaimer) {
            if (DisclaimerScreen.getInstance().isAcceptClicked(e.getPoint())) {
                DisclaimerState.saveAccepted(true);
                passedDisclaimer = true;
                return;
            }
            if (DisclaimerScreen.getInstance().isNevermindClicked(e.getPoint())) {
                ScriptManager.getScriptManager().stop();
            }
        }
    }
}

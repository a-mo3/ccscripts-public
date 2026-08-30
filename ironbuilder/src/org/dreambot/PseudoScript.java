package org.dreambot;


import org.dreambot.analytics.impl.AnalyticsReporter;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.fractals.IronFractal;
import org.dreambot.gui.settings.SettingsRepository;
import org.dreambot.loadouts.behavior.BankingFractal;
import org.dreambot.loadouts.behavior.LampHandler;
import org.dreambot.loadouts.behavior.RestockStackFractal;

import java.awt.*;

public abstract class PseudoScript implements HumanMouseListener {
    public PseudoScript() {
    }

    /**
     * @return string to set settings repo path to, i forgot how this happens in pvm scripts
     */
    protected abstract String scriptName();

    public void onStart(IronFractal tree, String[] args) {
        Logger.info("Init script");
        SettingsRepository.changePath(scriptName());
        tree.addChildren(
                // we need to do this here otherwise setting sfor tut and analytics dont get saved to the script folder
                // bank comes first because restock tasks have their own loadouts
                new AnalyticsReporter().setSimpleName("Analytics (report if you see this)"),
                new LampHandler().setSimpleName("Lamp handler"),
                new BankingFractal().setSimpleName("Banking"),
                new RestockStackFractal().setSimpleName("Restocking"),
                new TutorialTree().setSimpleName("Tut")
        );
        init(tree, args);
        Logger.info("Init paint");
    }


    protected abstract void init(IronFractal tree, String[] args);

    /**
     * i've decided PseudoScripts wont have their own tree, there is one tree for the whole script, PsuedoScripts update it
     * this is to make a standard thing for generating the GUI mostly
     *
     * @return true = return, false = continue
     */
    public boolean onLoop() {
        return false;
    }

    public void onPaint(Graphics g) {
    }

    public void onExit() {
    }

    protected void log(String msg) {
        Logger.log("[" + scriptName() + "]" + " " + msg);
    }
}

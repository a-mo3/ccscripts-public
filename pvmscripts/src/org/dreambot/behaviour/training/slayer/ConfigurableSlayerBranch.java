package org.dreambot.behaviour.training.slayer;

import org.dreambot.api.Client;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.training.slayer.behaviour.GetTaskLeaf;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;

import java.util.function.Supplier;

public class ConfigurableSlayerBranch extends Fractal implements ConfigurableFractal {
    public ConfigurableSlayerBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Gate of War", "Open"));
        Logger.info("registering this!");
        Client.getInstance().addEventListener(this);
        init();
    }


    private void init() {
        this.paintArraySupplier = () -> new String[]{
                "Current task: " + getSlayerTaskKey() + " Remaining: " + getQuantityRemaining(),
                "Level: " + Skills.getRealLevel(Skill.SLAYER)
        };

        addChildren(
                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Hop off 330"),
                new GetTaskLeaf().setSimpleName("Get new task"),
                new Fractal().setSimpleName("Slayer")
                        .setPrependLogic(() -> {
                            SlayerTaskMap.execSlayerTask();
                            return true;
                        })
        );

        // konar volcano webnodes
        EntranceWebNode konarElvator = new EntranceWebNode(new Tile(1311, 3807), "Elevator", "Activate");
        EntranceWebNode konarExit = new EntranceWebNode(new Tile(1311, 10185), "Cave exit", "Exit");
        konarExit.addDualConnections(konarElvator);
        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Rocks", "Climb"));
        WebFinder finder = WebFinder.getWebFinder();
        finder.getNearest(konarElvator, 5).addDualConnections(konarElvator);
        finder.getNearest(konarExit, 10).addDualConnections(konarExit);
        // remove web nodes that try and walk to death plateu for turoths
        // when doing death plateau these nodes are needed so we will only enable them when death plateaus in progress
        Area house = new Area(2810, 3579, 2835, 3550);
        finder.getNodesWithin(15, house.getCenter())
                .stream()
                .filter(x -> x instanceof BasicWebNode)
                .map(x -> (BasicWebNode) x)
                .forEach(x -> x.setValid(() -> PaidQuest.DEATH_PLATEAU.isStarted() && !PaidQuest.DEATH_PLATEAU.isFinished()));
    }

    public static int getSlayerTaskKey() {
        return PlayerSettings.getConfig(395);
    }

    public static int getQuantityRemaining() {
        return PlayerSettings.getConfig(394);
    }

    @Override
    public Object getSettings() {
        return null;
    }

    @Override
    public String settingName() {
        return "slayer";
    }
}

package org.dreambot.behaviour.method.gorillas;

import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.settings.timing.ReactionGenerator;

public class GoToGorillas extends Fractal {
    final Area area;
    Area FULL_GORILLA = new Area(2064, 5689, 2166, 5633);

    Tile[] path = {
            new Tile(2145, 5649, 0),
            new Tile(2148, 5649, 0),
            new Tile(2151, 5650, 0),
            new Tile(2152, 5653, 0),
            new Tile(2152, 5656, 0),
            new Tile(2154, 5658, 0),
            new Tile(2156, 5662, 0),
            new Tile(2155, 5666, 0),
            new Tile(2152, 5668, 0),
            new Tile(2151, 5672, 0),
            new Tile(2151, 5675, 0),
            new Tile(2148, 5677, 0),
            new Tile(2145, 5676, 0),
            new Tile(2141, 5678, 0),
            new Tile(2137, 5678, 0),
            new Tile(2133, 5678, 0),
            new Tile(2129, 5677, 0),
            new Tile(2123, 5676, 0),
            new Tile(2118, 5677, 0),
            new Tile(2113, 5677, 0),
            new Tile(2105, 5676, 0),
            new Tile(2101, 5676, 0),
            new Tile(2099, 5672, 0),
            new Tile(2091, 5676, 0),
            new Tile(2081, 5676, 0),
            new Tile(2077, 5675, 0),
            new Tile(2074, 5671, 0),
            new Tile(2074, 5665, 0),
            new Tile(2075, 5659, 0),
            new Tile(2075, 5652, 0),
            new Tile(2076, 5644, 0)
    };

    public GoToGorillas(Area area) {
        super(() -> !area.contains(Players.getLocal()));
        this.area = area;
        setSimpleName("Go Gorillas");

        WebFinder wf = WebFinder.getWebFinder();
        for (Tile tile : path) {
            wf.addNode(tile);
        }

        // add nodes that istg where in dreambot last update
    }

    public static final Area[] GORILLA_AREAS = {
            new Area(2069, 5654, 2083, 5642), // back of cave
            new Area(2069, 5684, 2084, 5668),
            new Area(2092, 5683, 2106, 5668),
            new Area(2119, 5685, 2135, 5669),
            new Area(2145, 5681, 2164, 5666),
            new Area(2148, 5665, 2165, 5652) // front of cave
    };


    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            Dialog.solve("don't ask");
            return ReactionGenerator.getNormal();
        }

        if (!FULL_GORILLA.contains(Players.getLocal()) && shouldFerox()) {
            log("Go recharge at ferox");
            if (Walking.shouldWalk()) Walking.walk(BankLocation.FEROX_ENCLAVE);
            return ReactionGenerator.getNormal();
        }

        PrayerUtils.disableAll();
        if (!area.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(area);
        }
        return ReactionGenerator.getNormal();
    }

    private boolean shouldFerox() {
        return Skills.getBoostedLevel(Skill.PRAYER) < Skills.getRealLevel(Skill.PRAYER)
                || Skills.getBoostedLevel(Skill.HITPOINTS) < Skills.getRealLevel(Skill.HITPOINTS);
    }
}
